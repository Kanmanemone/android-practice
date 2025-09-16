package com.example.ui.internal

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

@Composable
internal fun AssertSoftInputModeIsAdjustNothing() {
    val context = LocalView.current.context
    val activity = context as Activity
    val window = activity.window
    val softInputAdjustType by remember { mutableIntStateOf(window.attributes.softInputMode and WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST) }
    LaunchedEffect(window) {
        check(softInputAdjustType == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING) {
            """
                Expected SOFT_INPUT_ADJUST_NOTHING but was $softInputAdjustType
                해결법: Activity의 setContent {} 호출 전에, window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)와 같은 코드를 사용하세요.
            """.trimIndent()
        }
    }
}