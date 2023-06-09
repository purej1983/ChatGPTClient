package com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Message

@Entity(
    tableName = "Message",
    foreignKeys = [ForeignKey(entity = ConversationEntity::class,
        parentColumns = ["id"],
        childColumns = ["conversationId"],
        onDelete = ForeignKey.CASCADE)]
)
data class MessageEntity(
    val role: String,
    val content: String,
    @ColumnInfo(index = true)
    val conversationId: Long,
    @PrimaryKey val id: Long? = null,
) {
    fun toMessage(): Message {
        return Message(
            role = role,
            content = content
        )
    }
}
