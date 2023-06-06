package com.thomaslam.chatgptclient.chatecompletion.presentation

sealed class ConfigScreenEvent {
    data class NValueChange(val value: Int) : ConfigScreenEvent()
    data class TemperatureValueChange(val value: Float) : ConfigScreenEvent()
    data class StreamValueChange(val value: Boolean) : ConfigScreenEvent()
    data class MaxTokensValueChange(val value: Int) : ConfigScreenEvent()
    object SaveConfig : ConfigScreenEvent()
}