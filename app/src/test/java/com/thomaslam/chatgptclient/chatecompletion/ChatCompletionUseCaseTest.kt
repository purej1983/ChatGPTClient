package com.thomaslam.chatgptclient.chatecompletion

import com.thomaslam.chatgptclient.chatecompletion.data.repository.FakeChatCompletionRepository
import com.thomaslam.chatgptclient.chatecompletion.domain.ChatCompletionUseCase
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatCompletionRepository
import com.thomaslam.chatgptclient.chatecompletion.domain.util.Resource
import com.thomaslam.chatgptclient.chatecompletion.util.MockDataCollections
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
    fun Setup() {
        repository = Mockito.spy(FakeChatCompletionRepository())
        usecase = ChatCompletionUseCase(repository)
    }

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
            assert(saveLocalFirstParameterCaptor.lastValue === chatId)


            val assistantMessage = success.data
            assertNotNull(assistantMessage)
            assert(saveLocalSecondParameterCaptor.lastValue === assistantMessage)
        }
    }
}