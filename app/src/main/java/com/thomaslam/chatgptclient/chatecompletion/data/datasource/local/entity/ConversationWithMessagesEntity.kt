package com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Message

data class ConversationWithMessagesEntity(
    @Embedded val conversation: ConversationEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "conversationId"
    )
    val messages: List<MessageEntity>
) {
    fun toMessage(): Message {
        val message = messages[conversation.selectedMessageIdx]
        return Message(
            role = message.role,
            content = message.content
        )
    }
}
