package com.example.stateinviewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val count = mutableStateOf(0)

    fun updateCount(newValue: Int) {
        count.value = newValue + 1
    }
}