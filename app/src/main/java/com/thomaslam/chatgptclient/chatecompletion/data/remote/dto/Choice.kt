package com.thomaslam.chatgptclient.chatecompletion.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.thomaslam.chatgptclient.chatecompletion.domain.Entity.Message

data class Choice(
    val messages: List<Message>,
    @SerializedName("finish_reason") val finalReason: String,
    val index: Int,
)
