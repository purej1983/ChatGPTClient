package com.thomaslam.chatgptclient.chatecompletion

import app.cash.turbine.test
import com.thomaslam.chatgptclient.chatecompletion.domain.ChatCompletionUseCase
import com.thomaslam.chatgptclient.chatecompletion.presentation.ChatViewModel
import io.mockk.MockKAnnotations
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import io.mockk.coEvery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler

class ChatScreenViewModelTest {
    private lateinit var scheduler: TestCoroutineScheduler
    private lateinit var usecase: ChatCompletionUseCase

    private lateinit var viewModel: ChatViewModel
    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup(){
        scheduler = TestCoroutineScheduler()
        Dispatchers.setMain(StandardTestDispatcher(scheduler))
        MockKAnnotations.init(this, relaxUnitFun = true)
        usecase = mockk(relaxed = true)
        viewModel = ChatViewModel(usecase)

    }

    @Test
    fun testNewChat() = runTest {
        val id = 1L
        coEvery { usecase.newChat() } returns id
        val job = launch {
            viewModel.eventFlow.test {
                viewModel.newChat()
                val emission = awaitItem()
                assert(emission is ChatViewModel.UiEvent.NavigateToChat && emission.id == id)
                cancelAndConsumeRemainingEvents()
            }
        }
        job.join()
        job.cancel()
    }

    @Test
    fun testGoToChat() = runTest {
        val id = 2L
        val job = launch {
            viewModel.eventFlow.test {
                viewModel.goToChat(id)
                val emission = awaitItem()
                assert(emission is ChatViewModel.UiEvent.NavigateToChat && emission.id == id)
                cancelAndConsumeRemainingEvents()
            }
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