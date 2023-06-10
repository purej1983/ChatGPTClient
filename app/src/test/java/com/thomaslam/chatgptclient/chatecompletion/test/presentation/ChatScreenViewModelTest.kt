package com.thomaslam.chatgptclient.chatecompletion.test.presentation

import app.cash.turbine.test
import com.thomaslam.chatgptclient.chatecompletion.domain.ChatCompletionUseCase
import com.thomaslam.chatgptclient.chatecompletion.presentation.ChatViewModel
import com.thomaslam.chatgptclient.chatecompletion.util.MockDataCollections
import io.mockk.MockKAnnotations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

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
        usecase = mock(ChatCompletionUseCase::class.java)
        val flow = MutableStateFlow(MockDataCollections.chats)
        `when`(usecase.getChats()).thenReturn(flow)
        viewModel = ChatViewModel(usecase)

    }

    @Test
    fun testNewChat() = runTest {
        val id = 1L
        `when`(usecase.newChat()).thenReturn(id)
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

    @Test
    fun testGoToConfig() = runTest {
        val job = launch {
            viewModel.eventFlow.test {
                viewModel.goToConfig()
                val emission = awaitItem()
                assert(emission is ChatViewModel.UiEvent.NavigateToConfig)
                cancelAndConsumeRemainingEvents()
            }
        }
        job.join()
        job.cancel()
    }

    @Test
    fun testGetChats() = runTest{
        val job = launch {
            assertEquals(2, viewModel.state.value.chats.size)
            assertEquals(MockDataCollections.chats[0], viewModel.state.value.chats[0])
            assertEquals(MockDataCollections.chats[1], viewModel.state.value.chats[1])
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