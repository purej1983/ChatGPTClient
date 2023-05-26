package com.thomaslam.chatgptclient.chatecompletion

import androidx.lifecycle.SavedStateHandle
import com.thomaslam.chatgptclient.chatecompletion.domain.ChatCompletionUseCase
import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Message
import com.thomaslam.chatgptclient.chatecompletion.presentation.ConversationViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class ConversationScreenViewModelTest {
    private lateinit var scheduler: TestCoroutineScheduler
    private lateinit var usecase: ChatCompletionUseCase

    private lateinit var viewModel: ConversationViewModel
    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup(){
        scheduler = TestCoroutineScheduler()
        Dispatchers.setMain(StandardTestDispatcher(scheduler))
        MockKAnnotations.init(this, relaxUnitFun = true)
        usecase = mockk(relaxed = true)
        val savedState = SavedStateHandle()
        savedState["chatId"] = 1L
        viewModel = ConversationViewModel(usecase, savedState)
    }

    @Test
    fun testSend() = runTest{
        val id = 1L
        val content = "Test Message"
        val flow = MutableSharedFlow<List<Message>>()
        coEvery { usecase.getConversation(id) } returns flow
        viewModel.send(content)
        flow.emit(listOf(Message(content = content)))
        scheduler.advanceUntilIdle()
        TestCase.assertEquals(viewModel.state.value.messages, listOf(Message(content = content)))
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
    }
}