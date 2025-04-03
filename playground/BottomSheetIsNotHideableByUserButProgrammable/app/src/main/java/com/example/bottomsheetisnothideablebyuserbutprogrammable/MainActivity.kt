package com.example.bottomsheetisnothideablebyuserbutprogrammable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bottomsheetisnothideablebyuserbutprogrammable.ui.theme.BottomSheetIsNotHideableByUserButProgrammableTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val scaffoldState = rememberBottomSheetScaffoldState(
                bottomSheetState = rememberStandardBottomSheetState(
                    skipHiddenState = true
                )
            )
            val scope = rememberCoroutineScope()
            var sheetPeekHeight by remember { mutableStateOf(300.dp) }

            BottomSheetIsNotHideableByUserButProgrammableTheme {
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
                    modifier = Modifier.fillMaxSize(),
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = sheetPeekHeight,
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "sheetPeekHeight",
                                    style = MaterialTheme.typography.titleSmall
                                )
                            },
                            actions = {
                                FilledTonalButton(
                                    onClick = {
                                        scope.launch {
                                            sheetPeekHeight = 0.dp
                                        }
                                    }
                                ) {
                                    Text("0.dp")
                                }

                                Spacer(modifier = Modifier.size(8.dp))

                                FilledTonalButton(
                                    onClick = {
                                        scope.launch {
                                            sheetPeekHeight = 300.dp
                                        }
                                    }
                                ) {
                                    Text("300.dp")
                                }

                                Spacer(modifier = Modifier.size(8.dp))
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("this is scaffold's content")
                    }
                }
            }
        }
    }
}