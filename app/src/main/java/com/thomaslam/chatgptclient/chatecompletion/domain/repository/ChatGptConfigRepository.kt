package com.thomaslam.chatgptclient.chatecompletion.domain.repository

import com.thomaslam.chatgptclient.chatecompletion.domain.model.Config
import kotlinx.coroutines.flow.Flow

interface ChatGptConfigRepository {
    fun getConfig(): Flow<Config>
    suspend fun saveConfig(config: Config)
}