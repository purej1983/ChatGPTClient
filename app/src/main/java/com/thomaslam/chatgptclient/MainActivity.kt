package com.thomaslam.chatgptclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.thomaslam.chatgptclient.chatecompletion.presentation.ChatScreen
import com.thomaslam.chatgptclient.chatecompletion.presentation.Conversationscreen
import com.thomaslam.chatgptclient.chatecompletion.presentation.util.Screen
import com.thomaslam.chatgptclient.ui.theme.ChatGPTClientTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatGPTClientTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
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
                    }
                }

            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ChatGPTClientTheme {

    }
}