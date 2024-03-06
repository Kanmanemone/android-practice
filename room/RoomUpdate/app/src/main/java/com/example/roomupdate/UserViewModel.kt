package com.example.roomupdate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomupdate.db.User
import com.example.roomupdate.db.UserRepository
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
        isUpdateOrDelete = false
        clearInputBox()
        renewButtonText()
    }

    fun initUpdateAndDelete(user: User) {
        isUpdateOrDelete = true
        inputtedUser.value = user
        renewButtonText()
    }

    // 버튼 클릭 시 동작
    fun saveOrUpdate() {
        if (isUpdateOrDelete) {
            // Update 작업
            update(inputtedUser.value!!)
            initSaveAndClearAll()
        } else {
            // Save 작업
            insert(inputtedUser.value!!)
            clearInputBox()
        }
    }

    fun clearAllOrDelete() {
        if (isUpdateOrDelete) {
            // Delete 작업
            delete(inputtedUser.value!!)
            initSaveAndClearAll()
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
        repository.update(user)
    }

    fun delete(user: User) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(user)
    }

    fun clearAll() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }

    // 기타 UI 다듬기용 메소드들
    private fun clearInputBox() {
        inputtedUser.value = User(0, "", "")
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