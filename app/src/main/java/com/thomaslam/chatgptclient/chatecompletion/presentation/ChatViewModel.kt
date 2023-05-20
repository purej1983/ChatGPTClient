package com.thomaslam.chatgptclient.chatecompletion.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaslam.chatgptclient.chatecompletion.domain.ChatCompletionUseCase
import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
) : ViewModel()
{

}