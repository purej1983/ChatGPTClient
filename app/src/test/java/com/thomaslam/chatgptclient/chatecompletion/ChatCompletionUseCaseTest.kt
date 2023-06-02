package com.thomaslam.chatgptclient.chatecompletion

import com.thomaslam.chatgptclient.chatecompletion.data.repository.FakeChatCompletionRepository
import com.thomaslam.chatgptclient.chatecompletion.domain.ChatCompletionUseCase
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Chat
import com.thomaslam.chatgptclient.chatecompletion.domain.model.ChatState
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatCompletionRepository
import com.thomaslam.chatgptclient.chatecompletion.domain.util.Resource
import com.thomaslam.chatgptclient.chatecompletion.util.MockDataCollections
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockkObject
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.argumentCaptor

class ChatCompletionUseCaseTest {
    private lateinit var repository: ChatCompletionRepository
    private lateinit var usecase: ChatCompletionUseCase

    @Before
    fun setup() {
        repository = Mockito.spy(FakeChatCompletionRepository())
        usecase = ChatCompletionUseCase(repository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetChartsAndNewChat() = runTest{
        val values = mutableListOf<List<Chat>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            usecase.getChats().toList(values)
        }
        (repository as FakeChatCompletionRepository).emitChatChange()
        val beforeInsert = values[0]
        assertEquals(2, beforeInsert.size)
        val newId = usecase.newChat()
        val afterInsert = values[1]
        assertEquals(3, newId)
        assertEquals(3, afterInsert.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testUpdateLastMessage() = runTest {
        val values = mutableListOf<List<Chat>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            usecase.getChats().toList(values)
        }
        (repository as FakeChatCompletionRepository).emitChatChange()
        val chatId = 2L
        val beforeUpdateChatList = values[0]
        val testItemBeforeUpdate = beforeUpdateChatList.first { it.id == 2L}
        assertEquals(2, beforeUpdateChatList.size)
        assertEquals(MockDataCollections.chats[1].lastUserMessage, testItemBeforeUpdate.lastUserMessage)

        val newContent = "How to write a unit test?"
        usecase.updateLastUserMessage(chatId = chatId, content = newContent)
        val afterUpdateChatList = values[1]
        val testItemAfterUpdate = afterUpdateChatList.first { it.id == 2L}
        assertEquals(2, afterUpdateChatList.size)
        assertEquals(newContent, testItemAfterUpdate.lastUserMessage)
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testChatState() = runTest {
        val values = mutableListOf<List<Chat>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            usecase.getChats().toList(values)
        }
        (repository as FakeChatCompletionRepository).emitChatChange()
        val chatId = 2L
        val beforeUpdateChatList = values[0]
        val testItemBeforeUpdate = beforeUpdateChatList.first { it.id == 2L}
        assertEquals(2, beforeUpdateChatList.size)
        assertEquals(MockDataCollections.chats[1].state, testItemBeforeUpdate.state)


        usecase.resetChatState(chatId)
        val afterUpdateChatList = values[1]
        val testItemAfterUpdate = afterUpdateChatList.first { it.id == 2L}
        assertEquals(2, afterUpdateChatList.size)
        assertEquals(ChatState.IDLE, testItemAfterUpdate.state)
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCreateChatCompletion(){
        val messages = listOf(
            MockDataCollections.userMessage1
        )
        runTest {

            val chatId = 1L
            val values = mutableListOf<Resource<Message>>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                usecase.createCompletion(chatId,  messages).toList(values)
            }
            val loading = values[0]
            assert(loading is Resource.Loading)

            val success = values[1]
            assert(success is Resource.Success)
            val createParameterCaptor = argumentCaptor<List<Message>>()

            val saveLocalFirstParameterCaptor = argumentCaptor<Long>()
            val saveLocalSecondParameterCaptor = argumentCaptor<Message>()
            verify(repository, times(1)).create(createParameterCaptor.capture())
            verify(repository, times(1)).saveLocalMessage(saveLocalFirstParameterCaptor.capture(), saveLocalSecondParameterCaptor.capture())
            assert(createParameterCaptor.lastValue === messages)
            assert(saveLocalFirstParameterCaptor.lastValue == chatId)


            val assistantMessage = success.data
            assertNotNull(assistantMessage)
            assert(saveLocalSecondParameterCaptor.lastValue === assistantMessage)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCreateChatCompletionWithFailure(){
        val messages = listOf(
            MockDataCollections.userMessage1
        )
        runTest {

            val chatId = 1L
            val values = mutableListOf<Resource<Message>>()
            mockkObject(repository)
            coEvery { repository.create(any()) } returns Resource.Error(message = "Error")
            coEvery { repository.saveLocalMessage(any(), any()) } just Runs

            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                usecase.createCompletion(chatId,  messages).toList(values)
            }
            val loading = values[0]
            assert(loading is Resource.Loading)

            val error = values[1]
            assert(error is Resource.Error)
            val data = error.data
            val errorMessage = error.message
            assertNull(data)
            assertEquals("Error", errorMessage)

        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getConversation() = runTest {
        val chatId = 1L
        val values = mutableListOf<List<Message>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            usecase.getConversation(chatId).toList(values)
        }
        (repository as FakeChatCompletionRepository).emitMessageChange()
        val messageList = values[0]
        assertEquals(2, messageList.size)
        assertEquals(MockDataCollections.userMessage1, messageList[0])
        assertEquals(MockDataCollections.assistantMessage1, messageList[1])
    }
}