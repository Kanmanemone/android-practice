package com.example.livedatabuilder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.livedatabuilder.model.UserRepository
import kotlinx.coroutines.Dispatchers

class MainActivityViewModel : ViewModel() {

    private var usersRepository = UserRepository()
    var users = liveData(Dispatchers.IO) {
        val result = usersRepository.getUsers()
        emit(result)
    }

/*
    private var usersRepository = UserRepository()
    var users: MutableLiveData<List<User>?> = MutableLiveData()

    fun getUsers() {
        viewModelScope.launch {
            var result: List<User>? = null
            withContext(Dispatchers.IO) {
                result = usersRepository.getUsers()
            }
            users.value = result
        }
    }
*/
}
