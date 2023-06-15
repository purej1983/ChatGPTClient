package com.thomaslam.chatgptclient.chatecompletion.domain.model

data class ConversationWithSelectMessage(
    val conversationId: Long,
    val selectMessage: Message,
    val selectMessageIdx: Int,
    val totalMessage: Int
)
