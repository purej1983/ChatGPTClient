package com.thomaslam.chatgptclient.chatecompletion.test.domain

import com.thomaslam.chatgptclient.chatecompletion.data.repository.FakeChatGptConfigRepository
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Config
import com.thomaslam.chatgptclient.chatecompletion.domain.use_case.ConfigUseCase
import com.thomaslam.chatgptclient.chatecompletion.domain.use_case.GetConfig
import com.thomaslam.chatgptclient.chatecompletion.domain.use_case.SaveConfig
import com.thomaslam.chatgptclient.chatecompletion.util.MockDataCollections
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatGptSaveConfigUseCaseTest {
    private lateinit var getConfig: GetConfig
    private lateinit var saveConfig: SaveConfig
    private lateinit var repository: FakeChatGptConfigRepository
    private lateinit var useCase: ConfigUseCase

    @Before
    fun setup() {
        repository = FakeChatGptConfigRepository()
        getConfig = GetConfig(repository)
        saveConfig = SaveConfig(repository)
        useCase = ConfigUseCase(
            getConfig = getConfig,
            saveConfig = saveConfig
        )
    }

    @Test
    fun testInvoke() = runTest {

        val values = mutableListOf<Config>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            useCase.getConfig().toList(values)
        }
        repository.emitConfigChange()

        val beforeConfig = values[0]
        beforeConfig.apply {
            TestCase.assertEquals(MockDataCollections.config.n, this.n)
            TestCase.assertEquals(MockDataCollections.config.temperature, this.temperature)
            TestCase.assertEquals(MockDataCollections.config.max_tokens, this.max_tokens)
        }
        useCase.saveConfig(
            Config(
                n = 1,
                temperature = 1f,
                stream = false,
                max_tokens = 150
            )
        )
        val afterConfig = values[1]
        afterConfig.apply {
            TestCase.assertEquals(1, this.n)
            TestCase.assertEquals(1f, this.temperature)
            TestCase.assertEquals(false, this.stream)
            TestCase.assertEquals(150, this.max_tokens)
        }

    }
}