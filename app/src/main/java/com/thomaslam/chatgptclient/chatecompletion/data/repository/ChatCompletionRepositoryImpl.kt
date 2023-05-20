package com.thomaslam.chatgptclient.chatecompletion.data.repository

import com.thomaslam.chatgptclient.chatecompletion.data.remote.ChatCompletionService
import com.thomaslam.chatgptclient.chatecompletion.data.remote.dto.ChatCompletionRequest
import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatCompletionRepository

class ChatCompletionRepositoryImpl (
    private val chatCompletionService: ChatCompletionService
) : ChatCompletionRepository {
    override suspend fun create(messages: List<Message>): Message {
        try {
            val response = chatCompletionService.createChatCompletion(
                ChatCompletionRequest(
                    messages = messages
                )
            )

            return response.choices.first().message
        } catch (e: Exception) {
            throw e
        }
    }

}