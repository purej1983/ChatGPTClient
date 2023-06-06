package com.thomaslam.chatgptclient.chatecompletion.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thomaslam.chatgptclient.chatecompletion.domain.model.Config
import com.thomaslam.chatgptclient.ui.theme.ChatGPTClientTheme
import kotlin.math.roundToInt

@Composable
fun ConfigScreen(
    viewModel: ConfigScreenViewModel = hiltViewModel()
) {
    val state by remember { viewModel.state }
    state.config?.let {
        ConfigContent(
            config = it,
            nValueChange = { value -> viewModel.onEvent(ConfigScreenEvent.NValueChange(value)) },
            temperatureValueChange = { value -> viewModel.onEvent(ConfigScreenEvent.TemperatureValueChange(value)) },
            streamValueChange = { value -> viewModel.onEvent(ConfigScreenEvent.StreamValueChange(value)) },
            maxTokensValueChange = { value -> viewModel.onEvent(ConfigScreenEvent.MaxTokensValueChange(value)) },
            saveConfig = { viewModel.onEvent(ConfigScreenEvent.SaveConfig) }
        )
    }

}

@Composable
private fun ConfigContent(
    config: Config,
    nValueChange: (Int) -> Unit,
    temperatureValueChange: (Float) -> Unit,
    streamValueChange: (Boolean) -> Unit,
    maxTokensValueChange: (Int) -> Unit,
    saveConfig: () -> Unit
) {
    val scrollState = rememberScrollState()
    val nValue = remember {
        mutableStateOf(config.n)
    }
    val temperatureValue = remember {
        mutableStateOf(config.temperature)
    }
    val maxTokensValue = remember {
        mutableStateOf(config.max_tokens)
    }
    Column(
        modifier = Modifier
            .padding(20.dp)
            .verticalScroll(scrollState)
    ) {
        Row() {
            Text(
                text = "n"
            )
            Text(text = "${nValue.value}")
        }
        Slider(value = nValue.value.toFloat(), onValueChange = { nValue.value = it.toInt() } , onValueChangeFinished = { nValueChange(nValue.value) }, valueRange = 1f..10f)
        Row() {
            Text(
                text = "temperature"
            )
            Text(text = "${temperatureValue.value}")
        }
        Slider(value = temperatureValue.value, onValueChange = { temperatureValue.value = String.format("%.1f",it).toFloat() } , onValueChangeFinished = { temperatureValueChange(temperatureValue.value) }, valueRange = 0f..2f)
        Text(
            text = "stream"
        )
        Switch(checked = config.stream, onCheckedChange = { streamValueChange(it) })
        Row() {
            Text(
                text = "max_tokens"
            )
            Text(text = "${maxTokensValue.value}")
        }
        Slider(value = maxTokensValue.value.toFloat(), onValueChange = { maxTokensValue.value = (it/10f).roundToInt() * 10 } , onValueChangeFinished = { maxTokensValueChange(maxTokensValue.value) }, valueRange = 100f..250f)

        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            Button(

                onClick = { saveConfig() }
            ) {
                Text(text = "Save")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    ChatGPTClientTheme {
        ConfigContent(
            config = Config(
                n = 5,
                temperature = 1f,
                stream = true,
                max_tokens = 120
            ),
            nValueChange = {},
            temperatureValueChange = {},
            streamValueChange = {},
            maxTokensValueChange = {},
            saveConfig = {}
        )
    }
}