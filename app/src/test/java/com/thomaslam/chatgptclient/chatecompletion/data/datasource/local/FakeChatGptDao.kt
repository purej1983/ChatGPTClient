package com.thomaslam.chatgptclient.chatecompletion.data.datasource.local

import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ChatEntity
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ConversationEntity
import com.thomaslam.chatgptclient.chatecompletion.domain.model.ChatState
import com.thomaslam.chatgptclient.chatecompletion.util.MockDataCollections
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeChatGptDao: ChatGptDao {
    private val chatFlow = MutableSharedFlow<List<ChatEntity>>()
    private val conversationFlow = MutableSharedFlow<List<ConversationEntity>>()
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
        emitChatChange()
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
        emitConversationChange()
    }

    override suspend fun updateLastUserMessage(chatId: Long, lastUserMessage: String) {
        val index = chats.indexOfFirst { it.id == chatId }
        chats[index] = ChatEntity(lastUserMessage = lastUserMessage, id = chatId)
        emitChatChange()
    }

    override suspend fun resetChatState(chatId: Long) {

    }

    override suspend fun updateChatState(chatId: Long, state: ChatState) {

    }

    override fun getChats(): Flow<List<ChatEntity>> {
        return chatFlow
    }

    override fun getConversationByChatId(chatId: Long): Flow<List<ConversationEntity>> {
        return conversationFlow
    }

    suspend fun emitChatChange() {
        chatFlow.emit(chats)
    }

    suspend fun emitConversationChange(){
        conversationFlow.emit(conversations)
    }

    companion object {
        val mockChats = mutableListOf(
            ChatEntity(lastUserMessage = "testMessage1", id = 1),
            ChatEntity(lastUserMessage = "testMessage2", id = 2),
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