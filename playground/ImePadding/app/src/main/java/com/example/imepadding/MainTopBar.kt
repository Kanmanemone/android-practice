package com.example.imepadding

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    modifier: Modifier = Modifier,
    text: String = "this is main top bar"
) {
    TopAppBar(
        title = {
            Text(text)
        },
        modifier = modifier
    )
}