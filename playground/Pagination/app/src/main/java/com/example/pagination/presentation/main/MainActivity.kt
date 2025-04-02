package com.example.pagination.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pagination.presentation.screens.dataInput.DataInputScreen
import com.example.pagination.presentation.screens.pagination.PaginationScreen
import com.example.pagination.presentation.screens.pagination.PaginationScreenTopBar
import com.example.pagination.ui.theme.PaginationTheme
import com.example.pagination.utils.Dimensions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }
            val navController = rememberNavController()
            val currentBackStackEntry = navController.currentBackStackEntryAsState()
            val currentDestination = currentBackStackEntry.value?.destination?.route

            // ViewModel로부터 받을 이벤트
            LaunchedEffect(key1 = true) {
                viewModel.mainActivityEventFlow.collectLatest { event ->
                    when (event) {
                        is MainActivityEvent.ShowSnackbar -> {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = event.message,
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }

                        is MainActivityEvent.NavigateToPaginationScreen -> {
                            navController.navigate("paginationScreen")
                        }
                    }
                }
            }

            PaginationTheme {
                CompositionLocalProvider(
                    LocalMainViewModel provides viewModel,
                    LocalNavController provides navController
                ) {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.ime),
                        topBar = {
                            when (currentDestination) {
                                "paginationScreen" -> {
                                    PaginationScreenTopBar()
                                }
                                else -> {}
                            }
                        },
                        snackbarHost = {
                            SnackbarHost(
                                hostState = snackbarHostState,
                                snackbar = { snackbarData ->
                                    Snackbar(
                                        modifier = Modifier
                                            .padding(
                                                start = Dimensions.parentDistancePadding,
                                                top = 0.dp,
                                                end = Dimensions.parentDistancePadding,
                                                bottom = Dimensions.parentDistancePadding
                                            )
                                            .windowInsetsPadding(WindowInsets.ime)
                                    ) {
                                        Text(
                                            text = snackbarData.visuals.message
                                        )
                                    }
                                }
                            )
                        }
                    ) { innerPadding ->
                        NavHost(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(
                                    horizontal = Dimensions.parentDistancePadding
                                ),
                            navController = navController,
                            startDestination = "dataInputScreen"
                        ) {
                            composable(route = "dataInputScreen") {
                                DataInputScreen()
                            }

                            composable(route = "paginationScreen") {
                                PaginationScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}

