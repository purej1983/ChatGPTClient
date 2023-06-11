package com.thomaslam.chatgptclient.chatecompletion.test.util

import com.thomaslam.chatgptclient.chatecompletion.domain.use_case.ConfigUseCase
import com.thomaslam.chatgptclient.chatecompletion.domain.use_case.GetConfig
import com.thomaslam.chatgptclient.chatecompletion.domain.use_case.SaveConfig
import com.thomaslam.chatgptclient.chatecompletion.util.ChatGptConfigurationProvider
import com.thomaslam.chatgptclient.chatecompletion.util.ConfigurationProvider
import com.thomaslam.chatgptclient.chatecompletion.util.MockDataCollections
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class ConfigurationProviderTest {
    private lateinit var usecase: ConfigUseCase
    private lateinit var getConfig: GetConfig
    private lateinit var saveConfig: SaveConfig
    private lateinit var configProvider: ConfigurationProvider

    @Before
    fun setup() {
        getConfig = mock(GetConfig::class.java)
        saveConfig = mock(SaveConfig::class.java)
        usecase = ConfigUseCase(
            getConfig = getConfig,
            saveConfig = saveConfig
        )
        val flow = MutableStateFlow(MockDataCollections.config)
        `when`(getConfig.invoke() ).thenReturn(flow)
        configProvider = ChatGptConfigurationProvider(usecase)
    }

    @Test
    fun testValues() = runTest{
        val job = launch {
            assertEquals(MockDataCollections.config.n, configProvider.n)
            assertEquals(MockDataCollections.config.temperature, configProvider.temperature)
            assertEquals(MockDataCollections.config.stream, configProvider.stream)
            assertEquals(MockDataCollections.config.max_tokens, configProvider.maxTokens)
        }
        job.join()
        job.cancel()
    }
}