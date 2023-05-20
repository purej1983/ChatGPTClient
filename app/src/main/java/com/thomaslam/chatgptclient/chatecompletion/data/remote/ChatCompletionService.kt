package com.thomaslam.chatgptclient.chatecompletion.data.remote

import com.thomaslam.chatgptclient.chatecompletion.data.remote.dto.ChatCompletionRequest
import com.thomaslam.chatgptclient.chatecompletion.data.remote.dto.ChatCompletionResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatCompletionService {
    @POST("v1/chat/completions")
    suspend fun createChatCompletion(@Body postRequest: ChatCompletionRequest): ChatCompletionResponse
}