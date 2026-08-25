package com.example.splitlabeltext.ui.optionlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.splitlabeltext.entry.api.MyNavKey

@Composable
fun OptionListScreen(
    entries: List<MyNavKey>,
    onSelect: (MyNavKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (entries.isEmpty()) {
            Text("아직 구현된 옵션이 없습니다.")
        } else {
            entries.forEach { entry ->
                TextButton(
                    onClick = {
                        onSelect(entry)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                ) {
                    Text(entry.title)
                }
            }
        }
    }
}
