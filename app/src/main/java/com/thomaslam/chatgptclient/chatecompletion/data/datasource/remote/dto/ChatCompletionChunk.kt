package com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto

import com.thomaslam.chatgptclient.chatecompletion.domain.model.Model

class ChatCompletionChunk(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: Model,
    val choices: List<Choice>
)
