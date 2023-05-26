package com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.thomaslam.chatgptclient.chatecompletion.domain.entity.Chat

@Entity(
    tableName = "Chat"
)
data class ChatEntity(
    val lastUserMessage: String = "New Chat",
    @PrimaryKey val id: Long? = null
) {
    fun toChat(): Chat {
        return Chat(
            lastUserMessage = lastUserMessage,
            id = id
        )
    }
}
