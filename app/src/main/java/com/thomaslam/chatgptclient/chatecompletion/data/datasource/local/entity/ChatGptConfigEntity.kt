package com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Config

@Entity(
    tableName = "Config",
)
data class ChatGptConfigEntity(
    @PrimaryKey
    val id: Short? = null,
    val n: Int = 1,
    val temperature: Float = 1f,
    val stream: Boolean = false,
    val max_tokens: Int = 150
) {
    fun toConfig(): Config {
        return Config(
            n = n,
            temperature = temperature,
            stream = stream,
            max_tokens = max_tokens
        )
    }
}
