package com.example.scrolltransferredtosidecolumn

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.scrolltransferredtosidecolumn.ui.theme.ScrollTransferredToSideColumnTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @SuppressLint("ReturnFromAwaitPointerEventScope")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScrollTransferredToSideColumnTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Row(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        val coroutineScope = rememberCoroutineScope()

                        val scrollStateA = rememberScrollState()
                        val scrollStateB = rememberScrollState()

                        Column(
                            modifier = Modifier
                                .background(Color.Red)
                                .weight(1f)
                                .fillMaxHeight()
                                .pointerInput(Unit) {
                                    detectVerticalDragGestures { _, dragAmount ->
                                        coroutineScope.launch {
                                            scrollStateB.scrollBy(-dragAmount)
                                        }
                                    }
                                }
                                .verticalScroll(
                                    state = scrollStateA,
                                    enabled = false
                                ),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            SampleTexts()
                        }

                        Column(
                            modifier = Modifier
                                .background(Color.Blue)
                                .weight(1f)
                                .fillMaxHeight()
                                .pointerInput(Unit) {
                                    detectVerticalDragGestures { _, dragAmount ->
                                        coroutineScope.launch {
                                            scrollStateA.scrollBy(-dragAmount)
                                        }
                                    }
                                }
                                .verticalScroll(
                                    state = scrollStateB,
                                    enabled = false
                                ),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            SampleTexts(fontColor = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SampleTexts(
    modifier: Modifier = Modifier,
    fontColor: Color = Color.Unspecified
) {
    val textCount = 20
    repeat(textCount) {
        Text(
            text = "Item (${it + 1} / ${textCount})",
            modifier = modifier,
            color = fontColor,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}