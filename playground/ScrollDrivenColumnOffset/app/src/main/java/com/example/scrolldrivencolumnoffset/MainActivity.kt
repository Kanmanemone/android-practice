package com.example.scrolldrivencolumnoffset

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.scrolldrivencolumnoffset.ui.theme.ScrollDrivenColumnOffsetTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScrollDrivenColumnOffsetTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Row(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        var offsetA by remember { mutableFloatStateOf(0f) }
                        var offsetB by remember { mutableFloatStateOf(0f) }

                        val scrollStateA = rememberScrollableState { delta ->
                            offsetB += delta
                            0F
                        }
                        val scrollStateB = rememberScrollableState { delta ->
                            offsetA += delta
                            0F
                        }

                        Column(
                            modifier = Modifier
                                .background(Color.Red)
                                .weight(1f)
                                .fillMaxHeight()
                                .scrollable(
                                    orientation = Orientation.Vertical,
                                    state = scrollStateA
                                )
                                .offset {
                                    IntOffset(
                                        x = 0,
                                        y = offsetA.roundToInt()
                                    )
                                },
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
                                .scrollable(
                                    orientation = Orientation.Vertical,
                                    state = scrollStateB
                                )
                                .offset {
                                    IntOffset(
                                        x = 0,
                                        y = offsetB.roundToInt()
                                    )
                                },
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
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}