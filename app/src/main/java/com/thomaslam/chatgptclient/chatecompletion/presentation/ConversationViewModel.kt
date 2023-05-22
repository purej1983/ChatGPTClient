package com.thomaslam.chatgptclient.chatecompletion.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaslam.chatgptclient.chatecompletion.domain.ChatCompletionUseCase
import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val chatCompletionUseCase: ChatCompletionUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel(){
    private val scope = CoroutineScope(Dispatchers.Default)
    private val _state = mutableStateOf(ConversationScreenUIState())
    val state: State<ConversationScreenUIState> = _state
    private var currentChatId: Long = -1

    init {
        savedStateHandle.get<Long>("chatId")?.let { chatId ->
            currentChatId = chatId
            getConversation(chatId)
        }
    }

    private fun getConversation(chatId: Long) {
        viewModelScope.launch {
            chatCompletionUseCase.getConversation(chatId)?.also { messages ->
                _state.value = state.value.copy(
                    messages = messages
                )
            }
        }
    }

    fun send(content: String) {

        val newUserMessage = Message(content = content)
        val messages = _state.value.messages + newUserMessage
        appendMessage(newUserMessage)
        scope.launch {
            try {
                setLoading(true)
                chatCompletionUseCase.createCompletion(currentChatId, messages)
            } catch (e: Exception) {

            } finally {
                setLoading(false)
            }
            getConversation(currentChatId)
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _state.value = state.value.copy(
            isLoading = isLoading
        )
    }

    private fun appendMessage(message: Message) {
        val newMessages = _state.value.messages + message
        _state.value = state.value.copy(
            messages = newMessages
        )
        scope.launch {
            chatCompletionUseCase.updateLastUserMessage(currentChatId, message.content)
            chatCompletionUseCase.saveMessage(currentChatId, message)
        }
    }
}