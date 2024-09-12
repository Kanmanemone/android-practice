package com.example.objectorienteduilayer.event

sealed class MainScreenEvent {
    data class ShowSnackbar(val message: String) : MainScreenEvent()
}