package com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote

import com.thomaslam.chatgptclient.chatecompletion.data.remote.ChatCompletionService
import com.thomaslam.chatgptclient.chatecompletion.data.remote.dto.ChatCompletionRequest
import com.thomaslam.chatgptclient.chatecompletion.data.remote.dto.ChatCompletionResponse
import com.thomaslam.chatgptclient.chatecompletion.data.remote.dto.Choice
import com.thomaslam.chatgptclient.chatecompletion.data.remote.dto.Usage
import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Model
import com.thomaslam.chatgptclient.chatecompletion.util.MockDataCollections

class FakeChatCompletionService: ChatCompletionService {
    override suspend fun createChatCompletion(postRequest: ChatCompletionRequest): ChatCompletionResponse {
        return MockDataCollections.mockReponse
    }
}