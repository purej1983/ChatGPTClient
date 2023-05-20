package com.thomaslam.chatgptclient.chatecompletion.data.remote.dto

import com.thomaslam.chatgptclient.chatecompletion.domain.Entity.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.Entity.Model

data class ChatCompletionRequest(val model: Model = Model.GPT3_pt_5_turbo, val messages: List<Message>)
