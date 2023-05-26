package com.thomaslam.chatgptclient.chatecompletion.data.repository

import androidx.room.Entity
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.ChatGptDao
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ChatEntity
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ConversationEntity
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.ChatCompletionService
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto.ChatCompletionRequest
import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Chat
import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatCompletionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

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

    override suspend fun saveLocalMessage(chatId :Long, message: Message) {
        chatGptDao.insertConversation(
            ConversationEntity(
                role = message.role,
                content = message.content,
                chatId = chatId
            )
        )
    }

    override suspend fun create(messages: List<Message>): Message {
        try {
            val response = chatCompletionService.createChatCompletion(
                ChatCompletionRequest(
                    messages = messages
                )
            )
            return response.choices.first().message
        } catch (e: Exception) {
            throw e
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