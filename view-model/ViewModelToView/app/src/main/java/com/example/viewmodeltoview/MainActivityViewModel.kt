package com.example.viewmodeltoview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    private var count: MutableLiveData<Int> = MutableLiveData(0)

    fun getCurrentCount(): MutableLiveData<Int> {
        return count
    }

    fun updateCount() {
        count.value = (count.value)?.plus(1)
    }
}