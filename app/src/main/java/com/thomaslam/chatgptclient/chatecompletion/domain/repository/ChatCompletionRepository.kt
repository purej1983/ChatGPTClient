package com.thomaslam.chatgptclient.chatecompletion.domain.repository

import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Message

interface ChatCompletionRepository {
    suspend fun create(messages: List<Message>): Message
}