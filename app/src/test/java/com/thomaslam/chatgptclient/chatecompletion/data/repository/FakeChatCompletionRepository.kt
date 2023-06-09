package com.thomaslam.chatgptclient.chatecompletion.data.repository

import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto.ChatCompletionChunk
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Chat
import com.thomaslam.chatgptclient.chatecompletion.domain.model.ChatState
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatCompletionRepository
import com.thomaslam.chatgptclient.chatecompletion.domain.util.Resource
import com.thomaslam.chatgptclient.chatecompletion.util.MockDataCollections
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.jetbrains.annotations.VisibleForTesting

class FakeChatCompletionRepository: ChatCompletionRepository {
    private val _chats: MutableList<Chat> = MockDataCollections.chats.toMutableList()
    private val _messages: MutableList<Message> = mutableListOf(
            MockDataCollections.userMessage1,
            MockDataCollections.assistantMessage1
    )
    private var chatAutoId: Long = 2
    private val chatFlow = MutableSharedFlow<List<Chat>>()
    private val messageFlow = MutableSharedFlow<List<Message>>()
    private val chunkFlow = MutableSharedFlow<ChatCompletionChunk>()
    override fun getChats(): Flow<List<Chat>> {
        return chatFlow
    }

    override suspend fun newChat(): Long {
        _chats.add(
            Chat(lastUserMessage = "New Chat", id = ++chatAutoId)
        )
        emitChatChange()
        return chatAutoId
    }

    override suspend fun updateLastUserMessage(chatId: Long, content: String) {
        val index = _chats.indexOfFirst { it.id == chatId }
        val chat = _chats.first {it.id == chatId}
        _chats[index] = chat.copy(lastUserMessage = content)
        emitChatChange()
    }

    override suspend fun resetChatState(chatId: Long) {
        val index = _chats.indexOfFirst { it.id == chatId }
        val chat = _chats.first {it.id == chatId}
        _chats[index] = chat.copy(state = ChatState.IDLE)
        emitChatChange()
    }

    override suspend fun updateChatState(chatId: Long, state: ChatState) {
        val index = _chats.indexOfFirst { it.id == chatId }
        val chat = _chats.first {it.id == chatId}
        _chats[index] = chat.copy(state = state)
        emitChatChange()
    }

    override suspend fun saveLocalMessage(
        chatId: Long,
        message: Message,
        conversationId: Long?
    ): Long {
        return 1
    }

    override suspend fun createChatCompletion(messages: List<Message>): Resource<Message> {
        return Resource.Success(MockDataCollections.assistantMessage1)
    }

    override fun getConversation(id: Long): Flow<List<Message>> {
        return messageFlow
    }

    override fun streamChatCompletion(messages: List<Message>): Flow<ChatCompletionChunk> {
        return chunkFlow
    }

    @VisibleForTesting
    suspend fun emitChatChange() {
        chatFlow.emit(_chats)
    }

    @VisibleForTesting
    suspend fun emitMessageChange() {
        messageFlow.emit(_messages)
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