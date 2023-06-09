package com.thomaslam.chatgptclient.chatecompletion.domain

import com.thomaslam.chatgptclient.chatecompletion.domain.model.Chat
import com.thomaslam.chatgptclient.chatecompletion.domain.model.ChatState
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatCompletionRepository
import com.thomaslam.chatgptclient.chatecompletion.domain.util.Resource
import com.thomaslam.chatgptclient.chatecompletion.util.ConfigurationProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow

class ChatCompletionUseCase (
    private val repository: ChatCompletionRepository,
    private val configurationProvider: ConfigurationProvider
) {
    private val _errorEventFlow = MutableSharedFlow<ErrorEvent>()
    val errorEventFlow = _errorEventFlow.asSharedFlow()

    fun getChats(): Flow<List<Chat>> {
        return repository.getChats()
    }
    suspend fun newChat(): Long {
        return repository.newChat()
    }

    suspend fun createChatCompletion(chatId: Long, messages: List<Message>): Flow<Resource<Message>> {
        return if(configurationProvider.stream) {
            streamChatCompletion(chatId = chatId, messages = messages)
        } else {
            createChatCompletionInternal(chatId = chatId, messages = messages)
        }
    }
    private suspend fun createChatCompletionInternal(chatId: Long, messages: List<Message>): Flow<Resource<Message>> = flow {
        emit(Resource.Loading())
        updateChatState(chatId = chatId, state = ChatState.LOADING)
        val chatCompletionResult = repository.createChatCompletion(messages)

        if (chatCompletionResult is Resource.Success) {
            val assistantMessage = chatCompletionResult.data
            assistantMessage?.let {
                saveMessage(chatId, assistantMessage)
                updateChatState(chatId = chatId, state = ChatState.NEW_MESSAGE)
                emit(Resource.Success(assistantMessage))
            }
        } else if (chatCompletionResult is Resource.Error) {
            val errorMessage = chatCompletionResult.message ?: ""
            updateChatState(chatId = chatId, state = ChatState.ERROR)
            emit(Resource.Error(message = errorMessage))
            _errorEventFlow.emit(ErrorEvent.ChatCompletionError(chatId = chatId, errorMessage))
        }
    }

    private suspend fun streamChatCompletion(chatId: Long, messages: List<Message>): Flow<Resource<Message>> = flow {
        emit(Resource.Loading())
        updateChatState(chatId = chatId, state = ChatState.LOADING)
        var role = ""
        var content = ""
        var conversationId: Long? = null
        repository.streamChatCompletion(messages).collect{
            println("chunk ${it.choices[0]}")
            if(it.choices[0].message.role != null && it.choices[0].message.role.isNotEmpty()) {
                role = it.choices[0].message.role
            } else if (it.choices[0].message.content != null && it.choices[0].message.content.isNotEmpty()) {
                content += it.choices[0].message.content
            }
            if(role.isNotEmpty() && content.isNotEmpty()) {
                val message = Message(
                    role = role,
                    content = content
                )
                if(it.choices[0].finalReason == "stop") {
                    updateChatState(chatId = chatId, state = ChatState.NEW_MESSAGE)
                }
                conversationId = saveMessage(chatId, message, conversationId)
                emit(Resource.Success(message))
            }
        }
    }

    fun getConversation(id: Long): Flow<List<Message>> {
        return repository.getConversation(id)
    }

    suspend fun updateLastUserMessage(chatId: Long, content: String) {
        repository.updateLastUserMessage(chatId, content)
    }
    suspend fun saveMessage(chatId: Long, message: Message, conversationId: Long? = null): Long {
        return repository.saveLocalMessage(chatId, message, conversationId)
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
}