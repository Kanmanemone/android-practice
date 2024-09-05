package com.example.scaffold

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Snackbar를 위한 CoroutineScope와 State
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                // #3
                topBar = {
                    TopAppBar(
                        title = { Text("TopBar Text") }
                    )
                },
                // #4
                bottomBar = {
                    BottomAppBar(
                        content = { Text("BottomBar Text") }
                    )
                },
                // #5
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        snackbar = { snackbarData ->
                            Snackbar(
                                modifier = Modifier.padding(12.dp),
                                containerColor = Color.Magenta,
                            ) {
                                Text(
                                    text = snackbarData.visuals.message,
                                    color = Color.White
                                )
                            }
                        }
                    )
                },
                // #6
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    // snackbarData 보내기
                                    message = "FAB Clicked",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    ) {
                        Text(
                            "F A B"
                        )
                    }
                },
                floatingActionButtonPosition = FabPosition.End,
                // #7
                containerColor = MaterialTheme.colorScheme.background, // Scaffold의 배경 색상
                contentColor = contentColorFor(MaterialTheme.colorScheme.background), // 내부 content들의 기본 색
                contentWindowInsets = WindowInsets.systemBars // 상태바 위치를 고려한 Scaffold의 자리
            ) { innerPadding -> // Scaffold가 계산한, content가 Scaffold의 다른 UI 요소들과 겹치지 않을 정도의 Padding값
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .background(Color.LightGray)
                ) {
                    Text(
                        text = "Content Text",
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 30.sp
                    )
                }
            }
        }
    }
}