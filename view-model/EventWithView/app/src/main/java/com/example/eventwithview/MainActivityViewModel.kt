package com.example.eventwithview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    private var count: MutableLiveData<Int> = MutableLiveData(0)

    private val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>>
        get() = _toastMessage

    fun getCurrentCount(): MutableLiveData<Int> {
        return count
    }

    fun updateCount() {
        count.value = (count.value)?.plus(1)

        if (10 <= count.value!!) {
            _toastMessage.value = Event("${count.value}")
        }
    }
}