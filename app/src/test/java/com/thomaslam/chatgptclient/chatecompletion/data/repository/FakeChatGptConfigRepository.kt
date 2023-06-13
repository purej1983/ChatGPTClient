package com.thomaslam.chatgptclient.chatecompletion.data.repository

import com.thomaslam.chatgptclient.chatecompletion.domain.model.Config
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatGptConfigRepository
import com.thomaslam.chatgptclient.chatecompletion.util.MockDataCollections
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeChatGptConfigRepository: ChatGptConfigRepository {
    private var _config = MockDataCollections.config
    private val configFlow = MutableStateFlow(_config)

    override fun getConfig(): Flow<Config> = configFlow

    override suspend fun saveConfig(config: Config) {
        _config = config
        configFlow.value = _config
    }
}