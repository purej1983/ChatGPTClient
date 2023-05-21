package com.thomaslam.chatgptclient.chatecompletion.presentation

import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Chat

data class ChatScreenUIState (
    val chats: List<Chat> = listOf()
)