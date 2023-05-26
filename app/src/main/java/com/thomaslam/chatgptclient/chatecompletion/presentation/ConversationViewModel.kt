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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()


    init {
        savedStateHandle.get<Long>("chatId")?.let { chatId ->
            currentChatId = chatId
            getConversation(chatId)
        }
    }

    private fun getConversation(chatId: Long) {
        chatCompletionUseCase.getConversation(chatId).onEach { messages ->
            _state.value = state.value.copy(
                messages = messages
            )
        }.launchIn(viewModelScope)
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
                _eventFlow.emit(UiEvent.ShowSnackBar("Error occurred! Please try again later"))
            } finally {
                setLoading(false)
            }
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
    sealed class UiEvent {
        data class ShowSnackBar(val message: String): UiEvent()
    }
}