package com.thomaslam.chatgptclient.chatecompletion.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaslam.chatgptclient.chatecompletion.domain.ChatCompletionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatCompletionUseCase: ChatCompletionUseCase
) : ViewModel()
{
    private val _state = mutableStateOf(ChatScreenUIState())
    val state: State<ChatScreenUIState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getChats()
    }

    fun getChats() {
        viewModelScope.launch {
            val chats = chatCompletionUseCase.getChats()
            _state.value = state.value.copy(
                chats = chats
            )
        }
    }
    fun newChat() {
        viewModelScope.launch {
            val id = chatCompletionUseCase.newChat()
            _eventFlow.emit(UiEvent.NavigateToChat(id))
        }
    }

    fun goToChat(id: Long) {
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.NavigateToChat(id))
        }
    }
    sealed class UiEvent {
        data class NavigateToChat(val id: Long): UiEvent()
    }
}