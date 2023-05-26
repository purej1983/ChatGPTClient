package com.thomaslam.chatgptclient.chatecompletion.data.datasource.local

import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ChatEntity
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ConversationEntity
import com.thomaslam.chatgptclient.chatecompletion.util.MockDataCollections

class FakeChatGptDao: ChatGptDao {
    private var chatAutoId: Long = 2
    private var conversationAutoId: Long = 2

    private val conversations = mockConversations

    private val chats = mockChats
    override suspend fun insertChat(chat: ChatEntity): Long {
        chats.add(
            ChatEntity(
                lastUserMessage = chat.lastUserMessage,
                id = ++chatAutoId
            )
        )
        return chatAutoId
    }

    override suspend fun insertConversation(conversation: ConversationEntity) {
        conversations.add(
            ConversationEntity(
                role = conversation.role,
                content = conversation.content,
                chatId = conversation.chatId,
                id = ++conversationAutoId
            )
        )
    }

    override suspend fun updateLastUserMessage(chatId: Long, lastUserMessage: String) {
        val index = chats.indexOfFirst { it.id == chatId }
        chats[index] = ChatEntity(lastUserMessage, chatId)
    }

    override suspend fun getChats(): List<ChatEntity> {
        return chats
    }

    override suspend fun getConversationByChatId(chatId: Long): List<ConversationEntity> {
        return mockConversations
    }
    companion object {
        val mockChats = mutableListOf(
            ChatEntity("testMessage1", 1),
            ChatEntity("testMessage2", 2),
        )

        val mockConversations = mutableListOf(
            ConversationEntity(
                id = 1,
                role = MockDataCollections.userMessage1.role,
                content = MockDataCollections.userMessage1.content,
                chatId = 1
            ),
            ConversationEntity(
                id = 2,
                role = MockDataCollections.assistantMessage1.role,
                content = MockDataCollections.assistantMessage1.content,
                chatId = 1
            )
        )
    }
}