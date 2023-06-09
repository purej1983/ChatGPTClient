package com.thomaslam.chatgptclient.chatecompletion.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Chat
import com.thomaslam.chatgptclient.chatecompletion.domain.model.ChatState
import com.thomaslam.chatgptclient.chatecompletion.presentation.components.ChatItem
import com.thomaslam.chatgptclient.chatecompletion.presentation.util.Screen
import com.thomaslam.chatgptclient.ui.theme.ChatGPTClientTheme
import com.thomaslam.chatgptclient.ui.theme.separatorColor
import com.thomaslam.chatgptclient.ui.theme.userBackground
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by remember {
        viewModel.state
    }


    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is ChatViewModel.UiEvent.NavigateToChat -> {
                    navController.navigate(
                        Screen.ConversationScreen.route + "?chatId=${event.id}"
                    )
                }
                is ChatViewModel.UiEvent.NavigateToConfig -> {
                    navController.navigate(
                        Screen.ConfigScreen.route
                    )
                }
            }
        }
    }
    ChatContent(
        state = state,
        floatingActionButtonOnClick = { viewModel.newChat() },
        menuButtonOnClick = { viewModel.goToConfig() },
        chatItemOnClick = { chatId -> viewModel.goToChat(chatId)}
    )
}

@Composable
fun ChatContent(
    state: ChatScreenUIState,
    floatingActionButtonOnClick: () -> Unit,
    menuButtonOnClick: () -> Unit,
    chatItemOnClick: (Long) -> Unit
    
) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    floatingActionButtonOnClick()
                },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        },
        scaffoldState = scaffoldState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(MaterialTheme.colors.userBackground)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = {
                        menuButtonOnClick()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Sort"
                    )
                }
            }
            LazyColumn(modifier =
            Modifier.fillMaxWidth()
            ) {
                items(state.chats) { item ->
                    ChatItem(item = item) {
                        item.id?.let { chatId ->
                            chatItemOnClick(chatId)
                        }
                    }
                    Divider(
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colors.separatorColor
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    ChatGPTClientTheme {
        ChatContent(
            state = ChatScreenUIState(
                chats = listOf(
                    Chat(
                        lastUserMessage = "Demo Message1",
                        id = 1L
                    ),
                    Chat(
                        state = ChatState.NEW_MESSAGE,
                        lastUserMessage = "New Message2",
                        id = 2L
                    ),
                    Chat(
                        state = ChatState.ERROR,
                        lastUserMessage = "Error Message3",
                        id = 3L
                    ),
                    Chat(
                        state = ChatState.LOADING,
                        lastUserMessage = "LOADING",
                        id = 3L
                    )
                )
            ),
            floatingActionButtonOnClick = {  },
            menuButtonOnClick = {  },
            chatItemOnClick = {}
        )
    }
}