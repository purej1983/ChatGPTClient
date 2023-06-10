package com.thomaslam.chatgptclient.chatecompletion.test.domain

import com.thomaslam.chatgptclient.chatecompletion.data.repository.FakeChatGptConfigRepository
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Config
import com.thomaslam.chatgptclient.chatecompletion.domain.use_case.GetConfig
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
class ChatGptGetConfigUseCaseTest {
    private lateinit var getConfig: GetConfig
    private lateinit var repository: FakeChatGptConfigRepository

    @Before
    fun setup() {
        repository = FakeChatGptConfigRepository()
        getConfig = GetConfig(repository)
    }

    @Test
    fun testInvoke() = runTest {
        val values = mutableListOf<Config>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getConfig().toList(values)
        }
        repository.emitConfigChange()
        val actualConfig = values[0]
        actualConfig.apply {
            TestCase.assertEquals(MockDataCollections.config.n, this.n)
            TestCase.assertEquals(MockDataCollections.config.temperature, this.temperature)
            TestCase.assertEquals(MockDataCollections.config.stream, this.stream)
            TestCase.assertEquals(MockDataCollections.config.max_tokens, this.max_tokens)
        }
    }
}