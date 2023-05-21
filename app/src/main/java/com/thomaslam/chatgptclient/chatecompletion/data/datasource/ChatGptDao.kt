package com.thomaslam.chatgptclient.chatecompletion.data.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.thomaslam.chatgptclient.chatecompletion.data.local.entity.ChatEntity
import com.thomaslam.chatgptclient.chatecompletion.data.local.entity.ConversationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatGptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)

    @Query("UPDATE Chat SET lastUserMessage =:lastUserMessage where id=:chatId")
    suspend fun updateLastUserMessage(chatId: Long, lastUserMessage: String)

    @Query("SELECT * FROM Chat")
    suspend fun getChats(): List<ChatEntity>

    @Query("SELECT * FROM Conversation where chatId=:chatId")
    suspend fun getConversationByChatId(chatId: Long): List<ConversationEntity>
}