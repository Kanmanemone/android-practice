package com.example.automigrationbasics

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.automigrationbasics.db.User
import com.example.automigrationbasics.db.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    val users = repository.users // 현재 저장된 User들
    val inputtedUser = MutableLiveData<User>() // View의 입력 박스와 바인딩된 객체

    private var isUpdateOrDelete = false
    val saveOrUpdateButtonText = MutableLiveData<String>()
    val clearAllOrDeleteButtonText = MutableLiveData<String>()

    init {
        initSaveAndClearAll()
    }

    // 초기화 관련
    fun initSaveAndClearAll() {
        clearInputBox()
        renewButtonText()
    }

    fun initUpdateAndDelete(user: User) {
        TODO()
    }

    // 버튼 클릭 시 동작
    fun saveOrUpdate() {
        if (isUpdateOrDelete) {
            // Update 작업
            TODO()
        } else {
            // Save 작업
            insert(inputtedUser.value!!)
            clearInputBox()
        }
    }

    fun clearAllOrDelete() {
        if (isUpdateOrDelete) {
            // Delete 작업
            TODO()
        } else {
            // Clear All 작업
            clearAll()
        }
    }

    // Repository 동작과 관련된 메소드들
    fun insert(user: User) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(user)
    }

    fun update(user: User) = viewModelScope.launch(Dispatchers.IO) {
        TODO()
    }

    fun delete(user: User) = viewModelScope.launch(Dispatchers.IO) {
        TODO()
    }

    fun clearAll() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }

    // 기타 UI 다듬기용 메소드들
    private fun clearInputBox() {
        inputtedUser.value = User(0, "", "", -1)
    }

    private fun renewButtonText() {
        if (isUpdateOrDelete) {
            saveOrUpdateButtonText.value = "Update"
            clearAllOrDeleteButtonText.value = "Delete"
        } else {
            saveOrUpdateButtonText.value = "Save"
            clearAllOrDeleteButtonText.value = "Clear All"
        }
    }
}