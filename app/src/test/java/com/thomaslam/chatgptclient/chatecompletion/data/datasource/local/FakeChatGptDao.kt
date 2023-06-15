package com.thomaslam.chatgptclient.chatecompletion.data.datasource.local

import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ChatEntity
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ChatGptConfigEntity
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ConversationEntity
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ConversationWithMessages
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.MessageEntity
import com.thomaslam.chatgptclient.chatecompletion.domain.model.ChatState
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Message
import com.thomaslam.chatgptclient.chatecompletion.util.MockDataCollections
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeChatGptDao: ChatGptDao {
    private var config = mockConfig
    private val configFlow = MutableStateFlow(config)

    private var conversations = mockConversations
    private val conversationFlow = MutableStateFlow(conversations)

    private var messages = mockMessages
    private val messagesFlow = MutableStateFlow(messages)

    private var conversationsWithMessages = mockConversationWithMessages
    private val conversationWithMessagesFlow = MutableStateFlow(conversationsWithMessages)
    private var chats = mockChats
    private val chatFlow = MutableStateFlow(chats)


    private var chatAutoId: Long = 2
    private var conversationAutoId: Long = 2
    private var messageAutoId: Long = 2

    override suspend fun insertChat(chat: ChatEntity): Long {
        val newChat = chatFlow.value.toMutableList()
        newChat.add(
            ChatEntity(
                lastUserMessage = chat.lastUserMessage,
                id = ++chatAutoId
            )
        )
        chats = newChat
        chatFlow.value = chats
        return chatAutoId
    }

    override suspend fun insertConversation(conversation: ConversationEntity): Long {
        val id = conversation.id ?: run {
            ++conversationAutoId
        }

        val newConversation = conversationFlow.value.toMutableList()

        val index = newConversation.indexOfFirst { it.id == id }
        val entity = ConversationEntity(
            chatId = conversation.chatId,
            id = id
        )
        if(index == -1) {
            newConversation.add(entity)
        } else {
            newConversation[index] = entity
        }
        conversations = newConversation
        conversationFlow.value = conversations
        return id
    }

    override suspend fun insertMessage(messageEntity: MessageEntity): Long {
        val id = messageEntity.id ?: run {
            ++messageAutoId
        }

        val newMessages = messagesFlow.value.toMutableList()
        val index = newMessages.indexOfFirst { it.id == id }
        val entity = messageEntity.copy(
            id = id
        )
        if(index == -1) {
            newMessages.add(entity)
        } else {
            newMessages[index] = entity
        }
        newMessages.add(entity)

        messages = newMessages
        messagesFlow.value = messages
        return id
    }

    override fun getConfig(): Flow<ChatGptConfigEntity> {
        return configFlow
    }

    override suspend fun saveConfig(config: ChatGptConfigEntity) {
        this.config = config
        configFlow.value = this.config
    }

    override suspend fun updateLastUserMessage(chatId: Long, lastUserMessage: String) {
        val newChats = chatFlow.value.toMutableList()
        val index = newChats.indexOfFirst { it.id == chatId }
        newChats[index] = ChatEntity(lastUserMessage = lastUserMessage, id = chatId)
        chats = newChats
        chatFlow.value = chats
    }

    override suspend fun resetChatState(chatId: Long) {
        val newChats = chatFlow.value.toMutableList()
        val index = newChats.indexOfFirst { it.id == chatId }
        newChats[index] = ChatEntity(state = ChatState.IDLE, id = chatId)
        chats = newChats
        chatFlow.value = chats
    }

    override suspend fun updateChatState(chatId: Long, state: ChatState) {
        val newChats = chatFlow.value.toMutableList()
        val index = newChats.indexOfFirst { it.id == chatId }
        newChats[index] = ChatEntity(state = state, id = chatId)
        chats = newChats
        chatFlow.value = chats
    }

    override fun getChats(): Flow<List<ChatEntity>> {
        return chatFlow
    }

    override fun getConversationByChatId(chatId: Long): Flow<List<ConversationWithMessages>> {
        return conversationWithMessagesFlow
    }

    companion object {
        val mockChats = listOf(
            ChatEntity(lastUserMessage = "testMessage1", id = 1),
            ChatEntity(lastUserMessage = "testMessage2", id = 2),
        )

        val mockConversations = listOf(
            ConversationEntity(
                id = 1,
                chatId = 1
            ),
            ConversationEntity(
                id = 2,
                chatId = 1
            )
        )

        val mockMessages = listOf(
            MessageEntity(
                role = MockDataCollections.userMessage1.role,
                content = MockDataCollections.userMessage1.content,
                conversationId = 1
            ),
            MessageEntity(
                role = MockDataCollections.assistantMessage1.role,
                content = MockDataCollections.assistantMessage1.content,
                conversationId = 2
            )
        )
        val mockConversationWithMessages = listOf(
            ConversationWithMessages(
                conversation = mockConversations.first { it.id == 1L },
                messages = mockMessages.filter { it.conversationId == 1L }
            ),
            ConversationWithMessages(
                conversation = mockConversations.first { it.id == 2L },
                messages = mockMessages.filter { it.conversationId == 2L }
            ),
        )

        val mockConfig = ChatGptConfigEntity(
            id = 1,
            n = 2,
            temperature = 0.8f,
            stream = true,
            max_tokens = 120
        )
    }

    override suspend fun insertConversationWithMessage(
        chatId: Long,
        conversationId: Long?,
        messages: List<Message>
    ): Long {
        val id = super.insertConversationWithMessage(chatId, conversationId, messages)
        conversationsWithMessages = conversations.map {
            ConversationWithMessages(
                conversation = it,
                messages = this.messages.filter { message -> message.conversationId == it.id }
            )
        }
        conversationWithMessagesFlow.value = conversationsWithMessages

        return id
    }
}