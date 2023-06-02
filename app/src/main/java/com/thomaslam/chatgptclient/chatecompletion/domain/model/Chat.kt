package com.thomaslam.chatgptclient.chatecompletion.domain.model

enum class ChatState {
    IDLE,
    LOADING,
    ERROR,
    NEW_MESSAGE
}

data class Chat(
    val state: ChatState = ChatState.IDLE,
    val lastUserMessage: String,
    val id: Long?
)


