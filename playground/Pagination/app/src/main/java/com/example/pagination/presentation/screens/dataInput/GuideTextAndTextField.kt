package com.example.pagination.presentation.screens.dataInput

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pagination.utils.Dimensions

@Composable
fun GuideTextAndTextField(
    guideText: String,
    textFieldText: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = guideText,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.size(Dimensions.elementGap))

        OutlinedTextField(
            value = textFieldText,
            onValueChange = { inputted ->
                onValueChange(inputted)
            },
            modifier = Modifier.weight(1f),
            textStyle = MaterialTheme.typography.titleLarge,
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )
    }
}