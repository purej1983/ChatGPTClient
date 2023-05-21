package com.thomaslam.chatgptclient.chatecompletion.domain.repository

import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Chat
import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Message

interface ChatCompletionRepository {
    suspend fun getChats(): List<Chat>
    suspend fun newChat(): Long
    suspend fun updateLastUserMessage(chatId: Long, content: String)
    suspend fun saveLocalMessage(chatId :Long, message: Message)
    suspend fun create(messages: List<Message>): Message
    suspend fun getConversation(id: Long): List<Message>
}