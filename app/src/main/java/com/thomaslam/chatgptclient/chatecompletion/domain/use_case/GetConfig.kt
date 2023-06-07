package com.thomaslam.chatgptclient.chatecompletion.domain.use_case

import com.thomaslam.chatgptclient.chatecompletion.domain.model.Config
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatGptConfigRepository
import kotlinx.coroutines.flow.Flow

class GetConfig(
    private val repository: ChatGptConfigRepository
) {
    operator fun invoke(): Flow<Config> {
        return repository.getConfig()
    }
}