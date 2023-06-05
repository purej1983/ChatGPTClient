package com.thomaslam.chatgptclient.chatecompletion.presentation.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thomaslam.chatgptclient.R
import com.thomaslam.chatgptclient.ui.theme.ChatGPTClientTheme
import com.thomaslam.chatgptclient.ui.theme.iconTintColor
import com.thomaslam.chatgptclient.ui.theme.textColor
import com.thomaslam.chatgptclient.ui.theme.textFieldBackground
import com.thomaslam.chatgptclient.ui.theme.userBackground


@Composable
fun MessageSendBar(
    modifier: Modifier,
    onMessageButtonClick: (String) -> Unit
) {
    var value by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Row(
        modifier = modifier
            .border( width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(5.dp)
            )
    ){
        TextField(
            modifier = Modifier
                .weight(1f),
            value = value,
            onValueChange = { value = it} ,
            placeholder = { Text("Enter your message") },
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colors.textColor,
                placeholderColor = MaterialTheme.colors.textColor,
                backgroundColor = MaterialTheme.colors.textFieldBackground
            ),
        )
        IconButton(
            modifier = Modifier.background(MaterialTheme.colors.userBackground),
            onClick = {
                onMessageButtonClick(value)
                focusManager.clearFocus()
                value = ""
            }
        ) {
            Icon(
                painterResource(id = R.drawable.send),
                contentDescription = null,
                tint = MaterialTheme.colors.iconTintColor
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
                .background(MaterialTheme.colors.userBackground),
            {

            }
        )
    }
}