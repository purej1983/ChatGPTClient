package com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote

import com.thomaslam.chatgptclient.chatecompletion.data.remote.ChatCompletionService
import com.thomaslam.chatgptclient.chatecompletion.data.remote.dto.ChatCompletionRequest
import com.thomaslam.chatgptclient.chatecompletion.data.remote.dto.ChatCompletionResponse
import com.thomaslam.chatgptclient.chatecompletion.data.remote.dto.Choice
import com.thomaslam.chatgptclient.chatecompletion.data.remote.dto.Usage
import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Model

class FakeChatCompletionService: ChatCompletionService {
    override suspend fun createChatCompletion(postRequest: ChatCompletionRequest): ChatCompletionResponse {
        return mockResponse
    }
    companion object {
        val mockResponse = ChatCompletionResponse(
            id = "chatcmpl-7IIf6Xlkhg7B0PGOb3zPmrw2rzhom",
            `object` = "chat.completion",
            created = 1684595996,
            model = Model.GPT3_pt_5_turbo_0301,
            usage = Usage(457, 186,643),
            choices = listOf(
                Choice(
                    message = Message(
                        role = "assistant",
                        content = "Here are 5 top attractions in Manchester:\\n\\n1. Old Trafford - home to Manchester United Football Club, Old Trafford is a must-visit for sports fans and offers guided tours of the stadium, museum exhibits, and more.\\n\\n2. The Manchester Museum - a fascinating and diverse collection of natural history, archeology, and art objects from cultures all over the world.\\n\\n3. The John Rylands Library - a stunning neo-gothic building that houses one of the most significant collections of rare books and manuscripts in the UK, with exhibits and guided tours.\\n\\n4. The Science and Industry Museum - one of Manchester's most popular attractions, featuring interactive exhibits, demonstrations, and galleries that explore the science and innovation that shaped the industrial revolution and the modern world.\\n\\n5. The Northern Quarter - Manchester's vibrant cultural hub, known for its independent boutiques, trendy cafes and bars, street art, and vibrant music and arts scene."
                    ),
                    finalReason = "stop",
                    index = 0
                )
            )
        )
    }
}