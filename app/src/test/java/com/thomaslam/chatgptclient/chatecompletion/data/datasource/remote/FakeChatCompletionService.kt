package com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote

import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto.ChatCompletionRequest
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto.ChatCompletionResponse
import com.thomaslam.chatgptclient.chatecompletion.util.MockDataCollections
import okhttp3.ResponseBody
import retrofit2.Call

class FakeChatCompletionService: ChatCompletionService {
    override suspend fun createChatCompletion(postRequest: ChatCompletionRequest): ChatCompletionResponse {
        return MockDataCollections.mockReponse
    }

    override fun createStreamChatCompletion(postRequest: ChatCompletionRequest): Call<ResponseBody> {
        TODO("Not yet implemented")
    }
}