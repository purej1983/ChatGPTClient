package com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote

import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto.ChatCompletionRequest
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto.ChatCompletionResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface ChatCompletionService {
    @POST("v1/chat/completions")
    suspend fun createChatCompletion(@Body postRequest: ChatCompletionRequest): ChatCompletionResponse

    @Streaming
    @POST("v1/chat/completions")
    fun createStreamChatCompletion(@Body postRequest: ChatCompletionRequest): Call<ResponseBody>

//    @Streaming
//    @POST("v1/chat/completions")
//    fun createStreamChatCompletion(@Body postRequest: ChatCompletionRequest): Flow<ChatCompletionChunk> = flow {
//        val streamRequest = postRequest.copy(stream = true)
//        val call = createStreamChatCompletionSSE(streamRequest)
//        val gson = Gson()
//        createStreamChatCompletionSSE2(call).map {
//            gson.fromJson<ChatCompletionChunk>(it.getData(), ChatCompletionChunk::class.java)
//        }.collect {
//            emit(it)
//        }
//    }

//    @Streaming
//    @POST("v1/chat/completions")
//    fun createStreamChatCompletionSSE2(call: Call<ResponseBody>): Flow<SSE> = callbackFlow {
//        val callback = object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                val inputStream = response.body()?.byteStream() ?: return
//                val reader = BufferedReader(InputStreamReader(inputStream))
//                var line: String? = reader.readLine()
//                lateinit var sse:SSE
//                while (line != null) {
//                    if (line.startsWith("data:")) {
//                        val data = line.substring(5).trim { it <= ' ' }
//                        sse = SSE(data);
//                    } else if(line.equals("") && sse != null) {
//                        if(sse.isDone()) {
//                            break
//                        }
//                    }
//                    Log.d("ChatViewModel", "line${line}")
//                    line = reader.readLine()
//                }
//                Log.d("ChatViewModel", "End")
//                inputStream.close()
//                reader.close()
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                TODO("Not yet implemented")
//            }
//
//        }
//        call.enqueue(callback)
//    }

}