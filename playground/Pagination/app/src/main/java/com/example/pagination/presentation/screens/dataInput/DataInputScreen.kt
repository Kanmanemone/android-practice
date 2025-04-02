package com.example.pagination.presentation.screens.dataInput

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.pagination.presentation.main.LocalMainViewModel
import com.example.pagination.presentation.main.MainViewModelEvent
import com.example.pagination.utils.Strings

@Composable
fun DataInputScreen() {
    val viewModel = LocalMainViewModel.current
    val screenState by viewModel.mainActivityState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = Strings.DATA_INPUT_SCREEN_TITLE,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium
        )

        GuideTextAndTextField(
            guideText = Strings.ITEM_COUNT_GUIDE,
            textFieldText = screenState.itemCountString
        ) {
            viewModel.onEvent(MainViewModelEvent.UpdateState(
                screenState.copy(
                    itemCountString = it
                )
            ))
        }

        GuideTextAndTextField(
            guideText = Strings.ITEM_COUNT_PER_PAGE_GUIDE,
            textFieldText = screenState.itemCountPerPageString
        ) {
            viewModel.onEvent(MainViewModelEvent.UpdateState(
                screenState.copy(
                    itemCountPerPageString = it
                )
            ))
        }

        GuideTextAndTextField(
            guideText = Strings.PAGE_COUNT_PER_PAGE_BLOCK_GUIDE,
            textFieldText = screenState.pageCountPerPageBlockString
        ) {
            viewModel.onEvent(MainViewModelEvent.UpdateState(
                screenState.copy(
                    pageCountPerPageBlockString = it
                )
            ))
        }

        Button(
            onClick = {
                viewModel.onEvent(MainViewModelEvent.RequestPaginationScreen)
            }
        ) {
            Text(
                text = Strings.NAVIGATION_BUTTON,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}