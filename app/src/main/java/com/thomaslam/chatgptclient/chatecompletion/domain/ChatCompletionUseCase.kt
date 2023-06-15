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
                emit(Resource.Success(assistantMessage.first()))
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
        val roleMap = mutableMapOf<Int, String>()
        val contentMap = mutableMapOf<Int, String>()
        val messsageMap = mutableMapOf<Int, Message>()
        var conversationId: Long? = null
        repository.streamChatCompletion(messages).collect{
            val idx = it.choices[0].index
            if(it.choices[0].message.role != null && it.choices[0].message.role.isNotEmpty()) {
                roleMap[idx] = it.choices[0].message.role
            } else if (it.choices[0].message.content != null && it.choices[0].message.content.isNotEmpty()) {
                val content = contentMap[idx] ?: ""
                contentMap[idx] = content + it.choices[0].message.content
            }
            roleMap[idx]?.let { role ->
                contentMap[idx]?.let { content ->
                    val message = Message(
                        role = role,
                        content = content
                    )
                    messsageMap[idx] = message
                    if(it.choices[0].finalReason == "stop") {
                        updateChatState(chatId = chatId, state = ChatState.NEW_MESSAGE)
                    }
                    val newAssitantMessages = messsageMap.toSortedMap().values.toList()
                    conversationId = saveMessage(chatId, newAssitantMessages, conversationId)
                    emit(Resource.Success(newAssitantMessages.first()))
                }
            }
        }
    }

    fun getConversation(id: Long): Flow<List<Message>> {
        return repository.getConversation(id)
    }

    suspend fun updateLastUserMessage(chatId: Long, content: String) {
        repository.updateLastUserMessage(chatId, content)
    }
    suspend fun saveMessage(chatId: Long, messages: List<Message>, conversationId: Long? = null): Long {
        return repository.saveLocalMessage(chatId, messages, conversationId)
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