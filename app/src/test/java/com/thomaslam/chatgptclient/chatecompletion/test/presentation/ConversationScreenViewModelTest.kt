package com.thomaslam.chatgptclient.chatecompletion.test.presentation

import androidx.lifecycle.SavedStateHandle
import com.thomaslam.chatgptclient.chatecompletion.domain.ChatCompletionUseCase
import com.thomaslam.chatgptclient.chatecompletion.domain.model.ConversationWithSelectMessage
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Message
import com.thomaslam.chatgptclient.chatecompletion.presentation.ConversationViewModel
import io.mockk.MockKAnnotations
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class ConversationScreenViewModelTest {
    private lateinit var scheduler: TestCoroutineScheduler
    private lateinit var usecase: ChatCompletionUseCase

    private lateinit var viewModel: ConversationViewModel
    val flow = MutableStateFlow(listOf<ConversationWithSelectMessage>())
    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup(){
        scheduler = TestCoroutineScheduler()
        Dispatchers.setMain(StandardTestDispatcher(scheduler))
        MockKAnnotations.init(this, relaxUnitFun = true)
        usecase = mock(ChatCompletionUseCase::class.java)

        `when`(usecase.getConversation(anyLong())).thenReturn(flow)

        val savedState = SavedStateHandle()
        savedState["chatId"] = 1L
        viewModel = ConversationViewModel(usecase, savedState)

    }

    @Test
    fun testSend() = runTest{
        val id = 1L
        val content = "Test Message"


        val job = launch {
            viewModel.send(content)
            val newList = listOf(
                ConversationWithSelectMessage(
                    conversationId = id,
                    selectMessage =Message(content = content),
                    selectMessageIdx = 0,
                    totalMessage = 1
                ))
            flow.value = newList
            advanceUntilIdle()
            assertEquals(newList, viewModel.state.value.messages)
        }
        job.join()
        job.cancel()

    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
    }
}