package com.example.pagination.presentation.screens.pagination

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.pagination.R
import com.example.pagination.presentation.main.LocalMainViewModel
import com.example.pagination.presentation.main.MainViewModelEvent
import com.example.pagination.utils.Dimensions
import com.example.pagination.utils.Strings

@Composable
fun PaginationScreen() {
    val viewModel = LocalMainViewModel.current
    val screenState by viewModel.mainActivityState.collectAsState()

    if (screenState.openDialog) {
        InformationDialog()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            PaginationInformation()
        }

        Spacer(Modifier.size(Dimensions.containerGap))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            PaginationBar()
        }

        Spacer(Modifier.size(Dimensions.containerGap))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            ItemsOnPage()
        }

        Spacer(Modifier.size(Dimensions.containerGap))
    }
}

// 첫번째 Card()에 담길 컴포넌트
@Composable
fun PaginationInformation() {
    val viewModel = LocalMainViewModel.current
    val screenState by viewModel.mainActivityState.collectAsState()
    val paginatedList = screenState.paginatedList!!

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimensions.parentDistancePadding)
    ) {
        GuideTextAndDisabledTextField(
            guideText = Strings.CURRENT_PAGE_NUMBER,
            value = paginatedList.currentPageNumber.toString(),
            guidedMaxValue = paginatedList.totalPageCount.toString()
        )

        Spacer(Modifier.size(Dimensions.elementGap))

        GuideTextAndDisabledTextField(
            guideText = Strings.CURRENT_PAGE_BLOCK_NUMBER,
            value = paginatedList.currentPageBlockNumber.toString(),
            guidedMaxValue = paginatedList.totalPageBlockCount.toString()
        )

        Spacer(Modifier.size(Dimensions.elementGap))

        GuideTextAndTextFieldAndSubmitButton(
            guideText = Strings.PAGE_NUMBER_TO_MOVE,
            value = screenState.inputtedPageNumber,
            onValueChange = {
                viewModel.onEvent(
                    MainViewModelEvent.UpdateState(
                        screenState.copy(
                            inputtedPageNumber = it
                        )
                    )
                )
            },
            guidedMaxValue = paginatedList.totalPageCount.toString(),
            onSubmitClick = {
                viewModel.onEvent(
                    MainViewModelEvent.PaginationGoToPage(screenState.inputtedPageNumber)
                )
            }
        )

        Spacer(Modifier.size(Dimensions.elementGap))

        GuideTextAndTextFieldAndSubmitButton(
            guideText = Strings.PAGE_BLOCK_NUMBER_TO_MOVE,
            value = screenState.inputtedPageBlockNumber,
            onValueChange = {
                viewModel.onEvent(
                    MainViewModelEvent.UpdateState(
                        screenState.copy(
                            inputtedPageBlockNumber = it
                        )
                    )
                )
            },
            guidedMaxValue = paginatedList.totalPageBlockCount.toString(),
            onSubmitClick = {
                viewModel.onEvent(
                    MainViewModelEvent.PaginationGoToPageBlock(screenState.inputtedPageBlockNumber)
                )
            }
        )
    }
}

// 두번째 Card()에 담길 컴포넌트
@Composable
fun PaginationBar() {
    val viewModel = LocalMainViewModel.current
    val screenState by viewModel.mainActivityState.collectAsState()
    val paginatedList = screenState.paginatedList!!
    val pages = paginatedList.getCurrentPageBlockPages()

    var gridContent: LazyGridScope.() -> Unit = { }
    var itemCount = 0

    val columnCount = 4
    val itemHeight = 46.dp // hard coding (실력이 늘다 보면 언젠간 소프트 코딩 방식으로 구현할 수 있을 것이다!)

    gridContent += if (paginatedList.firstPageBlockButtonVisibility()) {
        itemCount += 1
        {
            item {
                IconButton(
                    onClick = {
                        paginatedList.goToFirstPageBlock()
                    },
                    modifier = Modifier.height(itemHeight)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.keyboard_double_arrow_left_24dp_1f1f1f_fill0_wght400_grad0_opsz24),
                        contentDescription = "first page block",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    } else {
        { }
    }

    gridContent += if (paginatedList.previousPageBlockButtonVisibility()) {
        itemCount += 1
        {
            item {
                IconButton(
                    onClick = {
                        paginatedList.goToPreviousPageBlock()
                    },
                    modifier = Modifier.height(itemHeight)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.keyboard_arrow_left_24dp_1f1f1f_fill0_wght400_grad0_opsz24),
                        contentDescription = "previous page block",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    } else {
        { }
    }

    gridContent += if (pages.isNotEmpty()) {
        itemCount += pages.size
        {
            items(pages) { page ->
                TextButton(
                    onClick = {
                        viewModel.onEvent(MainViewModelEvent.PaginationGoToPage(page.toString()))
                    },
                    modifier = Modifier.height(itemHeight)
                ) {
                    Text(
                        text = page.toString(),
                        textDecoration = if (page == paginatedList.currentPageNumber) {
                            TextDecoration.Underline
                        } else {
                            null
                        },
                        fontWeight = if (page == paginatedList.currentPageNumber) {
                            FontWeight.Bold
                        } else {
                            null
                        },
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    } else {
        { }
    }

    gridContent += if (paginatedList.nextPageBlockButtonVisibility()) {
        itemCount += 1
        {
            item {
                IconButton(
                    onClick = {
                        paginatedList.goToNextPageBlock()
                    },
                    modifier = Modifier.height(itemHeight)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.keyboard_arrow_right_24dp_1f1f1f_fill0_wght400_grad0_opsz24),
                        contentDescription = "next page block",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    } else {
        { }
    }

    gridContent += if (paginatedList.lastPageBlockButtonVisibility()) {
        itemCount += 1
        {
            item {
                IconButton(
                    onClick = {
                        paginatedList.goToLastPageBlock()
                    },
                    modifier = Modifier.height(itemHeight)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.keyboard_double_arrow_right_24dp_1f1f1f_fill0_wght400_grad0_opsz24),
                        contentDescription = "last page block",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    } else {
        { }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columnCount),
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimensions.parentDistancePadding)
            .height(
                calculateGridHeight(
                    gridItemCount = itemCount,
                    columnCount = columnCount,
                    itemHeight = itemHeight,
                    rowGap = 0.dp,
                )
            )
    ) {
        gridContent()
    }
}

// 세번째 Card()에 담길 컴포넌트
@Composable
fun ItemsOnPage() {
    val viewModel = LocalMainViewModel.current
    val screenState by viewModel.mainActivityState.collectAsState()
    val paginatedList = screenState.paginatedList!!
    val itemsOnPage = paginatedList.getCurrentPageItemsWithNumbers()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimensions.parentDistancePadding)
    ) {
        Text(
            text = "${Strings.ITEM_COUNT_ON_CURRENT_PAGE}: ${itemsOnPage.size}",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.size(Dimensions.elementGap))

        HorizontalDivider(thickness = 2.dp, color = Color.Gray)

        Spacer(modifier = Modifier.size(Dimensions.elementGap))

        val lastIndex = itemsOnPage.last().first
        itemsOnPage.forEach { item ->
            Text(
                text = "[${item.first}${Strings.ITEM_NUMBER_FORMAT}] ${item.second.toString()}",
                style = MaterialTheme.typography.titleMedium
            )
            if (item.first != lastIndex) {
                Spacer(modifier = Modifier.size(Dimensions.elementGap))
            }
        }
    }
}

fun calculateGridHeight(
    gridItemCount: Int,
    columnCount: Int,
    itemHeight: Dp,
    rowGap: Dp,
): Dp {
    val maxRowCount = (gridItemCount + columnCount - 1) / columnCount
    var height = 0.dp
    height += itemHeight * maxRowCount
    height += rowGap * (maxRowCount - 1)

    return height
}

operator fun (LazyGridScope.() -> Unit).plus(
    other: LazyGridScope.() -> Unit
): LazyGridScope.() -> Unit = {
    this@plus()
    other()
}


