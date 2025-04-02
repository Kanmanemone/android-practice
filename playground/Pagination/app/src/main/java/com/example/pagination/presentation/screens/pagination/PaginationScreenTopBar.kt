package com.example.pagination.presentation.screens.pagination

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import com.example.pagination.presentation.main.LocalMainViewModel
import com.example.pagination.presentation.main.LocalNavController
import com.example.pagination.presentation.main.MainViewModelEvent
import com.example.pagination.utils.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaginationScreenTopBar() {
    val viewModel = LocalMainViewModel.current
    val navController = LocalNavController.current

    TopAppBar(
        title = {
            Text(Strings.PAGINATION_SCREEN_TITLE)
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.navigateUp()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    viewModel.onEvent(MainViewModelEvent.DialogToggleEvent(true))
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Information"
                )
            }
        }
    )
}