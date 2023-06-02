package com.thomaslam.chatgptclient.chatecompletion.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Chat
import com.thomaslam.chatgptclient.chatecompletion.domain.model.ChatState
import com.thomaslam.chatgptclient.ui.theme.ChatGPTClientTheme

@Composable
fun ChatItem(item: Chat, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(8.dp)
    ) {
        Text(
            text = item.lastUserMessage,
            modifier = Modifier
                .clickable { onClick() }
                .weight(1f),
            color = Color.White
        )
        when(item.state) {
            ChatState.LOADING -> {
                CircularProgressIndicator()
            }
            ChatState.ERROR -> {
                Text(
                    text = "E",
                    color = Color.White,
                    modifier = Modifier.background(Color.Red, CircleShape)
                )
            }
            ChatState.NEW_MESSAGE -> {
                Text(
                    text = "New",
                    color = Color.White,
                    modifier = Modifier
                        .background(Color.Green, RoundedCornerShape(8.dp))
                        .padding(4.dp)
                )
            }
            else -> {}
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingPreview() {
    ChatGPTClientTheme {
        val chat = Chat(ChatState.NEW_MESSAGE, "Top 5 attractions in UK", 1L)
        ChatItem(item = chat) {

        }
    }
}
