package com.thomaslam.chatgptclient.chatecompletion.domain.repository

import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto.ChatCompletionChunk
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Chat
import com.thomaslam.chatgptclient.chatecompletion.domain.model.ChatState
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ChatCompletionRepository {
    fun getChats(): Flow<List<Chat>>
    suspend fun newChat(): Long
    suspend fun updateLastUserMessage(chatId: Long, content: String)
    suspend fun resetChatState(chatId: Long)
    suspend fun updateChatState(chatId: Long, state: ChatState)
    suspend fun saveLocalMessage(chatId :Long, messages: List<Message>, conversationId: Long? = null): Long
    suspend fun createChatCompletion(messages: List<Message>): Resource<List<Message>>
    fun getConversation(id: Long): Flow<List<Message>>
    fun streamChatCompletion(messages: List<Message>): Flow<ChatCompletionChunk>
}