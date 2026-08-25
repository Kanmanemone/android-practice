package com.example.splitlabeltext.entry.optionexample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun OptionExampleScreen(
    modifier: Modifier = Modifier,
    initialLabel: String = "이름",
    initialText: String = "",
) {
    var label by remember { mutableStateOf(initialLabel) }
    var text by remember { mutableStateOf(initialText) }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = label,
            onValueChange = { label = it },
            label = { Text("Label") },
            modifier = Modifier.widthIn(max = 120.dp),
        )
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Text") },
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview(name = "text 비어있음", showBackground = true)
@Composable
private fun OptionExampleScreenEmptyTextPreview() {
    OptionExampleScreen(initialLabel = "이름", initialText = "")
}

@Preview(name = "text 채워짐", showBackground = true)
@Composable
private fun OptionExampleScreenFilledTextPreview() {
    OptionExampleScreen(initialLabel = "이름", initialText = "홍길동")
}

@Preview(name = "label 빈 값", showBackground = true)
@Composable
private fun OptionExampleScreenEmptyLabelPreview() {
    OptionExampleScreen(initialLabel = "", initialText = "홍길동")
}
