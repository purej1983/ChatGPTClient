package com.thomaslam.chatgptclient.chatecompletion.domain.use_case

data class ConfigUseCase(
    val getConfig: GetConfig,
    val saveConfig: SaveConfig
)
