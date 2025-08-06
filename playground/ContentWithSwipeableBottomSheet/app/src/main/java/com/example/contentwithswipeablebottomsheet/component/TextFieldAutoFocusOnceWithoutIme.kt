package com.example.contentwithswipeablebottomsheet.component

import android.annotation.SuppressLint
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@Composable
fun TextFieldAutoFocusOnceWithoutIme(
    value: String = "",
    onValueChange: (String) -> Unit = {},
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    val imeController = LocalSoftwareKeyboardController.current
    var showKeyboardOnFocus by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        modifier = modifier.focusRequester(focusRequester),
        keyboardOptions = KeyboardOptions.Default.copy(
            showKeyboardOnFocus = showKeyboardOnFocus
        )
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        imeController?.hide() // showKeyboardOnFocus 조작만으론 완벽히 동작하지 않음. 이 문제가 해결될 때까지는 이 코드를 통해 보조함.
        showKeyboardOnFocus = true
    }
}

@Composable
fun TextFieldAutoFocusOnceWithoutIme(
    value: String = "",
    onValueChange: (String) -> Unit = {},
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource,
) {
    val focusRequester = remember { FocusRequester() }
    val imeController = LocalSoftwareKeyboardController.current
    var showKeyboardOnFocus by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        modifier = modifier.focusRequester(focusRequester),
        keyboardOptions = KeyboardOptions.Default.copy(
            showKeyboardOnFocus = showKeyboardOnFocus
        ),
        interactionSource = interactionSource
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        imeController?.hide() // showKeyboardOnFocus가 false여도 완벽히 동작하질 않음. 이 문제가 해결될 때까지는 이 코드를 통해 보조함.
        showKeyboardOnFocus = true
    }
}