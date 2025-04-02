package com.example.pagination.presentation.main

import com.example.pagination.utils.PaginatedListImpl

data class MainActivityState(
    val itemCountString: String,
    val itemCountPerPageString: String,
    val pageCountPerPageBlockString: String,
    val paginatedList: PaginatedListImpl<*>?,
    val openDialog: Boolean,
    val inputtedPageNumber: String,
    val inputtedPageBlockNumber: String,
)