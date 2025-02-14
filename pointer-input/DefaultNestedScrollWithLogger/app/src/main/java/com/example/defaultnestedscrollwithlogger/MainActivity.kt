package com.example.defaultnestedscrollwithlogger

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.defaultnestedscrollwithlogger.ui.theme.DefaultNestedScrollWithLoggerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DefaultNestedScrollWithLoggerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    val p2Count = 1
                    val p1Count = 5
                    val cCount = 10

                    repeat(p2Count) { p2Index ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .background(Color.White)
                                .scrollModifierP2(
                                    nestedScrollConnection = remember {
                                        NestedScrollConnectionWithLogger("$p2Index")
                                    },
                                    scrollState = rememberScrollState()
                                )
                        ) {
                            repeat(p1Count) { p1Index ->
                                Column(
                                    modifier = Modifier
                                        .padding(
                                            start = 24.dp,
                                            top = 24.dp,
                                            bottom = 24.dp,
                                            end = 96.dp
                                        )
                                        .fillMaxWidth()
                                        .height(500.dp)
                                        .background(Color.LightGray)
                                        .scrollModifierP1(
                                            nestedScrollConnection = remember {
                                                NestedScrollConnectionWithLogger("$p2Index $p1Index")
                                            },
                                            scrollState = rememberScrollState()
                                        )
                                ) {
                                    repeat(cCount) { cIndex ->
                                        Column(
                                            modifier = Modifier
                                                .padding(
                                                    start = 24.dp,
                                                    top = 24.dp,
                                                    bottom = 24.dp,
                                                    end = 96.dp
                                                )
                                                .height(300.dp)
                                                .background(Color.Gray)
                                                .scrollModifierC(
                                                    nestedScrollConnection = remember {
                                                        NestedScrollConnectionWithLogger("$p2Index $p1Index $cIndex")
                                                    },
                                                    scrollState = rememberScrollState()
                                                )
                                        ) {
                                            SampleTexts()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
private fun Modifier.scrollModifierP2(
    nestedScrollConnection: NestedScrollConnection, scrollState: ScrollState
): Modifier {
    return this
        .nestedScroll(nestedScrollConnection)
        .verticalScroll(scrollState)
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
private fun Modifier.scrollModifierP1(
    nestedScrollConnection: NestedScrollConnection, scrollState: ScrollState
): Modifier {
    return this
        .nestedScroll(nestedScrollConnection)
        .verticalScroll(scrollState)
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
private fun Modifier.scrollModifierC(
    nestedScrollConnection: NestedScrollConnection, scrollState: ScrollState
): Modifier {
    return this
        .nestedScroll(nestedScrollConnection)
        .verticalScroll(scrollState)
}

@Composable
fun SampleTexts(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
) {
    val textCount = 20
    repeat(textCount) {
        Text(
            text = "Item (${it + 1} / ${textCount})",
            modifier = modifier,
            fontSize = fontSize,
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}