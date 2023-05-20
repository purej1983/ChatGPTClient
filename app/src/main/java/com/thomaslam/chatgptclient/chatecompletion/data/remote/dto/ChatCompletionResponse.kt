package com.thomaslam.chatgptclient.chatecompletion.data.remote.dto

import com.thomaslam.chatgptclient.chatecompletion.domain.Entity.Model

data class ChatCompletionResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: Model,
    val usage: Usage,
    val choices: List<Choice>
)