package com.thomaslam.chatgptclient.chatecompletion.domain

import com.thomaslam.chatgptclient.chatecompletion.domain.model.Chat
import com.thomaslam.chatgptclient.chatecompletion.domain.model.ChatState
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatCompletionRepository
import com.thomaslam.chatgptclient.chatecompletion.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow

class ChatCompletionUseCase (
    private val repository: ChatCompletionRepository
) {
    private val _errorEventFlow = MutableSharedFlow<ErrorEvent>()
    val errorEventFlow = _errorEventFlow.asSharedFlow()

    private val _chatEventFlow = MutableSharedFlow<ChatEvent>()
    val chatEventFlow = _chatEventFlow.asSharedFlow()

    fun getChats(): Flow<List<Chat>> {
        return repository.getChats()
    }
    suspend fun newChat(): Long {
        return repository.newChat()
    }
    suspend fun createCompletion(chatId: Long, messages: List<Message>): Flow<Resource<Message>> = flow {
        emit(Resource.Loading())
        updateChatState(chatId = chatId, state = ChatState.LOADING)
        val chatCompletionResult = repository.create(messages)

        if (chatCompletionResult is Resource.Success) {
            val assistantMessage = chatCompletionResult.data
            assistantMessage?.let {
                saveMessage(chatId, assistantMessage)
                updateChatState(chatId = chatId, state = ChatState.NEW_MESSAGE)
                emit(Resource.Success(assistantMessage))
                _chatEventFlow.emit(ChatEvent.NewConversation(chatId))
            }
        } else if (chatCompletionResult is Resource.Error) {
            val errorMessage = chatCompletionResult.message ?: ""
            updateChatState(chatId = chatId, state = ChatState.ERROR)
            emit(Resource.Error(message = errorMessage))
            _errorEventFlow.emit(ErrorEvent.ChatCompletionError(chatId = chatId, errorMessage))
        }
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

    suspend fun resetChatState(chatId: Long) {
        repository.resetChatState(chatId = chatId)
    }

    private suspend fun updateChatState(chatId: Long, state: ChatState) {
        repository.updateChatState(chatId = chatId, state = state)
    }

    sealed class ErrorEvent(val errorMessage: String) {
        class ChatCompletionError(val chatId: Long, errorMessage: String): ErrorEvent(errorMessage)
    }

    sealed class ChatEvent {
        data class NewConversation(val chatId: Long): ChatEvent()
    }
}