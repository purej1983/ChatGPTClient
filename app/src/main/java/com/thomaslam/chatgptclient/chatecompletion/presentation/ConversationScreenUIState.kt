package com.thomaslam.chatgptclient.chatecompletion.presentation

import com.thomaslam.chatgptclient.chatecompletion.domain.model.Message

data class ConversationScreenUIState(
    val messages: List<Message> = listOf(),
    val isLoading: Boolean = false
)
