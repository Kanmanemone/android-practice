package com.example.pagination.utils

/*
(example) goToFirstPageBlockButton: <<
(example) goToPreviousPageBlockButton: <
(example) goToNextPageBlockButton: >
(example) goToLastPageBlockButton: >>

(example) pageBlock 01: << < 01 02 03 04 05 06 07 08 09 10 > >>
(example) pageBlock 02: << < 11 12 13 14 15 16 17 18 19 20 > >>
(example) pageBlock 03: << < 21 22 23 24 25 26 27 28 29 30 > >>
...
*/
interface PaginatedList<T> : List<T> { // T는 List가 보유하는 item의 데이터형

    // public property - 인수로 받을 것들 (따라서 자연히 immutable)
    val items: List<T>
    val itemCountPerPage: Int
    val pageCountPerPageBlock: Int

    // public property - immutable
    val totalItemCount: Int
    val totalPageCount: Int
    val totalPageBlockCount: Int

    // public property - private한 setter를 _currentPageNumber 및 _currentPageBlockNumber에 구현
    val currentPageNumber: Int // 1-based indexing
    val currentPageBlockNumber: Int // 1-based indexing

    // getter
    fun getCurrentPageItems(): List<T>
    fun getCurrentPageItemNumbers(): List<Int> // 1-based indexing
    fun getCurrentPageItemsWithNumbers(): List<Pair<Int, T>> // 1-based indexing
    fun getCurrentPageBlockPages(): List<Int>

    // page move method
    fun goToPage(pageNumber: Int): Boolean // 1-based indexing
    fun goToPreviousPage(): Boolean
    fun goToNextPage(): Boolean
    fun goToFirstPage(): Boolean
    fun goToLastPage(): Boolean

    // pageBlock move method
    fun goToPageBlock(pageBlockNumber: Int): Boolean // 1-based indexing
    fun goToPreviousPageBlock(): Boolean
    fun goToNextPageBlock(): Boolean
    fun goToFirstPageBlock(): Boolean
    fun goToLastPageBlock(): Boolean

    // method for cheking before page/pageBlock moving
    fun canGoToPage(pageNumber: Int): Boolean
    fun canGoToPageBlock(pageBlockNumber: Int): Boolean

    // pageBlock move button visibility method
    fun previousPageBlockButtonVisibility(): Boolean
    fun nextPageBlockButtonVisibility(): Boolean
    fun firstPageBlockButtonVisibility(): Boolean
    fun lastPageBlockButtonVisibility(): Boolean

    // parent interface implementation - List
    override val size: Int
        get() = items.size

    override fun contains(element: T): Boolean {
        return items.contains(element)
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return items.containsAll(elements)
    }

    override fun get(index: Int): T {
        return items[index]
    }

    override fun indexOf(element: T): Int {
        return items.indexOf(element)
    }

    override fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    override fun iterator(): Iterator<T> {
        return items.iterator()
    }

    override fun lastIndexOf(element: T): Int {
        return items.lastIndexOf(element)
    }

    override fun listIterator(): ListIterator<T> {
        return items.listIterator()
    }

    override fun listIterator(index: Int): ListIterator<T> {
        return items.listIterator(index)
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        return items.subList(fromIndex, toIndex)
    }
}