package com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Chat
import com.thomaslam.chatgptclient.chatecompletion.domain.model.ChatState

@Entity(
    tableName = "Chat",
)
data class ChatEntity(
    val lastUserMessage: String = "New Chat",
    @ColumnInfo(defaultValue = "IDLE")
    val state: ChatState = ChatState.IDLE,
    @PrimaryKey val id: Long? = null
) {
    fun toChat(): Chat {
        return Chat(
            lastUserMessage = lastUserMessage,
            state = state,
            id = id
        )
    }
}
