package com.example.collectasstate

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class MainViewModel : ViewModel() {
    val countFlow = flow {
        for (i in 1..5) {
            delay(1000)
            emit(i)
        }
    }
}