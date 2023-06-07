package com.thomaslam.chatgptclient.chatecompletion.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Config
import com.thomaslam.chatgptclient.chatecompletion.domain.use_case.ConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigScreenViewModel@Inject constructor(
    private val useCase: ConfigUseCase
): ViewModel(){
    private val _state = mutableStateOf(ConfigScreenUIState())
    val state: State<ConfigScreenUIState> = _state
    init {
        getConfig()
    }

    private fun getConfig() {
        viewModelScope.launch {
            useCase.getConfig().collectLatest {
                _state.value = state.value.copy(
                    config = it
                )
            }
        }
    }

    private fun replaceNewConfigState(newConfig: Config) {
        _state.value = state.value.copy(
            config = newConfig
        )
    }

    fun onEvent(event: ConfigScreenEvent) {
        state.value.config?.let {
            when(event) {
                is ConfigScreenEvent.NValueChange -> {
                    val newConfig = it.copy(
                        n = event.value
                    )
                    replaceNewConfigState(newConfig)
                }
                is ConfigScreenEvent.TemperatureValueChange -> {
                    val newConfig = it.copy(
                        temperature = event.value
                    )
                    replaceNewConfigState(newConfig)
                }
                is ConfigScreenEvent.StreamValueChange -> {
                    val newConfig = it.copy(
                        stream = event.value
                    )
                    replaceNewConfigState(newConfig)
                }
                is ConfigScreenEvent.MaxTokensValueChange -> {
                    val newConfig = it.copy(
                        max_tokens = event.value
                    )
                    replaceNewConfigState(newConfig)
                }
                is ConfigScreenEvent.SaveConfig -> {
                    viewModelScope.launch {
                        useCase.saveConfig(Config(
                            n = it.n,
                            temperature = it.temperature,
                            stream = it.stream,
                            max_tokens = it.max_tokens
                        ))
                    }
                }
            }
        }

    }
}