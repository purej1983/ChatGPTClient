package com.thomaslam.chatgptclient.chatecompletion.data.datasource.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ChatEntity
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ChatGptConfigEntity
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ConversationEntity

@Database(
    entities = [ChatEntity::class, ConversationEntity::class, ChatGptConfigEntity::class],
    version = 5,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 4, to = 5)
    ],
)
abstract class ChatGPTDatabase: RoomDatabase() {

    abstract val dao: ChatGptDao

    companion object {
        const val DATABASE_NAME = "chat_gpt_db"
        val migration3To4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("insert into Config (n, temperature, stream, max_tokens) values (1,1, false, 150)")
            }
        }
    }
}