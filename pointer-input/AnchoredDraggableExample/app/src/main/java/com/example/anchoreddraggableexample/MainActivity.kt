package com.example.anchoreddraggableexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.anchoreddraggableexample.ui.theme.AnchoredDraggableExampleTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnchoredDraggableExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        AnchoredDraggableBox()
                    }
                }
            }
        }
    }
}

enum class DragValue { Start, Center, End }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnchoredDraggableBox() {
    val density = LocalDensity.current

    // 앵커 정의
    val anchors = with(density) {
        DraggableAnchors {
            DragValue.Start at -250.dp.toPx()
            DragValue.Center at 0f
            DragValue.End at 250.dp.toPx()
        }
    }

    // AnchoredDraggableState 선언
    val state = remember {
        AnchoredDraggableState(
            initialValue = DragValue.Center
        )
    }

    // AnchoredDraggableState에 앵커 연결 (초기화)
    LaunchedEffect(Unit) {
        state.updateAnchors(anchors)
    }

    if (!state.offset.isNaN()) { // 분기문으로 앵커 연결 (초기화) 유무 확인 후 Box() 호출
        Box(
            Modifier
                .offset {
                    IntOffset(
                        x = 0,
                        y = state.offset.roundToInt()
                    )
                }
                .width(200.dp)
                .height(200.dp)
                .background(Color.LightGray)
                .anchoredDraggable(
                    state = state,
                    orientation = Orientation.Vertical,
                    flingBehavior = AnchoredDraggableDefaults.flingBehavior(
                        state = state,
                        positionalThreshold = { distance -> distance * 0.5f },
                        animationSpec = spring()
                    )
                )
        ) {
            Text(
                """
                Swipe me!
                
                current: ${state.currentValue}
                settled: ${state.settledValue}
                """.trimIndent()
            )
        }
    }
}