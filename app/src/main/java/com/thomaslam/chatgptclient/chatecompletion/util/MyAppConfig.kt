package com.thomaslam.chatgptclient.chatecompletion.util

import com.thomaslam.chatgptclient.BuildConfig

class MyAppConfig {
    fun getChatGptApiKey(): String {
        return BuildConfig.CHAT_GPT_API_KEY
    }
}