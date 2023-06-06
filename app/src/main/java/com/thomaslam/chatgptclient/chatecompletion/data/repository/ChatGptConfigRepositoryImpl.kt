package com.thomaslam.chatgptclient.chatecompletion.data.repository

import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.ChatGptDao
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ChatGptConfigEntity
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Config
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatGptConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChatGptConfigRepositoryImpl(
    private val dao: ChatGptDao
): ChatGptConfigRepository {
    override fun getConfig(): Flow<Config> {
        return flow {
            dao.getConfig().collect {
                emit(it.toConfig())
            }
        }
    }

    override suspend fun saveConfig(config: Config) {
        dao.saveConfig(
            ChatGptConfigEntity(
                id = 1,
                n = config.n,
                temperature = config.temperature,
                stream = config.stream,
                max_tokens = config.max_tokens
            )
        )
    }
}