package com.thomaslam.chatgptclient

import com.thomaslam.chatgptclient.chatecompletion.data.repository.FakeChatCompletionRepository
import com.thomaslam.chatgptclient.chatecompletion.domain.ChatCompletionUseCase
import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatCompletionRepository
import com.thomaslam.chatgptclient.chatecompletion.util.MockDataCollections
import kotlinx.coroutines.runBlocking
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
        runBlocking {
            val chatId = 1L
            val assistantMessage = usecase.createCompletion(chatId,  messages)
            val createParameterCaptor = argumentCaptor<List<Message>>()

            val saveLocalFirstParameterCaptor = argumentCaptor<Long>()
            val saveLocalSecondParameterCaptor = argumentCaptor<Message>()
            verify(repository, times(1)).create(createParameterCaptor.capture())
            verify(repository, times(1)).saveLocalMessage(saveLocalFirstParameterCaptor.capture(), saveLocalSecondParameterCaptor.capture())
            assert(createParameterCaptor.lastValue === messages)
            assert(saveLocalFirstParameterCaptor.lastValue === chatId)
            assert(saveLocalSecondParameterCaptor.lastValue === assistantMessage)
        }
    }
}