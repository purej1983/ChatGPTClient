package com.thomaslam.chatgptclient.chatecompletion.util

import com.thomaslam.chatgptclient.BuildConfig
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Config
import com.thomaslam.chatgptclient.chatecompletion.domain.use_case.ConfigUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatGptConfigurationProvider @Inject constructor(
    private val useCase: ConfigUseCase
): ConfigurationProvider {

    private val scope = CoroutineScope(Dispatchers.IO)
    lateinit var config: Config
    init {

        scope.launch {
            getConfig()
        }
    }

    private suspend fun getConfig() {
        useCase.getConfig().collectLatest {
            config = it
        }
    }
    override val chatGptApiKey: String
        get() = BuildConfig.CHAT_GPT_API_KEY

    override val temperature: Float
        get() = config.temperature

    override val n: Int
        get() = config.n

    override val stream: Boolean
        get() = config.stream

    override val maxTokens: Int
        get() = config.max_tokens
}