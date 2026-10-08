package com.example.splitlabeltext.ui.optionlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.splitlabeltext.entry.api.MyNavKey
import com.example.splitlabeltext.ui.info.NavKeyInfoButton
import com.example.splitlabeltext.ui.info.NavKeyInfoDialog

@Composable
fun OptionListScreen(
    entries: List<MyNavKey>,
    onSelect: (MyNavKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    /** 정보 다이얼로그를 띄울 대상. `null`이면 닫힌 상태. */
    var infoKey by remember { mutableStateOf<MyNavKey?>(null) }

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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(
                        onClick = {
                            onSelect(entry)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                    ) {
                        Text(
                            text = entry.title,
                            textAlign = TextAlign.Center,
                        )
                    }
                    if (entry.description.isNotEmpty()) {
                        NavKeyInfoButton(onClick = { infoKey = entry })
                    }
                }
            }
        }
    }

    infoKey?.let { key ->
        NavKeyInfoDialog(key = key, onDismiss = { infoKey = null })
    }
}
