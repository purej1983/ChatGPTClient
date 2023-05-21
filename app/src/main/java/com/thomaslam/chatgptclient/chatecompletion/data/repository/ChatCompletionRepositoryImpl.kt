package com.thomaslam.chatgptclient.chatecompletion.data.repository

import com.thomaslam.chatgptclient.chatecompletion.data.datasource.ChatGptDao
import com.thomaslam.chatgptclient.chatecompletion.data.local.entity.ChatEntity
import com.thomaslam.chatgptclient.chatecompletion.data.local.entity.ConversationEntity
import com.thomaslam.chatgptclient.chatecompletion.data.remote.ChatCompletionService
import com.thomaslam.chatgptclient.chatecompletion.data.remote.dto.ChatCompletionRequest
import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Chat
import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatCompletionRepository

class ChatCompletionRepositoryImpl (
    private val chatGptDao: ChatGptDao,
    private val chatCompletionService: ChatCompletionService
) : ChatCompletionRepository {
    override suspend fun getChats(): List<Chat> {
        return chatGptDao.getChats().map {
            it.toChat()
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

    override suspend fun getConversation(id: Long): List<Message> {
        return chatGptDao.getConversationByChatId(id).map {
            it.toMessage()
        }
    }
}