package com.thomaslam.chatgptclient.chatecompletion.data.repository

import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto.ChatCompletionChunk
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Chat
import com.thomaslam.chatgptclient.chatecompletion.domain.model.ChatState
import com.thomaslam.chatgptclient.chatecompletion.domain.model.ConversationWithSelectMessage
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatCompletionRepository
import com.thomaslam.chatgptclient.chatecompletion.domain.util.Resource
import com.thomaslam.chatgptclient.chatecompletion.util.MockDataCollections
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.annotations.VisibleForTesting

class FakeChatCompletionRepository: ChatCompletionRepository {
    private var _chats: List<Chat> = MockDataCollections.chats
    private val _conversations: List<ConversationWithSelectMessage> = listOf(
        ConversationWithSelectMessage(
            conversationId = 1,
            selectMessage = MockDataCollections.userMessage1,
            selectMessageIdx = 0,
            totalMessage = 1
        ),
        ConversationWithSelectMessage(
            conversationId = 2,
            selectMessage = MockDataCollections.assistantMessage1,
            selectMessageIdx = 0,
            totalMessage = 1
        )
    )
    private var chatAutoId: Long = 2
    private val chatFlow = MutableStateFlow(_chats)
    private val conversationFlow = MutableStateFlow(_conversations)
    private val chunkFlow = MutableSharedFlow<ChatCompletionChunk>()
    override fun getChats(): Flow<List<Chat>> {
        return chatFlow
    }

    override suspend fun newChat(): Long {
        val newChats = chatFlow.value.toMutableList()
        newChats.add(
            Chat(lastUserMessage = "New Chat", id = ++chatAutoId)
        )
        _chats = newChats
        chatFlow.value = _chats
        return chatAutoId
    }

    override suspend fun updateLastUserMessage(chatId: Long, content: String) {
        val newChats = chatFlow.value.toMutableList()
        val index = newChats.indexOfFirst { it.id == chatId }
        val chat = newChats.first {it.id == chatId}
        newChats[index] = chat.copy(lastUserMessage = content)
        _chats = newChats
        chatFlow.value = _chats
    }

    override suspend fun resetChatState(chatId: Long) {
        val newChats = chatFlow.value.toMutableList()
        val index = newChats.indexOfFirst { it.id == chatId }
        val chat = newChats.first {it.id == chatId}
        newChats[index] = chat.copy(state = ChatState.IDLE)
        _chats = newChats
        chatFlow.value = _chats
    }

    override suspend fun updateChatState(chatId: Long, state: ChatState) {
        val newChats = chatFlow.value.toMutableList()
        val index = newChats.indexOfFirst { it.id == chatId }
        val chat = newChats.first {it.id == chatId}
        newChats[index] = chat.copy(state = state)
        _chats = newChats
        chatFlow.value = _chats
    }

    override suspend fun saveLocalMessage(
        chatId: Long,
        messages: List<Message>,
        conversationId: Long?
    ): Long {
        return 1
    }

    override suspend fun createChatCompletion(messages: List<Message>): Resource<List<Message>> {
        return Resource.Success(listOf(MockDataCollections.assistantMessage1))
    }

    override fun getConversation(id: Long): Flow<List<ConversationWithSelectMessage>> {
        return conversationFlow
    }

    override fun streamChatCompletion(messages: List<Message>): Flow<ChatCompletionChunk> {
        return chunkFlow
    }

    override suspend fun navigateToMessage(conversationId: Long, next: Boolean) {

    }

    @VisibleForTesting
    suspend fun emitChunkChange() {
        chunkFlow.emit(MockDataCollections.mockCunk1)
        delay(500)
        chunkFlow.emit(MockDataCollections.mockCunk2)
        delay(500)
        chunkFlow.emit(MockDataCollections.mockCunk3)
        delay(500)
        chunkFlow.emit(MockDataCollections.mockCunk4)
    }
}