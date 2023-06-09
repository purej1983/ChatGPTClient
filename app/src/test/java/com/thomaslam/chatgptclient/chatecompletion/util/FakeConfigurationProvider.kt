package com.thomaslam.chatgptclient.chatecompletion.util

class FakeConfigurationProvider: ConfigurationProvider {
    override val chatGptApiKey: String
        get() = "Testing API Key"
    override val temperature: Float
        get() = 1f
    override val n: Int
        get() = 1
    override val stream: Boolean
        get() = false
    override val maxTokens: Int
        get() = 120
}