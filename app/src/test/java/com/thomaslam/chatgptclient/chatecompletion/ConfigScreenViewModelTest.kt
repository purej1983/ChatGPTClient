package com.thomaslam.chatgptclient.chatecompletion

import com.thomaslam.chatgptclient.chatecompletion.domain.model.Config
import com.thomaslam.chatgptclient.chatecompletion.domain.use_case.ConfigUseCase
import com.thomaslam.chatgptclient.chatecompletion.domain.use_case.GetConfig
import com.thomaslam.chatgptclient.chatecompletion.domain.use_case.SaveConfig
import com.thomaslam.chatgptclient.chatecompletion.presentation.ConfigScreenEvent
import com.thomaslam.chatgptclient.chatecompletion.presentation.ConfigScreenViewModel
import com.thomaslam.chatgptclient.chatecompletion.util.MockDataCollections
import io.mockk.MockKAnnotations
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class ConfigScreenViewModelTest {
    private lateinit var scheduler: TestCoroutineScheduler
    private lateinit var usecase: ConfigUseCase
    private lateinit var getConfig: GetConfig
    private lateinit var saveConfig: SaveConfig

    private lateinit var viewModel: ConfigScreenViewModel

    @Before
    fun setup() {
        scheduler = TestCoroutineScheduler()
        Dispatchers.setMain(StandardTestDispatcher(scheduler))
        MockKAnnotations.init(this, relaxUnitFun = true)
        getConfig = mock(GetConfig::class.java)
        saveConfig = mock(SaveConfig::class.java)
        usecase = ConfigUseCase(
            getConfig = getConfig,
            saveConfig = saveConfig
        )
        val flow = MutableStateFlow(MockDataCollections.config)
        `when`(getConfig.invoke() ).thenReturn(flow)
        viewModel = ConfigScreenViewModel(usecase)
    }

    @Test
    fun testNValueChangeEvent() = runTest{

        val job = launch {
            viewModel.onEvent(ConfigScreenEvent.NValueChange(1))
            assertEquals(1, viewModel.state.value.config?.n)
            viewModel.onEvent(ConfigScreenEvent.NValueChange(2))
            assertEquals(2, viewModel.state.value.config?.n)
        }
        job.join()
        job.cancel()

    }

    @Test
    fun testTemperatureValueChangeEvent() = runTest{
        val job = launch {
            viewModel.onEvent(ConfigScreenEvent.TemperatureValueChange(0.8f))
            assertEquals(0.8f, viewModel.state.value.config?.temperature)
            viewModel.onEvent(ConfigScreenEvent.TemperatureValueChange(1.6f))
            assertEquals(1.6f, viewModel.state.value.config?.temperature)
        }
        job.join()
        job.cancel()
    }

    @Test
    fun testStreamValueChangeEvent() = runTest{
        val job = launch {
            viewModel.onEvent(ConfigScreenEvent.StreamValueChange(true))
            assertEquals(true, viewModel.state.value.config?.stream)
            viewModel.onEvent(ConfigScreenEvent.StreamValueChange(false))
            assertEquals(false, viewModel.state.value.config?.stream)
        }
        job.join()
        job.cancel()
    }

    @Test
    fun testMaxTokensValueChangeEvent() = runTest{
        val job = launch {
            viewModel.onEvent(ConfigScreenEvent.MaxTokensValueChange(100))
            assertEquals(100, viewModel.state.value.config?.max_tokens)
            viewModel.onEvent(ConfigScreenEvent.MaxTokensValueChange(150))
            assertEquals(150, viewModel.state.value.config?.max_tokens)
        }
        job.join()
        job.cancel()
    }

    @Test
    fun testSaveConfig() = runTest{
        val job = launch {
            viewModel.onEvent(ConfigScreenEvent.SaveConfig)
        }
        job.join()
        val configParameterCaptor = argumentCaptor<Config>()
        verify(saveConfig, times(1)).invoke(configParameterCaptor.capture())
        assertEquals(MockDataCollections.config.n, configParameterCaptor.lastValue.n)
        assertEquals(MockDataCollections.config.temperature, configParameterCaptor.lastValue.temperature)
        assertEquals(MockDataCollections.config.stream, configParameterCaptor.lastValue.stream)
        assertEquals(MockDataCollections.config.max_tokens, configParameterCaptor.lastValue.max_tokens)
        job.cancel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
    }
}