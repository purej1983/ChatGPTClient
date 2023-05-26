package com.thomaslam.chatgptclient.chatecompletion.domain.repository

import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Chat
import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Message
import kotlinx.coroutines.flow.Flow

interface ChatCompletionRepository {
    fun getChats(): Flow<List<Chat>>
    suspend fun newChat(): Long
    suspend fun updateLastUserMessage(chatId: Long, content: String)
    suspend fun saveLocalMessage(chatId :Long, message: Message)
    suspend fun create(messages: List<Message>): Message
    fun getConversation(id: Long): Flow<List<Message>>
}