package com.thomaslam.chatgptclient.chatecompletion.domain.model

data class Config(
    val n: Int,
    val temperature: Float,
    val stream: Boolean,
    val max_tokens: Int
)
