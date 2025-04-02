package com.example.pagination.presentation.main

sealed class MainActivityEvent {
    data class ShowSnackbar(val message:String): MainActivityEvent()
    data object NavigateToPaginationScreen: MainActivityEvent()
}