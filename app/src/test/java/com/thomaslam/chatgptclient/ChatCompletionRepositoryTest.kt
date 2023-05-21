package com.thomaslam.chatgptclient

import com.thomaslam.chatgptclient.chatecompletion.data.datasource.ChatGptDao
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.FakeChatGptDao
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.FakeChatCompletionService
import com.thomaslam.chatgptclient.chatecompletion.data.remote.ChatCompletionService
import com.thomaslam.chatgptclient.chatecompletion.data.repository.ChatCompletionRepositoryImpl
import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatCompletionRepository
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
                Message(
                    role = "user",
                    content = "Top 5 attractions in Birmingham"
                ),
                Message(
                    role = "assistant",
                    content = "1. Cadbury World - a chocolate-themed attraction that offers tours, demonstrations, and interactive exhibits. Located in Bournville, about 6 miles from Birmingham city center.\\n\\n2. Warwick Castle - a medieval fortress with extensive grounds, fortress walls, moat, and dungeons. Located in Warwick, about 22 miles southeast of Birmingham.\\n\\n3. Drayton Manor Theme Park - a large amusement park with rides, roller coasters, attractions, and a zoo. Located in Tamworth, about 16 miles northeast of Birmingham.\\n\\n4. Black Country Living Museum - an open-air museum that showcasing life during the industrial revolution, with reconstructed buildings, coal mines, and a tram system. Located in Dudley, about 10 miles west of Birmingham.\\n\\n5. The National Sea Life Centre - an aquarium with over 60 displays of marine life, including sharks, sea turtles, and tropical fish. Located in Birmingham city center."
                ),
                Message(
                    role = "user",
                    content = "how about in manchester?"
                )
            )
            val reponse = repository.create(messages)
            assert(reponse.role == FakeChatCompletionService.mockResponse.choices.first().message.role)
            assert(reponse.content == FakeChatCompletionService.mockResponse.choices.first().message.content)
        }
    }
}