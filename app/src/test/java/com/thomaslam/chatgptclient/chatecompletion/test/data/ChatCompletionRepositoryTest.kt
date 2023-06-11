package com.thomaslam.chatgptclient.chatecompletion.test.data

import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.FakeChatGptDao
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.ChatCompletionService
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto.ChatCompletionChunk
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
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ChatCompletionRepositoryTest {
    private lateinit var mockServer: MockWebServer
    private lateinit var repository: ChatCompletionRepository
    private lateinit var dao: FakeChatGptDao
    private lateinit var api: ChatCompletionService

    @Before
    fun setup() {
        mockServer = MockWebServer()
        mockServer.start(8080)

        dao = FakeChatGptDao()
        api = mock(ChatCompletionService::class.java)
        repository = ChatCompletionRepositoryImpl(dao, api)

    }


    @Test
    fun testGetChats() =
        runTest {
            val values = mutableListOf<List<Chat>>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                repository.getChats().toList(values)
            }
            dao.emitChatChange()
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



    @Test
    fun testNewChats() =
        runTest {
            val values = mutableListOf<List<Chat>>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                repository.getChats().toList(values)
            }
            dao.emitChatChange()
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


    @Test
    fun testGetConversation() {
        runTest {
            val values = mutableListOf<List<Message>>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                repository.getConversation(1L).toList(values)
            }
            dao.emitConversationChange()
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


    @Test
    fun testSaveLocalMessage() {
        runTest {
            val chatId = 2L
            val values = mutableListOf<List<Message>>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                repository.getConversation(chatId).toList(values)
            }

            val role = "user"
            val content = "How's attraction in Birmingham"
            dao.emitConversationChange()
            val beforeInsert = values[0]
            assertEquals(2, beforeInsert.size)

            val conversationId:Long = repository.saveLocalMessage(chatId, Message(role, content))
            val afterInsert = values[1]
            assertEquals(3,afterInsert.size)
            assertEquals(role,afterInsert.last().role)
            assertEquals(content,afterInsert.last().content)

            val newRecordId: Long = repository.saveLocalMessage(chatId, Message(role, "updatedContent"), conversationId)
            val updatedRecordList = values[2]
            assertEquals(3,updatedRecordList.size)
            assertEquals(role,updatedRecordList.last().role)
            assertEquals("updatedContent", updatedRecordList.last().content)
            assertEquals(conversationId, newRecordId)
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
            `when`(api.createChatCompletion(any())).thenReturn(MockDataCollections.mockReponse)

            val success = repository.createChatCompletion(messages)
            assert(success is Resource.Success)
            val response = success.data
            assertNotNull(response)
            assert(response?.role == MockDataCollections.assistantMessage2.role)
            assert(response?.content == MockDataCollections.assistantMessage2.content)
        }
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun testStreamCompletion() {
        runTest {
            val mockedCall = mock(Call::class.java) as Call<ResponseBody>
            val messages = listOf(
                MockDataCollections.userMessage1,
                MockDataCollections.assistantMessage1,
                MockDataCollections.userMessage2
            )

            `when`(api.createStreamChatCompletion(any())).thenReturn(mockedCall)

            `when`(mockedCall.enqueue(any()))
                .thenAnswer {
                    invocation ->
                    val callback = invocation.arguments[0] as Callback<ResponseBody>
                    callback.onResponse(mockedCall, Response.success(MockDataCollections.mockChunkRawData1.toResponseBody("text/event-stream".toMediaTypeOrNull())))
                    Thread.sleep(500)
                    callback.onResponse(mockedCall, Response.success(MockDataCollections.mockChunkRawData2.toResponseBody("text/event-stream".toMediaTypeOrNull())))
                    Thread.sleep(500)
                    callback.onResponse(mockedCall, Response.success(MockDataCollections.mockChunkRawData3.toResponseBody("text/event-stream".toMediaTypeOrNull())))
                    Thread.sleep(500)
                    callback.onResponse(mockedCall, Response.success(MockDataCollections.mockChunkRawData4.toResponseBody("text/event-stream".toMediaTypeOrNull())))
                }

            val values = mutableListOf<ChatCompletionChunk>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                repository.streamChatCompletion(messages).toList(values)
            }
            assertEquals(4, values.size)
            val firstChunk = values[0]
            assertEquals("assistant", firstChunk.choices[0].message.role)
            assertNull(firstChunk.choices[0].message.content)

            val secondChunk = values[1]
            assertEquals("Sure", secondChunk.choices[0].message.content)
            assertNull(secondChunk.choices[0].message.role)

            val thirdChunk = values[2]
            assertEquals("!", thirdChunk.choices[0].message.content)
            assertNull(thirdChunk.choices[0].message.role)

            val forthChunk = values[3]
            assertEquals(" Here", forthChunk.choices[0].message.content)
            assertNull(forthChunk.choices[0].message.role)

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

            val error = repository.createChatCompletion(messages)
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

            val error = repository.createChatCompletion(messages)
            assert(error is Resource.Error)
            val response = error.data
            assertNull(response)
            val message = error.message
            assertEquals(message, "Couldn't reach server, check your internet connection.")
        }
    }


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

    @After
    fun tearDown() {
        mockServer.shutdown()
    }
}
