package com.thomaslam.chatgptclient.chatecompletion.presentation

import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Message

data class ConversationState(
    val messages: List<Message> = listOf(),
)
