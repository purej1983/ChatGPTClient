package com.thomaslam.chatgptclient.chatecompletion.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.thomaslam.chatgptclient.ui.theme.ChatGPTClientTheme
import com.thomaslam.chatgptclient.ui.theme.assistantBackground
import com.thomaslam.chatgptclient.ui.theme.iconTintColor
import com.thomaslam.chatgptclient.ui.theme.textColor

@Composable
fun PageControl(index: Int, total: Int, onPreviousClick: ()->Unit, onNextClick: ()->Unit) {
    // invalid case
    if(index <0 || total<=0) {
        return
    }
    // invalid case
    if(index>total) {
        return
    }

    // hide if only one page
    if(total == 1) {
        return
    }
    Row(
        modifier = Modifier.background(MaterialTheme.colors.assistantBackground)
    ) {
        IconButton(
            modifier = Modifier.background(MaterialTheme.colors.assistantBackground),
            onClick = {
                onPreviousClick()
            },
            enabled = (index > 0),
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colors.iconTintColor.copy(alpha = LocalContentAlpha.current)
            )
        }
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            color = MaterialTheme.colors.textColor,
            text = "${index+1} / $total"
        )

        IconButton(
            modifier = Modifier.background(MaterialTheme.colors.assistantBackground),
            onClick = {
                onNextClick()
            },
            enabled = (index < total - 1)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colors.iconTintColor.copy(alpha = LocalContentAlpha.current)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    ChatGPTClientTheme(darkTheme = false) {
        PageControl(
            index = 0,
            total = 10,
            onPreviousClick = { },
            onNextClick = { }
        )
    }
}