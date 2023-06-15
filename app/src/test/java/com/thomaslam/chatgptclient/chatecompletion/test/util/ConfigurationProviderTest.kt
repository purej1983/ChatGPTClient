package com.thomaslam.chatgptclient.chatecompletion.test.util

import com.thomaslam.chatgptclient.chatecompletion.domain.use_case.ConfigUseCase
import com.thomaslam.chatgptclient.chatecompletion.domain.use_case.GetConfig
import com.thomaslam.chatgptclient.chatecompletion.domain.use_case.SaveConfig
import com.thomaslam.chatgptclient.chatecompletion.util.ChatGptConfigurationProvider
import com.thomaslam.chatgptclient.chatecompletion.util.ConfigurationProvider
import com.thomaslam.chatgptclient.chatecompletion.util.MockDataCollections
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
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class ConfigurationProviderTest {
    private lateinit var scheduler: TestCoroutineScheduler
    private lateinit var usecase: ConfigUseCase
    private lateinit var getConfig: GetConfig
    private lateinit var saveConfig: SaveConfig
    private lateinit var configProvider: ConfigurationProvider

    @Before
    fun setup() {
        scheduler = TestCoroutineScheduler()
        Dispatchers.setMain(StandardTestDispatcher(scheduler))
        getConfig = mock(GetConfig::class.java)
        saveConfig = mock(SaveConfig::class.java)
        val flow = MutableStateFlow(MockDataCollections.config)
        `when`(getConfig.invoke() ).thenReturn(flow)
        usecase = ConfigUseCase(
            getConfig = getConfig,
            saveConfig = saveConfig
        )

        configProvider = ChatGptConfigurationProvider(usecase)
    }

    @Test
    fun testValues() = runTest{
        val job = launch {
            advanceUntilIdle()
            assertEquals(MockDataCollections.config.n, configProvider.n)
            assertEquals(MockDataCollections.config.temperature, configProvider.temperature)
            assertEquals(MockDataCollections.config.stream, configProvider.stream)
            assertEquals(MockDataCollections.config.max_tokens, configProvider.maxTokens)
        }
        job.join()
        job.cancel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
    }
}