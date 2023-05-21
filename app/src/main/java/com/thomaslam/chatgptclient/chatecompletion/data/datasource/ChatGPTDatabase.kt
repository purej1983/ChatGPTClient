package com.thomaslam.chatgptclient.chatecompletion.data.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.thomaslam.chatgptclient.chatecompletion.data.local.entity.ChatEntity
import com.thomaslam.chatgptclient.chatecompletion.data.local.entity.ConversationEntity

@Database(
    entities = [ChatEntity::class, ConversationEntity::class],
    version = 1
)
abstract class ChatGPTDatabase: RoomDatabase() {

    abstract val dao: ChatGptDao

    companion object {
        const val DATABASE_NAME = "chat_gpt_db"
    }
}