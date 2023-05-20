package com.thomaslam.chatgptclient.chatecompletion.domain

import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatCompletionRepository

class ChatCompletionUseCase (
    private val repository: ChatCompletionRepository
) {
    suspend fun createCompletion(messages: List<Message>): Message {
        return repository.create(messages)
    }
}