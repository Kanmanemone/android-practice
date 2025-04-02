package com.example.pagination.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pagination.utils.PaginatedListImpl
import com.example.pagination.utils.Strings
import com.example.pagination.utils.canCastToNonNegativeInt
import com.example.pagination.utils.canCastToPositiveInt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    // State
    private val _mainActivityState = MutableStateFlow(
        MainActivityState(
            itemCountString = "",
            itemCountPerPageString = "",
            pageCountPerPageBlockString = "",
            paginatedList = null,
            openDialog = false,
            inputtedPageNumber = "",
            inputtedPageBlockNumber = "",
        )
    )

    val mainActivityState: StateFlow<MainActivityState>
        get() = _mainActivityState

    // View로부터 받을 이벤트
    fun onEvent(event: MainViewModelEvent) {
        when (event) {
            is MainViewModelEvent.UpdateState -> {
                _mainActivityState.value = event.state
            }

            is MainViewModelEvent.RequestPaginationScreen -> {
                var warningMessage = ""

                if (!canCastToNonNegativeInt( _mainActivityState.value.itemCountString)) {
                    warningMessage += "\n${Strings.INVALID_ITEM_COUNT_WARNING}"
                }

                if (!canCastToPositiveInt( _mainActivityState.value.itemCountPerPageString)) {
                    warningMessage += "\n${Strings.INVALID_ITEM_COUNT_PER_PAGE_WARNING}"
                }

                if (!canCastToPositiveInt( _mainActivityState.value.pageCountPerPageBlockString)) {
                    warningMessage += "\n${Strings.INVALID_PAGE_COUNT_PER_PAGE_BLOCK_WARNING}"
                }

                if (warningMessage == "") {
                    val itemCount = _mainActivityState.value.itemCountString.toInt()
                    val items = List(itemCount) { String.format(Locale.KOREA, "%04d", Random.nextInt(1, 10000)) }
                    val itemCountPerPage = _mainActivityState.value.itemCountPerPageString.toInt()
                    val pageCountPerPageBlock = _mainActivityState.value.pageCountPerPageBlockString.toInt()

                    val paginatedList = PaginatedListImpl(
                        items = items,
                        itemCountPerPage = itemCountPerPage,
                        pageCountPerPageBlock = pageCountPerPageBlock
                    )

                    _mainActivityState.value = _mainActivityState.value.copy(
                        paginatedList = paginatedList,
                        inputtedPageNumber = paginatedList.currentPageNumber.toString(),
                        inputtedPageBlockNumber = paginatedList.currentPageBlockNumber.toString()
                    )

                    viewModelScope.launch {
                        _mainActivityEventFlow.emit(MainActivityEvent.NavigateToPaginationScreen)
                    }
                } else {
                    viewModelScope.launch {
                        _mainActivityEventFlow.emit(
                            MainActivityEvent.ShowSnackbar(
                                warningMessage.trim()
                            )
                        )
                    }
                }
            }

            is MainViewModelEvent.DialogToggleEvent -> {
                _mainActivityState.value = _mainActivityState.value.copy(
                    openDialog = event.openDialog
                )
            }

            is MainViewModelEvent.PaginationGoToPage -> {
                val pageNumberString = event.pageNumber

                if (!canCastToPositiveInt(pageNumberString)) {
                    viewModelScope.launch {
                        _mainActivityEventFlow.emit(
                            MainActivityEvent.ShowSnackbar(
                                Strings.INVALID_STRING_PAGE_NUMBER
                            )
                        )
                    }
                    return
                }

                val pageNumber = pageNumberString.toInt()
                val paginatedList = _mainActivityState.value.paginatedList!!

                if (!paginatedList.canGoToPage(pageNumber)) {
                    viewModelScope.launch {
                        _mainActivityEventFlow.emit(
                            MainActivityEvent.ShowSnackbar(
                                Strings.INVALID_RANGE_PAGE_NUMBER
                            )
                        )
                    }
                    return
                }

                paginatedList.goToPage(pageNumber)
                _mainActivityState.value = _mainActivityState.value.copy(
                    paginatedList = paginatedList
                )
            }

            is MainViewModelEvent.PaginationGoToPageBlock -> {
                val pageBlockNumberString = event.pageBlockNumber

                if (!canCastToPositiveInt(pageBlockNumberString)) {
                    viewModelScope.launch {
                        _mainActivityEventFlow.emit(
                            MainActivityEvent.ShowSnackbar(
                                Strings.INVALID_STRING_PAGE_BLOCK_NUMBER
                            )
                        )
                    }
                    return
                }

                val pageBlockNumber = pageBlockNumberString.toInt()
                val paginatedList = _mainActivityState.value.paginatedList!!

                if (!paginatedList.canGoToPageBlock(pageBlockNumber)) {
                    viewModelScope.launch {
                        _mainActivityEventFlow.emit(
                            MainActivityEvent.ShowSnackbar(
                                Strings.INVALID_RANGE_PAGE_BLOCK_NUMBER
                            )
                        )
                    }
                    return
                }

                paginatedList.goToPageBlock(pageBlockNumber)
                _mainActivityState.value = _mainActivityState.value.copy(
                    paginatedList = paginatedList
                )
            }
        }
    }

    // View에 보낼 이벤트
    private val _mainActivityEventFlow = MutableSharedFlow<MainActivityEvent>()
    val mainActivityEventFlow: SharedFlow<MainActivityEvent>
        get() = _mainActivityEventFlow.asSharedFlow()
}