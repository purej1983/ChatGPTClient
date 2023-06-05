package com.thomaslam.chatgptclient.chatecompletion.util

import com.thomaslam.chatgptclient.BuildConfig

class ChatGptConfigurationProvider: ConfigurationProvider {
    override val chatGptApiKey: String
        get() = BuildConfig.CHAT_GPT_API_KEY

    override val temperature: Float
        get() = 1.0f

    override val n: Int
        get() = 1

    override val stream: Boolean
        get() = false
}