package com.thomaslam.chatgptclient.chatecompletion.presentation.components

import android.annotation.SuppressLint
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.thomaslam.chatgptclient.chatecompletion.presentation.ChatScreen
import com.thomaslam.chatgptclient.chatecompletion.presentation.ConfigScreen
import com.thomaslam.chatgptclient.chatecompletion.presentation.Conversationscreen
import com.thomaslam.chatgptclient.chatecompletion.presentation.NavigationViewModel
import com.thomaslam.chatgptclient.chatecompletion.presentation.util.Screen
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CustomNavigation(
    viewModel: NavigationViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is NavigationViewModel.UiEvent.ShowSnackBarEvent -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }


    Scaffold(
        scaffoldState = scaffoldState
    ) {
        NavHost(navController = navController, startDestination = Screen.ChatScreen.route) {
            composable(route = Screen.ChatScreen.route) {
                ChatScreen(navController = navController)
            }
            composable(
                route = Screen.ConversationScreen.route + "?chatId={chatId}",
                arguments = listOf(
                    navArgument(
                        name = "chatId"
                    ) {
                        type = NavType.LongType
                        defaultValue = -1
                    }
                )
            ) {
                Conversationscreen()
            }
            composable(route = Screen.ConfigScreen.route) {
                ConfigScreen()
            }
        }
    }

}