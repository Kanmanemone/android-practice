package com.example.keyboardlikebottomsheet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.keyboardlikebottomsheet.ui.theme.KeyboardLikeBottomSheetTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = viewModel<MainViewModel>()
            val uiState = viewModel.uiState.collectAsState().value

            val scaffoldState = rememberBottomSheetScaffoldState(
                bottomSheetState = rememberStandardBottomSheetState(
                    skipHiddenState = true
                )
            )

            val focusManager = LocalFocusManager.current
            LaunchedEffect(uiState.phase) {
                if (uiState.phase == UiPhase.Initial || uiState.phase == UiPhase.InputOtherInfo) {
                    focusManager.clearFocus()
                }
            }

            KeyboardLikeBottomSheetTheme {
                BottomSheetScaffold(
                    sheetContent = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Green),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("this is sheet's content")
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (uiState.phase == UiPhase.InputOtherInfo) {
                                Modifier
                            } else {
                                Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                            }
                        )
                        .windowInsetsPadding(WindowInsets.ime),
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = if (uiState.phase == UiPhase.InputOtherInfo) {
                        300.dp
                    } else {
                        0.dp
                    },
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "UiPhase: ${uiState.phase}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Magenta
                            )
                        )
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(Color.Yellow),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("this is scaffold's content")
                        }

                        BottomBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(Color.LightGray)
                                .padding(8.dp),
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val uiState = viewModel.uiState.collectAsState().value

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = uiState.inputtedText,
            onValueChange = {
                viewModel.onTextValueChange(it)
            },
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        viewModel.requestPhase(UiPhase.InputText)
                    }
                }
        )
        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            FilledTonalIconButton(
                onClick = {
                    if (uiState.phase == UiPhase.InputOtherInfo) {
                        viewModel.requestPhase(UiPhase.Initial)
                    } else {
                        viewModel.requestPhase(UiPhase.InputOtherInfo)
                    }
                },
            ) {
                Icon(
                    imageVector = if (uiState.phase == UiPhase.InputOtherInfo) {
                        Icons.AutoMirrored.Filled.Send
                    } else {
                        Icons.AutoMirrored.Filled.ArrowForward
                    },
                    contentDescription = if (uiState.phase == UiPhase.InputOtherInfo) {
                        "전송"
                    } else {
                        "다음"
                    },
                )
            }
        }
    }
}