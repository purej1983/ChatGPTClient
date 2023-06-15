package com.thomaslam.chatgptclient.chatecompletion.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ChatEntity
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ChatGptConfigEntity
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ConversationEntity
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.ConversationWithMessagesEntity
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity.MessageEntity
import com.thomaslam.chatgptclient.chatecompletion.domain.model.ChatState
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatGptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(messageEntity: MessageEntity): Long

    @Query("Select * from Config limit 1")
    fun getConfig(): Flow<ChatGptConfigEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConfig(config: ChatGptConfigEntity)

    @Query("UPDATE Chat SET lastUserMessage =:lastUserMessage where id=:chatId")
    suspend fun updateLastUserMessage(chatId: Long, lastUserMessage: String)

    @Query("UPDATE Chat SET state = 'IDLE' where id=:chatId and state != 'ERROR'")
    suspend fun resetChatState(chatId: Long)

    @Query("UPDATE Chat SET state = :state where id=:chatId")
    suspend fun updateChatState(chatId: Long, state: ChatState)

    @Query("SELECT * FROM Chat")
    fun getChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM Conversation where chatId=:chatId")
    fun getConversationByChatId(chatId: Long): Flow<List<ConversationWithMessagesEntity>>

    @Transaction
    suspend fun insertConversationWithMessage(chatId: Long, conversationId: Long?,  messages: List<Message>): Long {
        val conversationEntity = if(conversationId != null)
            ConversationEntity(
                id = conversationId,
                chatId = chatId
            ) else
            ConversationEntity(
                chatId = chatId
            )
        val id = insertConversation(conversationEntity)
        for(message in messages) {
            insertMessage(
                MessageEntity(
                    role = message.role,
                    content = message.content,
                    conversationId = id,
                    id = message.id
                )
            )
        }
        return id
    }
}