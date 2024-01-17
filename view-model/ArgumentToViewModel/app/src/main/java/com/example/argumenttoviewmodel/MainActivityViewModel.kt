package com.example.argumenttoviewmodel

import androidx.lifecycle.ViewModel

class MainActivityViewModel(startingCount : Int) : ViewModel() {
    private var count = 0

    init {
        count = startingCount
    }

    fun getCurrentCount(): Int {
        return count
    }

    fun getUpdatedCount(): Int {
        return ++count
    }
}