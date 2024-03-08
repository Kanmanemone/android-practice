package com.example.insertionresponse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.insertionresponse.db.User
import com.example.insertionresponse.db.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    val users = repository.users // 현재 저장된 User들
    val inputtedUser = MutableLiveData<User>() // View의 입력 박스와 바인딩된 객체

    private var isUpdateOrDelete = false
    val saveOrUpdateButtonText = MutableLiveData<String>()
    val clearAllOrDeleteButtonText = MutableLiveData<String>()

    private val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>>
        get() = _toastMessage

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
            insert2(inputtedUser.value!!)
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

    fun insert2(user: User) = viewModelScope.launch(Dispatchers.IO) {
        val insertedId = repository.insert2(user)
        withContext(Dispatchers.Main) { // LiveData의 값 변경은 설계 상 Main 스레드에서만 실행되어야 함
            if (-1 < insertedId) {
                _toastMessage.value = Event("Inserted Id is $insertedId")
            } else {
                _toastMessage.value = Event("Insertion failed")
            }
        }
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