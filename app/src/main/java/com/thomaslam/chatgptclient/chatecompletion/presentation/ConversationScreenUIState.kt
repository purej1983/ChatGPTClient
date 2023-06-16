package com.thomaslam.chatgptclient.chatecompletion.presentation

import com.thomaslam.chatgptclient.chatecompletion.domain.model.ConversationWithSelectMessage

data class ConversationScreenUIState(
    val messages: List<ConversationWithSelectMessage> = listOf(),
    val isLoading: Boolean = false
)
