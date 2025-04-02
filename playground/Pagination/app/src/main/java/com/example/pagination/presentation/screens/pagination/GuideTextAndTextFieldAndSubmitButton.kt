package com.example.pagination.presentation.screens.pagination

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.pagination.utils.Dimensions

@Composable
fun GuideTextAndTextFieldAndSubmitButton(
    guideText :String,
    value: String,
    onValueChange: (String) -> Unit,
    guidedMaxValue: String,
    onSubmitClick: () -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = guideText,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.size(Dimensions.elementGap))

        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            modifier = Modifier.weight(1f),
            textStyle = MaterialTheme.typography.titleMedium,
            singleLine = true,
        )

        Spacer(Modifier.size(Dimensions.elementGap))

        Text(
            text = "/ $guidedMaxValue",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.size(Dimensions.elementGap))

        FilledTonalIconButton(
            onClick = {
                onSubmitClick()
            },
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Back"
            )
        }
    }
}