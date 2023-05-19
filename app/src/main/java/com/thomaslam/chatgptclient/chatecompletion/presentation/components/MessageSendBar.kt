package com.thomaslam.chatgptclient.chatecompletion.presentation.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.thomaslam.chatgptclient.R
import com.thomaslam.chatgptclient.ui.theme.ChatGPTClientTheme
import com.thomaslam.chatgptclient.ui.theme.userBackground


@Composable
fun MessageSendBar(
    modifier: Modifier
) {
    var value by remember { mutableStateOf("") }
    Row(
        modifier = modifier
    ){
        TextField(
            modifier = Modifier.weight(1f),
            value = value,
            onValueChange = { value = it} ,
            placeholder = { Text("Enter Email") },
            colors = TextFieldDefaults.textFieldColors(textColor = Color.White, placeholderColor = Color.White),
        )
        IconButton(
            modifier = Modifier.background(MaterialTheme.colors.userBackground),
            onClick = { /*TODO*/ }
        ) {
            Icon(
                painterResource(id = R.drawable.send),
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    ChatGPTClientTheme {
        MessageSendBar(
            Modifier
                .background(MaterialTheme.colors.userBackground)
        )
    }
}