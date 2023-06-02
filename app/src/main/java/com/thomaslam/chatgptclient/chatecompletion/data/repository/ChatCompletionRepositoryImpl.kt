package com.thomaslam.chatgptclient.chatecompletion.data.repository

import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.ChatGptDao
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ChatEntity
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ConversationEntity
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.ChatCompletionService
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto.ChatCompletionRequest
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Chat
import com.thomaslam.chatgptclient.chatecompletion.domain.model.ChatState
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatCompletionRepository
import com.thomaslam.chatgptclient.chatecompletion.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class ChatCompletionRepositoryImpl (
    private val chatGptDao: ChatGptDao,
    private val chatCompletionService: ChatCompletionService
) : ChatCompletionRepository {
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

    override suspend fun saveLocalMessage(chatId :Long, message: Message) {
        chatGptDao.insertConversation(
            ConversationEntity(
                role = message.role,
                content = message.content,
                chatId = chatId
            )
        )
    }

    override suspend fun create(messages: List<Message>): Resource<Message> {
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

    override fun getConversation(id: Long): Flow<List<Message>> {
        return flow {
            chatGptDao.getConversationByChatId(id).collect {list ->
                emit(list.map { it.toMessage() })
            }
        }
    }
}