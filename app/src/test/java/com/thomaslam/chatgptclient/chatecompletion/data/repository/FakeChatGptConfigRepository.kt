package com.thomaslam.chatgptclient.chatecompletion.data.repository

import com.thomaslam.chatgptclient.chatecompletion.domain.model.Config
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatGptConfigRepository
import com.thomaslam.chatgptclient.chatecompletion.util.MockDataCollections
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.jetbrains.annotations.VisibleForTesting

class FakeChatGptConfigRepository: ChatGptConfigRepository {
    private val configFlow = MutableSharedFlow<Config>()
    private var _config = MockDataCollections.config
    override fun getConfig(): Flow<Config> = configFlow

    override suspend fun saveConfig(config: Config) {
        _config = config
        emitConfigChange()
    }

    @VisibleForTesting
    suspend fun emitConfigChange() {
        configFlow.emit(_config)
    }
}