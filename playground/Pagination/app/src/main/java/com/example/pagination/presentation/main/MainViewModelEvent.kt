package com.example.pagination.presentation.main

sealed class MainViewModelEvent {
    data class UpdateState(val state: MainActivityState) : MainViewModelEvent()
    data object RequestPaginationScreen : MainViewModelEvent()
    data class DialogToggleEvent(val openDialog: Boolean) : MainViewModelEvent()
    data class PaginationGoToPage(val pageNumber: String) : MainViewModelEvent()
    data class PaginationGoToPageBlock(val pageBlockNumber: String) : MainViewModelEvent()
}