package com.thomaslam.chatgptclient.chatecompletion.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaslam.chatgptclient.chatecompletion.domain.ChatCompletionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val chatCompletionUseCase: ChatCompletionUseCase
): ViewModel() {
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            handleCompletionError()
        }
    }

    private suspend fun handleCompletionError() {
        chatCompletionUseCase.errorEventFlow.collectLatest {
            _eventFlow.emit(UiEvent.ShowSnackBarEvent(it.errorMessage))
        }
    }

    sealed class UiEvent {
        data class ShowSnackBarEvent(val message: String): UiEvent()
    }
}