package com.thomaslam.chatgptclient.chatecompletion

import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.ChatGptDao
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.FakeChatGptDao
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.FakeChatCompletionService
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.ChatCompletionService
import com.thomaslam.chatgptclient.chatecompletion.data.repository.ChatCompletionRepositoryImpl
import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatCompletionRepository
import com.thomaslam.chatgptclient.chatecompletion.util.MockDataCollections
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class ChatCompletionRepositoryTest {
    private lateinit var repository: ChatCompletionRepository
    private lateinit var dao: ChatGptDao
    private lateinit var api: ChatCompletionService

    @Before
    fun setup() {
        dao = FakeChatGptDao()
        api = FakeChatCompletionService()
        repository = ChatCompletionRepositoryImpl(dao, api)
    }

    @Test
    fun testGetChats() {
        runBlocking {
            val actual = repository.getChats()
            assert(actual.size == FakeChatGptDao.mockChats.size)
            actual.forEachIndexed {
                index, chat ->
                    run {
                        val expectChat = FakeChatGptDao.mockChats[index]
                        assert(chat.id == expectChat.id)
                        assert(chat.lastUserMessage == expectChat.lastUserMessage)
                    }
            }
        }
    }

    @Test
    fun testNewChats() {
        runBlocking {
            val beforeInsert = repository.getChats()
            assert(beforeInsert.size == 2)
            val newId = repository.newChat()
            assert(newId == 3L)
            val afterInsert = repository.getChats()
            assert(afterInsert.size == 3)
            val lastItem = afterInsert.last()
            assert(lastItem.id == 3L)
            assert(lastItem.lastUserMessage == "New Chat")
        }
    }

    @Test
    fun testUpdateLastUserMessage() {
        runBlocking {
            val id = 2L
            val updateMessage = "Test Update Message"
            repository.updateLastUserMessage(id, updateMessage)
            val chats = dao.getChats()
            val filtered = chats.filter { it.id == id }.first()
            assert(filtered.id == id)
            assert(filtered.lastUserMessage == updateMessage)
        }
    }

    @Test
    fun testGetConversation() {
        runBlocking {
            val actual = repository.getConversation(1L)
            assert(actual.size == FakeChatGptDao.mockConversations.size)
            actual.forEachIndexed {
                    index, conversation ->
                run {
                    val expectConversation = FakeChatGptDao.mockConversations[index]
                    assert(conversation.role == expectConversation.role)
                    assert(conversation.content == expectConversation.content)
                }
            }
        }
    }

    @Test
    fun testSaveLocalMessage() {
        runBlocking {
            val id = 2L
            val role = "user"
            val content = "How's attraction in Birmingham"
            val beforeInsert = repository.getConversation(id)
            assert(beforeInsert.size == 2)
            repository.saveLocalMessage(id, Message(role, content))
            val afterInsert = repository.getConversation(id)
            assert(afterInsert.size == 3)
            assert(afterInsert.last().role == role)
            assert(afterInsert.last().content == content)
        }
    }

    @Test
    fun testCreateCompletion() {
        runBlocking {
            val messages = listOf(
                MockDataCollections.userMessage1,
                MockDataCollections.assistantMessage1,
                MockDataCollections.userMessage2
            )
            val reponse = repository.create(messages)
            assert(reponse.role == MockDataCollections.assistantMessage2.role)
            assert(reponse.content == MockDataCollections.assistantMessage2.content)
        }
    }
}