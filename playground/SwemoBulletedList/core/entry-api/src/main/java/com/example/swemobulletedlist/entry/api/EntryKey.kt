package com.example.swemobulletedlist.entry.api

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey

interface EntryKey : NavKey {
    val title: String
    val screen: @Composable () -> Unit
    val description: String get() = ""
}
