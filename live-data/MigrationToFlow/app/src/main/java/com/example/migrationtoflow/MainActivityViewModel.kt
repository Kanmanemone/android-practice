package com.example.migrationtoflow

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainActivityViewModel : ViewModel() {
    private var count: MutableStateFlow<Int> = MutableStateFlow(0)

    fun getCurrentCount(): StateFlow<Int> {
        return count
    }

    fun updateCount() {
        count.value++
    }
}