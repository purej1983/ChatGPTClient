package com.thomaslam.chatgptclient.chatecompletion.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
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
import com.thomaslam.chatgptclient.chatecompletion.presentation.components.AssitantMessageItem
import com.thomaslam.chatgptclient.chatecompletion.presentation.components.MessageSendBar
import com.thomaslam.chatgptclient.chatecompletion.presentation.components.UserMessageItem
import com.thomaslam.chatgptclient.ui.theme.ChatGPTClientTheme
import com.thomaslam.chatgptclient.ui.theme.assistantBackground
import com.thomaslam.chatgptclient.ui.theme.userBackground
import kotlinx.coroutines.flow.collectLatest

@Composable
fun Conversationscreen(
    viewModel: ConversationViewModel = hiltViewModel()
) {
    val state by remember {
        viewModel.state
    }

    val scaffoldState = rememberScaffoldState()
    val listState = rememberLazyListState()
    LaunchedEffect(state.messages) {
        listState.scrollToItem(state.messages.size)
    }
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is ConversationViewModel.UiEvent.ShowSnackBar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
            }
        }
    }

    Scaffold(scaffoldState = scaffoldState)
    {
        Column(
            modifier  = Modifier
                .fillMaxHeight()
                .background(MaterialTheme.colors.userBackground)
                .padding(it),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LazyColumn(modifier =
            Modifier
                .fillMaxWidth()
                .weight(1f)
            ) {
                items(state.messages) { item ->
                    if (item.role == "user") {
                        UserMessageItem(
                            content = item.content,
                            backgroundColor = MaterialTheme.colors.userBackground
                        )
                    } else {
                        AssitantMessageItem(
                            content = item.content,
                            backgroundColor = MaterialTheme.colors.assistantBackground
                        )
                    }
                }
            }
            if(state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(0.dp, 8.dp)
                )
            }
            MessageSendBar(
                modifier = Modifier
                    .background(MaterialTheme.colors.userBackground),
                onMessageButtonClick = {
                    viewModel.send(it)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    ChatGPTClientTheme {
        Conversationscreen()
    }
}