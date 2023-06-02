package com.thomaslam.chatgptclient.chatecompletion

import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.ChatGptDao
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.FakeChatGptDao
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.FakeChatCompletionService
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.ChatCompletionService
import com.thomaslam.chatgptclient.chatecompletion.data.repository.ChatCompletionRepositoryImpl
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Chat
import com.thomaslam.chatgptclient.chatecompletion.domain.model.ChatState
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatCompletionRepository
import com.thomaslam.chatgptclient.chatecompletion.domain.util.Resource
import com.thomaslam.chatgptclient.chatecompletion.util.MockDataCollections
import io.mockk.coEvery
import io.mockk.mockkObject
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ChatCompletionRepositoryTest {
    private lateinit var repository: ChatCompletionRepository
    private lateinit var dao: ChatGptDao
    private lateinit var api: ChatCompletionService

    @Before
    fun setup() {
        dao = FakeChatGptDao()
        api = FakeChatCompletionService()
        repository = ChatCompletionRepositoryImpl(dao, api)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetChats() =
        runTest {
            val values = mutableListOf<List<Chat>>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                repository.getChats().toList(values)
            }
            (dao as FakeChatGptDao).emitChatChange()
            val actual = values[0]
            assertEquals(FakeChatGptDao.mockChats.size, actual.size)
            assert(actual.size == FakeChatGptDao.mockChats.size)
            actual.forEachIndexed {
                index, chat ->
                    run {
                        val expectChat = FakeChatGptDao.mockChats[index]
                        assert(chat.id == expectChat.id)
                        assert(chat.lastUserMessage == expectChat.lastUserMessage)
                    }
            }
        }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testNewChats() =
        runTest {
            val values = mutableListOf<List<Chat>>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                repository.getChats().toList(values)
            }
            (dao as FakeChatGptDao).emitChatChange()
            val beforeInsert = values[0]
            assert(beforeInsert.size == 2)
            val newId = repository.newChat()
            assert(newId == 3L)
            val afterInsert = values[1]
            assert(afterInsert.size == 3)
            val lastItem = afterInsert.last()
            assert(lastItem.id == 3L)
            assert(lastItem.lastUserMessage == "New Chat")
        }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testUpdateLastUserMessage() {
        runTest {

            val values = mutableListOf<List<Chat>>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                repository.getChats().toList(values)
            }

            val id = 2L
            val updateMessage = "Test Update Message"
            repository.updateLastUserMessage(id, updateMessage)
            val chats = values[0]
            val filtered = chats.first { it.id == id }
            assert(filtered.id == id)
            assert(filtered.lastUserMessage == updateMessage)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetConversation() {
        runTest {
            val values = mutableListOf<List<Message>>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                repository.getConversation(1L).toList(values)
            }
            (dao as FakeChatGptDao).emitConversationChange()
            val actual = values[0]
            assert(actual.size == FakeChatGptDao.mockConversations.size)
            actual.forEachIndexed {
                    index, conversation ->
                run {
                    val expectConversation = FakeChatGptDao.mockConversations[index]
                    assert(conversation.role == expectConversation.role)
                    assert(conversation.content == expectConversation.content)
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSaveLocalMessage() {
        runTest {
            val id = 2L
            val values = mutableListOf<List<Message>>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                repository.getConversation(id).toList(values)
            }

            val role = "user"
            val content = "How's attraction in Birmingham"
            (dao as FakeChatGptDao).emitConversationChange()
            val beforeInsert = values[0]
            assert(beforeInsert.size == 2)
            repository.saveLocalMessage(id, Message(role, content))
            val afterInsert = values[1]
            assert(afterInsert.size == 3)
            assert(afterInsert.last().role == role)
            assert(afterInsert.last().content == content)
        }
    }

    @Test
    fun testCreateCompletion() {
        runTest {
            val messages = listOf(
                MockDataCollections.userMessage1,
                MockDataCollections.assistantMessage1,
                MockDataCollections.userMessage2
            )

            val success = repository.create(messages)
            assert(success is Resource.Success)
            val response = success.data
            assertNotNull(response)
            assert(response?.role == MockDataCollections.assistantMessage2.role)
            assert(response?.content == MockDataCollections.assistantMessage2.content)
        }
    }

    @Test
    fun testCreateCompletionWithHttpException() {
        runTest {
            val messages = listOf(
                MockDataCollections.userMessage1,
                MockDataCollections.assistantMessage1,
                MockDataCollections.userMessage2
            )
            mockkObject(api)
            coEvery { api.createChatCompletion(any()) } throws HttpException(
                Response.error<Any>(500, "Error".toResponseBody("plain/text".toMediaTypeOrNull()))
            )

            val error = repository.create(messages)
            assert(error is Resource.Error)
            val response = error.data
            assertNull(response)
            val message = error.message
            assertEquals(message, "Oops, something went wrong!")
        }
    }

    @Test
    fun testCreateCompletionWithIOException() {
        runTest {
            val messages = listOf(
                MockDataCollections.userMessage1,
                MockDataCollections.assistantMessage1,
                MockDataCollections.userMessage2
            )
            mockkObject(api)
            coEvery { api.createChatCompletion(any()) } throws IOException("Not Network")

            val error = repository.create(messages)
            assert(error is Resource.Error)
            val response = error.data
            assertNull(response)
            val message = error.message
            assertEquals(message, "Couldn't reach server, check your internet connection.")
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testUpdateChatState() {
        runTest {
            val values = mutableListOf<List<Chat>>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                repository.getChats().toList(values)
            }

            val id = 2L
            val newState = ChatState.ERROR
            repository.updateChatState(id, newState)
            val chats = values[0]
            val filtered = chats.first { it.id == id }
            assert(filtered.id == id)
            assert(filtered.state == newState)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun resetChatState() {
        runTest {
            val values = mutableListOf<List<Chat>>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                repository.getChats().toList(values)
            }

            val id = 2L
            repository.resetChatState(id)
            val chats = values[0]
            val filtered = chats.first { it.id == id }
            assert(filtered.id == id)
            assert(filtered.state == ChatState.IDLE)
        }
    }
}
