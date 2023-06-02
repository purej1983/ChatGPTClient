package com.thomaslam.chatgptclient.chatecompletion.data.repository

import com.thomaslam.chatgptclient.chatecompletion.domain.model.Chat
import com.thomaslam.chatgptclient.chatecompletion.domain.model.ChatState
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatCompletionRepository
import com.thomaslam.chatgptclient.chatecompletion.domain.util.Resource
import com.thomaslam.chatgptclient.chatecompletion.util.MockDataCollections
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeChatCompletionRepository: ChatCompletionRepository {
    override fun getChats(): Flow<List<Chat>> {
        return flow{
          emit(MockDataCollections.chats)
        }
    }

    override suspend fun newChat(): Long {
        return 2L
    }

    override suspend fun updateLastUserMessage(chatId: Long, content: String) {

    }

    override suspend fun resetChatState(chatId: Long) {

    }

    override suspend fun updateChatState(chatId: Long, state: ChatState) {

    }

    override suspend fun saveLocalMessage(chatId: Long, message: Message) {

    }

    override suspend fun create(messages: List<Message>): Resource<Message> {
        return Resource.Success(MockDataCollections.assistantMessage1)
    }

    override fun getConversation(id: Long): Flow<List<Message>> {
        return flow {
            emit(
                listOf(
                    MockDataCollections.userMessage1,
                    MockDataCollections.assistantMessage1
                )
            )
        }
    }
}