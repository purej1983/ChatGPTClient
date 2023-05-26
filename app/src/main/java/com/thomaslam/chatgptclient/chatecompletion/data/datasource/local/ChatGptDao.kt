package com.thomaslam.chatgptclient.chatecompletion.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ChatEntity
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ConversationEntity
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
    fun getChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM Conversation where chatId=:chatId")
    fun getConversationByChatId(chatId: Long): Flow<List<ConversationEntity>>
}