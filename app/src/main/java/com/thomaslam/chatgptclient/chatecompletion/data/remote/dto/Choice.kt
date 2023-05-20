package com.thomaslam.chatgptclient.chatecompletion.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Message

data class Choice(
    val message: Message,
    @SerializedName("finish_reason") val finalReason: String,
    val index: Int,
)
