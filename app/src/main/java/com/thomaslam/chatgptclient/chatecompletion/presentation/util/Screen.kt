package com.thomaslam.chatgptclient.chatecompletion.presentation.util

sealed class Screen(val route: String) {
    object ChatScreen: Screen("chat_screen")
    object ConversationScreen: Screen("conversation_screen")
    object ConfigScreen: Screen("config_screen")
}
