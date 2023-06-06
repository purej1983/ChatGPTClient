package com.thomaslam.chatgptclient.chatecompletion.domain.use_case

import com.thomaslam.chatgptclient.chatecompletion.domain.model.Config
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatGptConfigRepository

class SaveConfig(
    private val repository: ChatGptConfigRepository
) {
    suspend operator fun invoke(config: Config) {
        repository.saveConfig(config)
    }
}