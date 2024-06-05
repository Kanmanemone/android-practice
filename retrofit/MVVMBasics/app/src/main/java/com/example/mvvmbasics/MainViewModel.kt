package com.example.mvvmbasics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmbasics.retrofit.Albums
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val repository: MyRepository) : ViewModel() {

    private var _userId: MutableLiveData<Int> = MutableLiveData()
    val userId: MutableLiveData<Int> get() = _userId

    private var _albums: MutableLiveData<Response<Albums>> = MutableLiveData()
    val albums: LiveData<Response<Albums>> get() = _albums

    init {
        _userId.value = 0
        _albums.value = Response.success(Albums()) // 빈 Albums 넣기
    }

    fun onSubmitClicked(newUserId: Int) {
        _userId.value = newUserId
        updateAlbums()
    }

    private fun updateAlbums() {
        viewModelScope.launch {
            val response = repository.getSortedAlbums(_userId.value!!)
            _albums.value = response
        }
    }
}