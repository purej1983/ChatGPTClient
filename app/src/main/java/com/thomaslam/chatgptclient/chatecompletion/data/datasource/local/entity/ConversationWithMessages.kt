package com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Message

data class ConversationWithMessages(
    @Embedded val conversation: ConversationEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "conversationId"
    )
    val messages: List<MessageEntity>
) {
    fun toMessage(): Message {
        val message = messages.first()
        return Message(
            role = message.role,
            content = message.content
        )
    }
}
