package com.example.pagination.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import kotlin.math.min

data class PaginatedListImpl<T>(
    // public property - 인수로 받을 것들 (따라서 자연히 immutable)
    override val items: List<T>,
    override val itemCountPerPage: Int,
    override val pageCountPerPageBlock: Int
) : PaginatedList<T> {

    // public property - immutable
    override val totalItemCount: Int = items.size

    override val totalPageCount: Int =
        if (totalItemCount % itemCountPerPage == 0) totalItemCount / itemCountPerPage
        else totalItemCount / itemCountPerPage + 1

    override val totalPageBlockCount: Int =
        if (totalPageCount % pageCountPerPageBlock == 0) totalPageCount / pageCountPerPageBlock
        else totalPageCount / pageCountPerPageBlock + 1

    // public property - private한 setter를 _currentPageNumber 및 _currentPageBlockNumber에 구현
    private var _currentPageNumber by mutableIntStateOf(1)
    override val currentPageNumber: Int
        get() = _currentPageNumber

    private var _currentPageBlockNumber by mutableIntStateOf(1)
    override val currentPageBlockNumber: Int
        get() = _currentPageBlockNumber

    // getter
    override fun getCurrentPageItems(): List<T> {
        val startIndex = (currentPageNumber - 1) * itemCountPerPage
        val endIndex = min(startIndex + itemCountPerPage, totalItemCount)
        return if (startIndex < endIndex) items.subList(startIndex, endIndex) else emptyList()
    }

    override fun getCurrentPageItemNumbers(): List<Int> {
        val startIndex = (currentPageNumber - 1) * itemCountPerPage
        val endIndex = min(startIndex + itemCountPerPage, totalItemCount)
        return if (startIndex < endIndex) (startIndex + 1 until endIndex + 1).toList() else emptyList()
    }

    override fun getCurrentPageItemsWithNumbers(): List<Pair<Int, T>> {
        val startIndex = (currentPageNumber - 1) * itemCountPerPage
        val endIndex = min(startIndex + itemCountPerPage, totalItemCount)
        return if (startIndex < endIndex) {
            items.subList(startIndex, endIndex).mapIndexed { index, item ->
                (startIndex + index + 1) to item
            }
        } else {
            emptyList()
        }
    }

    override fun getCurrentPageBlockPages(): List<Int> {
        val firstPageInBlock = (currentPageBlockNumber - 1) * pageCountPerPageBlock + 1
        val lastPageInBlock = min(currentPageBlockNumber * pageCountPerPageBlock, totalPageCount)
        return (firstPageInBlock..lastPageInBlock).toList()
    }

    // page move method
    override fun goToPage(pageNumber: Int): Boolean {
        if (canGoToPage(pageNumber)) {
            _currentPageNumber = pageNumber
            _currentPageBlockNumber = ((pageNumber - 1) / pageCountPerPageBlock) + 1
            return true
        }
        return false
    }

    override fun goToPreviousPage(): Boolean = goToPage(currentPageNumber - 1)

    override fun goToNextPage(): Boolean = goToPage(currentPageNumber + 1)

    override fun goToFirstPage(): Boolean = goToPage(1)

    override fun goToLastPage(): Boolean = goToPage(totalPageCount)

    // pageBlock move method
    override fun goToPageBlock(pageBlockNumber: Int): Boolean {
        return if (canGoToPageBlock(pageBlockNumber)) {
            _currentPageBlockNumber = pageBlockNumber
            val firstPageInBlock = (pageBlockNumber - 1) * pageCountPerPageBlock + 1
            _currentPageNumber = firstPageInBlock
            true
        } else {
            false
        }
    }

    override fun goToPreviousPageBlock(): Boolean = goToPageBlock(currentPageBlockNumber - 1)

    override fun goToNextPageBlock(): Boolean = goToPageBlock(currentPageBlockNumber + 1)

    override fun goToFirstPageBlock(): Boolean = goToPageBlock(1)

    override fun goToLastPageBlock(): Boolean = goToPageBlock(totalPageBlockCount)

    // method for cheking before page/pageBlock moving
    override fun canGoToPage(pageNumber: Int): Boolean {
        return pageNumber in 1..totalPageCount
    }

    override fun canGoToPageBlock(pageBlockNumber: Int): Boolean {
        return pageBlockNumber in 1..totalPageBlockCount
    }

    // pageBlock move button visibility method
    override fun previousPageBlockButtonVisibility(): Boolean = currentPageBlockNumber > 1

    override fun nextPageBlockButtonVisibility(): Boolean = currentPageBlockNumber < totalPageBlockCount

    override fun firstPageBlockButtonVisibility(): Boolean = previousPageBlockButtonVisibility()

    override fun lastPageBlockButtonVisibility(): Boolean = nextPageBlockButtonVisibility()
}