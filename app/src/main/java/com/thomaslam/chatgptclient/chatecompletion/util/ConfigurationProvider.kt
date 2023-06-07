package com.thomaslam.chatgptclient.chatecompletion.util

interface ConfigurationProvider {
    val chatGptApiKey: String
    val temperature: Float
    val n: Int
    val stream: Boolean
    val maxTokens: Int
}