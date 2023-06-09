package com.thomaslam.chatgptclient.chatecompletion.util

import com.google.gson.Gson
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto.ChatCompletionChunk
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto.ChatCompletionResponse
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto.Choice
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto.SSE
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto.Usage
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Chat
import com.thomaslam.chatgptclient.chatecompletion.domain.model.ChatState
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Config
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Model

class MockDataCollections {
    companion object {
        private val gson = Gson()
        val userMessage1 = Message(
            role = "user",
            content = "Top 5 attractions in Birmingham"
        )

        val userMessage2 = Message(
            role = "user",
            content = "how about in manchester?"
        )

        val assistantMessage1 =  Message(
            role = "assistant",
            content = "1. Cadbury World - a chocolate-themed attraction that offers tours, demonstrations, and interactive exhibits. Located in Bournville, about 6 miles from Birmingham city center.\\n\\n2. Warwick Castle - a medieval fortress with extensive grounds, fortress walls, moat, and dungeons. Located in Warwick, about 22 miles southeast of Birmingham.\\n\\n3. Drayton Manor Theme Park - a large amusement park with rides, roller coasters, attractions, and a zoo. Located in Tamworth, about 16 miles northeast of Birmingham.\\n\\n4. Black Country Living Museum - an open-air museum that showcasing life during the industrial revolution, with reconstructed buildings, coal mines, and a tram system. Located in Dudley, about 10 miles west of Birmingham.\\n\\n5. The National Sea Life Centre - an aquarium with over 60 displays of marine life, including sharks, sea turtles, and tropical fish. Located in Birmingham city center."
        )

        val assistantMessage2 = Message(
            role = "assistant",
            content = "Here are 5 top attractions in Manchester:\\n\\n1. Old Trafford - home to Manchester United Football Club, Old Trafford is a must-visit for sports fans and offers guided tours of the stadium, museum exhibits, and more.\\n\\n2. The Manchester Museum - a fascinating and diverse collection of natural history, archeology, and art objects from cultures all over the world.\\n\\n3. The John Rylands Library - a stunning neo-gothic building that houses one of the most significant collections of rare books and manuscripts in the UK, with exhibits and guided tours.\\n\\n4. The Science and Industry Museum - one of Manchester's most popular attractions, featuring interactive exhibits, demonstrations, and galleries that explore the science and innovation that shaped the industrial revolution and the modern world.\\n\\n5. The Northern Quarter - Manchester's vibrant cultural hub, known for its independent boutiques, trendy cafes and bars, street art, and vibrant music and arts scene."
        )

        val mockReponse = ChatCompletionResponse(
            id = "chatcmpl-7IIf6Xlkhg7B0PGOb3zPmrw2rzhom",
            `object` = "chat.completion",
            created = 1684595996,
            model = Model.GPT3_pt_5_turbo_0301,
            usage = Usage(457, 186,643),
            choices = listOf(
                Choice(
                    message = assistantMessage2,
                    finalReason = "stop",
                    index = 0
                )
            )
        )
        
        const val mockChunkRawData1 =
        "data: {\"id\":\"chatcmpl-7PCtAHaH7ty0Qw2fZbmX77Z3DxRjf\",\"object\":\"chat.completion.chunk\",\"created\":1686242100,\"model\":\"gpt-3.5-turbo-0301\",\"choices\":[{\"delta\":{\"role\":\"assistant\"},\"index\":0,\"finish_reason\":null}]}\n\n"
        const val mockChunkRawData2 =
        "data: {\"id\":\"chatcmpl-7PCtAHaH7ty0Qw2fZbmX77Z3DxRjf\",\"object\":\"chat.completion.chunk\",\"created\":1686242100,\"model\":\"gpt-3.5-turbo-0301\",\"choices\":[{\"delta\":{\"content\":\"Sure\"},\"index\":0,\"finish_reason\":null}]}\n" +
                "\n"
        const val mockChunkRawData3 =
        "data: {\"id\":\"chatcmpl-7PCtAHaH7ty0Qw2fZbmX77Z3DxRjf\",\"object\":\"chat.completion.chunk\",\"created\":1686242100,\"model\":\"gpt-3.5-turbo-0301\",\"choices\":[{\"delta\":{\"content\":\"!\"},\"index\":0,\"finish_reason\":null}]}\n" +
                "\n"
        const val mockChunkRawData4 =
        "data: {\"id\":\"chatcmpl-7PCtAHaH7ty0Qw2fZbmX77Z3DxRjf\",\"object\":\"chat.completion.chunk\",\"created\":1686242100,\"model\":\"gpt-3.5-turbo-0301\",\"choices\":[{\"delta\":{\"content\":\" Here\"},\"index\":0,\"finish_reason\":\"stop\"}]}\n" +
                "\n"

        private var _mockChunk1: ChatCompletionChunk? = null
        private var _mockChunk2: ChatCompletionChunk? = null
        private var _mockChunk3: ChatCompletionChunk? = null
        private var _mockChunk4: ChatCompletionChunk? = null
        val mockCunk1: ChatCompletionChunk
            get() {
                if(_mockChunk1 == null) {
                    val sse = SSE(mockChunkRawData1.substring(5).trim())
                    _mockChunk1 = gson.fromJson(sse.getData(), ChatCompletionChunk::class.java)
                }
                return _mockChunk1!!
            }
        val mockCunk2: ChatCompletionChunk
            get() {
                if(_mockChunk2 == null) {
                    val sse = SSE(mockChunkRawData2.substring(5).trim())
                    _mockChunk2 = gson.fromJson(sse.getData(), ChatCompletionChunk::class.java)
                }
                return _mockChunk2!!
            }
        val mockCunk3: ChatCompletionChunk
            get() {
                if(_mockChunk3 == null) {
                    val sse = SSE(mockChunkRawData3.substring(5).trim())
                    _mockChunk3 = gson.fromJson(sse.getData(), ChatCompletionChunk::class.java)
                }
                return _mockChunk3!!
            }
        val mockCunk4: ChatCompletionChunk
            get() {
                if(_mockChunk4 == null) {
                    val sse = SSE(mockChunkRawData4.substring(5).trim())
                    _mockChunk4 = gson.fromJson(sse.getData(), ChatCompletionChunk::class.java)
                }
                return _mockChunk4!!
            }

        val chats = listOf(
            Chat(lastUserMessage = "Top 5 attractions in Birmingham", id = 1),
            Chat(lastUserMessage = "New Chat", id = 2, state = ChatState.NEW_MESSAGE),
        )

        val config = Config(
            n = 2,
            temperature = 0.8f,
            stream = true,
            max_tokens = 120
        )
    }
}