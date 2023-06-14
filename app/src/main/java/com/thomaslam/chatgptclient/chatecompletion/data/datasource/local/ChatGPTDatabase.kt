package com.thomaslam.chatgptclient.chatecompletion.data.datasource.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ChatEntity
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ChatGptConfigEntity
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ConversationEntity
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.MessageEntity

@Database(
    entities = [ChatEntity::class, ConversationEntity::class, ChatGptConfigEntity::class, MessageEntity::class],
    version = 6,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
    ],
)
abstract class ChatGPTDatabase: RoomDatabase() {

    abstract val dao: ChatGptDao

    companion object {
        const val DATABASE_NAME = "chat_gpt_db"
        val callback = object: Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL("insert into Config (n, temperature, stream, max_tokens) values (1,1, false, 120)")
            }
        }
        val migration3To4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("insert into Config (n, temperature, stream, max_tokens) values (1,1, false, 150)")
            }
        }

        val migration5To6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `Message` (`role` TEXT NOT NULL, `content` TEXT NOT NULL, `conversationId` INTEGER NOT NULL, `id` INTEGER, PRIMARY KEY(`id`), FOREIGN KEY(`conversationId`) REFERENCES `Conversation`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_Message_conversationId` ON `Message` (`conversationId`)")
                database.execSQL("insert into Message (conversationId, role, content) select id, role,content from Conversation")
            }
        }
    }
}