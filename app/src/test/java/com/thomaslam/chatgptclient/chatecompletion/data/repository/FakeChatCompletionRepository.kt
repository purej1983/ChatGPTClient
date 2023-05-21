package com.thomaslam.chatgptclient.chatecompletion.data.repository

import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Chat
import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Message
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatCompletionRepository

class FakeChatCompletionRepository: ChatCompletionRepository {
    override suspend fun getChats(): List<Chat> {
        return listOf(
            Chat(lastUserMessage = "New Chat", id = 1)
        )
    }

    override suspend fun newChat(): Long {
        return 2L
    }

    override suspend fun updateLastUserMessage(chatId: Long, content: String) {

    }

    override suspend fun saveLocalMessage(chatId: Long, message: Message) {

    }

    override suspend fun create(messages: List<Message>): Message {
        return Message(
            role = "assistant",
            content = "Here are 5 top attractions in Manchester:\\n\\n1. Old Trafford - home to Manchester United Football Club, Old Trafford is a must-visit for sports fans and offers guided tours of the stadium, museum exhibits, and more.\\n\\n2. The Manchester Museum - a fascinating and diverse collection of natural history, archeology, and art objects from cultures all over the world.\\n\\n3. The John Rylands Library - a stunning neo-gothic building that houses one of the most significant collections of rare books and manuscripts in the UK, with exhibits and guided tours.\\n\\n4. The Science and Industry Museum - one of Manchester's most popular attractions, featuring interactive exhibits, demonstrations, and galleries that explore the science and innovation that shaped the industrial revolution and the modern world.\\n\\n5. The Northern Quarter - Manchester's vibrant cultural hub, known for its independent boutiques, trendy cafes and bars, street art, and vibrant music and arts scene."
        )
    }

    override suspend fun getConversation(id: Long): List<Message> {
        return listOf(
            Message(
                role = "user",
                content = ""
            ),
            Message(
                role = "assistant",
                content = ""
            )
        )
    }
}