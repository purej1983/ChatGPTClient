package com.thomaslam.chatgptclient.chatecompletion.data.repository

import com.google.gson.Gson
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.ChatGptDao
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ChatEntity
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ConversationEntity
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.ChatCompletionService
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto.ChatCompletionChunk
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto.ChatCompletionRequest
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto.SSE
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Chat
import com.thomaslam.chatgptclient.chatecompletion.domain.model.ChatState
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatCompletionRepository
import com.thomaslam.chatgptclient.chatecompletion.domain.util.Resource
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class ChatCompletionRepositoryImpl (
    private val chatGptDao: ChatGptDao,
    private val chatCompletionService: ChatCompletionService
) : ChatCompletionRepository {
    private val gson = Gson()
    override fun getChats(): Flow<List<Chat>> {
        return flow {
            chatGptDao.getChats().collect { list ->
                emit(list.map { it.toChat() })
            }
        }
    }

    override suspend fun newChat():Long {
        return chatGptDao.insertChat(ChatEntity())
    }

    override suspend fun updateLastUserMessage(chatId: Long, content: String) {
        chatGptDao.updateLastUserMessage(chatId, content)
    }

    override suspend fun resetChatState(chatId: Long) {
        chatGptDao.resetChatState(chatId)
    }

    override suspend fun updateChatState(chatId: Long, state: ChatState) {
        chatGptDao.updateChatState(chatId, state)
    }

    override suspend fun saveLocalMessage(chatId :Long, message: Message, conversationId: Long?): Long {
        val entity = if(conversationId != null)
            ConversationEntity(
                id = conversationId,
                role = message.role,
                content = message.content,
                chatId = chatId
            ) else
            ConversationEntity(
                role = message.role,
                content = message.content,
                chatId = chatId
            )
        return chatGptDao.insertConversation(
            entity
        )
    }

    override suspend fun createChatCompletion(messages: List<Message>): Resource<Message> {
        try {
            val response = chatCompletionService.createChatCompletion(
                ChatCompletionRequest(
                    messages = messages
                )
            )
            return Resource.Success(response.choices.first().message)
        } catch(e: HttpException) {
            return Resource.Error(
                message = "Oops, something went wrong!"
            )
        } catch(e: IOException) {
            return Resource.Error(
                message = "Couldn't reach server, check your internet connection."
            )
        }
    }

    override fun streamChatCompletion(messages: List<Message>): Flow<ChatCompletionChunk> {
        val call:Call<ResponseBody> = chatCompletionService.createStreamChatCompletion(
            ChatCompletionRequest(
                messages = messages,
                stream = true
            )
        )
        return stream(call).transform { sse ->
            emit(
                gson.fromJson(
                    sse.getData(),
                    ChatCompletionChunk::class.java
                )
            )
        }
    }

    private fun stream(call: Call<ResponseBody>): Flow<SSE> {

        val flow:Flow<SSE> = callbackFlow<SSE> {
            call.enqueue(object: Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val inStream = response.body()!!.byteStream()
                    val reader = BufferedReader(InputStreamReader(inStream))
                    var line: String? = reader.readLine()
                    var sse: SSE? = null
                    while(line != null) {
                        if (line.startsWith("data:")) {
                            val data: String = line.substring(5).trim()
                            sse = SSE(data)
                        } else if (line == "" && sse != null) {
                            if (sse.isDone()) {
                                break
                            }
                            trySend(sse)

                            sse = null
                        } else {

                        }
                        line = reader.readLine()
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                }

            })
            awaitClose {
                call.cancel()
            }
        }.buffer(UNLIMITED)
        return flow
    }

    override fun getConversation(id: Long): Flow<List<Message>> {
        return flow {
            chatGptDao.getConversationByChatId(id).collect {list ->
                emit(list.map { it.toMessage() })
            }
        }
    }
}