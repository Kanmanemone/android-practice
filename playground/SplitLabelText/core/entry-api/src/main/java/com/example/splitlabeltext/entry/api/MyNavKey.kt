package com.example.splitlabeltext.entry.api

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey

interface MyNavKey : NavKey {
    val title: String
    val screen: @Composable () -> Unit
}
