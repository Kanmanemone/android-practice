package com.example.testingroomandlivedata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.testingroomandlivedata.db.UserRepository

class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}