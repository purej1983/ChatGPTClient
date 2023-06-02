package com.thomaslam.chatgptclient.chatecompletion.domain.model

data class Message(
    val role: String = "user",
    val content: String
)
