package com.thomaslam.chatgptclient.chatecompletion.presentation

import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Message

data class ConversationScreenUIState(
    val messages: List<Message> = listOf(),
)
