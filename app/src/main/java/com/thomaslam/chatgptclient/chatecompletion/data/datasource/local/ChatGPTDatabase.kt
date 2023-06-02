package com.thomaslam.chatgptclient.chatecompletion.data.datasource.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ChatEntity
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ConversationEntity

@Database(
    entities = [ChatEntity::class, ConversationEntity::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class ChatGPTDatabase: RoomDatabase() {

    abstract val dao: ChatGptDao

    companion object {
        const val DATABASE_NAME = "chat_gpt_db"
    }
}