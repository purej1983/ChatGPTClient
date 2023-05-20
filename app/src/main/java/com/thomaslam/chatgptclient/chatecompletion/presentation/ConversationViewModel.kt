package com.thomaslam.chatgptclient.chatecompletion.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
    private val chatCompletionUseCase: ChatCompletionUseCase
) : ViewModel(){
    private val scope = CoroutineScope(Dispatchers.Default)
    private val _state = mutableStateOf(ConversationState())
    val state: State<ConversationState> = _state

    init {

    }

    fun send(content: String) {

        val newUserMessage = Message(content = content)
        val messages = _state.value.messages + newUserMessage
        appendMessage(newUserMessage)
        scope.launch {
            val newAssitantMessage = chatCompletionUseCase.createCompletion(messages)
            appendMessage(newAssitantMessage)
        }
    }

    private fun appendMessage(message: Message) {
        val newMessages = _state.value.messages + message
        _state.value = state.value.copy(
            messages = newMessages
        )
    }

}