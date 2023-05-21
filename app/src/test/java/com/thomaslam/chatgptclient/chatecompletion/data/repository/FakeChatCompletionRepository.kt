package com.thomaslam.chatgptclient.chatecompletion.data.repository

import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Chat
import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatCompletionRepository
import com.thomaslam.chatgptclient.chatecompletion.util.MockDataCollections

class FakeChatCompletionRepository: ChatCompletionRepository {
    override suspend fun getChats(): List<Chat> {
        return MockDataCollections.chats
    }

    override suspend fun newChat(): Long {
        return 2L
    }

    override suspend fun updateLastUserMessage(chatId: Long, content: String) {

    }

    override suspend fun saveLocalMessage(chatId: Long, message: Message) {

    }

    override suspend fun create(messages: List<Message>): Message {
        return MockDataCollections.assistantMessage1
    }

    override suspend fun getConversation(id: Long): List<Message> {
        return listOf(
            MockDataCollections.userMessage1,
            MockDataCollections.assistantMessage1
        )
    }
}