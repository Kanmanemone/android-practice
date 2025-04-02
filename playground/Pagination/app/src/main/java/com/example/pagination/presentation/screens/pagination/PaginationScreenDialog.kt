package com.example.pagination.presentation.screens.pagination

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.pagination.presentation.main.LocalMainViewModel
import com.example.pagination.presentation.main.MainViewModelEvent
import com.example.pagination.utils.Strings

@Composable
fun InformationDialog() {
    val viewModel = LocalMainViewModel.current
    val screenState by viewModel.mainActivityState.collectAsState()
    val paginatedList = screenState.paginatedList!!

    AlertDialog(
        onDismissRequest = {
            viewModel.onEvent(MainViewModelEvent.DialogToggleEvent(false))
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.onEvent(MainViewModelEvent.DialogToggleEvent(false))
                }
            ) {
                Text(Strings.CLOSE_DIALOG)
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Info, contentDescription = "information"
            )
        },
        title = {
            Text(
                text = Strings.DIALOG_TITLE,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val informationList = listOf(
                    "${Strings.ITEM_COUNT_GUIDE}: ${paginatedList.totalItemCount}",
                    "${Strings.ITEM_COUNT_PER_PAGE_GUIDE}: ${paginatedList.itemCountPerPage}",
                    "${Strings.PAGE_COUNT_PER_PAGE_BLOCK_GUIDE}: ${paginatedList.pageCountPerPageBlock}",
                )

                for (information in informationList) {
                    Text(
                        text = information,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        },
    )
}