package com.thomaslam.chatgptclient.chatecompletion.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.thomaslam.chatgptclient.chatecompletion.presentation.components.AssitantMessageItem
import com.thomaslam.chatgptclient.chatecompletion.presentation.components.MessageSendBar
import com.thomaslam.chatgptclient.chatecompletion.presentation.components.UserMessageItem
import com.thomaslam.chatgptclient.ui.theme.ChatGPTClientTheme
import com.thomaslam.chatgptclient.ui.theme.assistantBackground
import com.thomaslam.chatgptclient.ui.theme.userBackground

@Composable
fun Conversationscreen(
    navController: NavController
) {
    val list: List<String> = listOf("I am a boy", "You are a girl", "She is hahaha")
    var i = 0
    Column(
        modifier  = Modifier
            .fillMaxHeight()
            .background(MaterialTheme.colors.userBackground),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(modifier =
            Modifier.fillMaxWidth()
        ) {
            items(list) { item ->
                if (i % 2 == 0) {
                    UserMessageItem(
                        content = item,
                        backgroundColor = MaterialTheme.colors.userBackground
                    )
                } else {
                    AssitantMessageItem(
                        content = item,
                        backgroundColor = MaterialTheme.colors.assistantBackground
                    )
                }
                i++
            }
        }
        MessageSendBar(
            modifier = Modifier
                .weight(1f, false)
                .background(MaterialTheme.colors.userBackground),
            onMessageButtonClick = {
                navController.navigateUp()
            }
        )

    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    ChatGPTClientTheme {
        Conversationscreen(rememberNavController())
    }
}