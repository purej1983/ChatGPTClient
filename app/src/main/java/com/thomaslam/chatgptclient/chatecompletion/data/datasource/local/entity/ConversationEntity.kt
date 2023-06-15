package com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Conversation",
    foreignKeys = [ForeignKey(entity = ChatEntity::class,
    parentColumns = ["id"],
    childColumns = ["chatId"],
    onDelete = ForeignKey.CASCADE)]
)
data class ConversationEntity(
    @ColumnInfo(index = true)
    val chatId: Long,
    @PrimaryKey val id: Long? = null,
)
