package com.thomaslam.chatgptclient.chatecompletion.domain

import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Chat
import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatCompletionRepository
import kotlinx.coroutines.flow.Flow

class ChatCompletionUseCase (
    private val repository: ChatCompletionRepository
) {
    fun getChats(): Flow<List<Chat>> {
        return repository.getChats()
    }
    suspend fun newChat(): Long {
        return repository.newChat()
    }
    suspend fun createCompletion(chatId: Long, messages: List<Message>): Message {
        val assistantMessage = repository.create(messages)
        saveMessage(chatId, assistantMessage)
        return assistantMessage
    }

    fun getConversation(id: Long): Flow<List<Message>> {
        return repository.getConversation(id)
    }

    suspend fun updateLastUserMessage(chatId: Long, content: String) {
        repository.updateLastUserMessage(chatId, content)
    }
    suspend fun saveMessage(chatId: Long, message: Message) {
        repository.saveLocalMessage(chatId, message)
    }
}