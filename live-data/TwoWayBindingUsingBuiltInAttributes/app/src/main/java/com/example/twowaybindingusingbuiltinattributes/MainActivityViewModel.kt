package com.example.twowaybindingusingbuiltinattributes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    private var count: MutableLiveData<String> = MutableLiveData("0회")

    fun getCurrentCount(): MutableLiveData<String> {
        return count
    }

    fun updateCount() {
        val valueInt = count.value?.replace("회", "")?.toIntOrNull() ?: 0
        count.value = (valueInt + 1).toString() + "회"
    }
}