package com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote

import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto.ChatCompletionRequest
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto.ChatCompletionResponse
import com.thomaslam.chatgptclient.chatecompletion.util.MockDataCollections

class FakeChatCompletionService: ChatCompletionService {
    override suspend fun createChatCompletion(postRequest: ChatCompletionRequest): ChatCompletionResponse {
        return MockDataCollections.mockReponse
    }
}