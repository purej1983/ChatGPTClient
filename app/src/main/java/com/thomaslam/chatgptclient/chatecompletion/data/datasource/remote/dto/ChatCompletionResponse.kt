package com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto

import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Model

data class ChatCompletionResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: Model,
    val usage: Usage,
    val choices: List<Choice>
)
