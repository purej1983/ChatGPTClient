package com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto

import com.thomaslam.chatgptclient.chatecompletion.domain.model.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Model

data class ChatCompletionRequest(val model: Model = Model.GPT3_pt_5_turbo, val messages: List<Message>, val stream: Boolean = false)
