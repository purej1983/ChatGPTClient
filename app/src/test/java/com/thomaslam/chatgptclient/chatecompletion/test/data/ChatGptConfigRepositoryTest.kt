package com.thomaslam.chatgptclient.chatecompletion.test.data

import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.FakeChatGptDao
import com.thomaslam.chatgptclient.chatecompletion.data.repository.ChatGptConfigRepositoryImpl
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Config
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatGptConfigRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatGptConfigRepositoryTest {
    private lateinit var repository: ChatGptConfigRepository
    private lateinit var dao: FakeChatGptDao
    @Before
    fun setup() {
        dao = FakeChatGptDao()
        repository = ChatGptConfigRepositoryImpl(dao)
    }

    @Test
    fun testGetConfig() = runTest {
        val values = mutableListOf<Config>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.getConfig().toList(values)
        }
        dao.emitConfigChange()
        val actualConfig = values[0]
        actualConfig.apply {
            assertEquals(FakeChatGptDao.mockConfig.n, this.n)
            assertEquals(FakeChatGptDao.mockConfig.temperature, this.temperature)
            assertEquals(FakeChatGptDao.mockConfig.stream, this.stream)
            assertEquals(FakeChatGptDao.mockConfig.max_tokens, this.max_tokens)
        }
    }

    @Test
    fun testSaveConfig() = runTest {
        val values = mutableListOf<Config>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.getConfig().toList(values)
        }
        dao.emitConfigChange()
        val beforeConfig = values[0]
        beforeConfig.apply {
            assertEquals(FakeChatGptDao.mockConfig.n, this.n)
            assertEquals(FakeChatGptDao.mockConfig.temperature, this.temperature)
            assertEquals(FakeChatGptDao.mockConfig.stream, this.stream)
            assertEquals(FakeChatGptDao.mockConfig.max_tokens, this.max_tokens)
        }
        repository.saveConfig(
            Config(
            n = 1,
            temperature = 1f,
            stream = false,
            max_tokens = 150
            )
        )
        val afterConfig = values[1]
        afterConfig.apply {
            assertEquals(1, this.n)
            assertEquals(1f, this.temperature)
            assertEquals(false, this.stream)
            assertEquals(150, this.max_tokens)
        }
    }
}