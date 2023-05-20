package com.thomaslam.chatgptclient.chatecompletion.domain.entity

data class Message(
    val role: String = "user",
    val content: String
)
