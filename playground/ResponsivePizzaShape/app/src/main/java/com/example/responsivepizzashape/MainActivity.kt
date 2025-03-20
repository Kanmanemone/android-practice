package com.example.responsivepizzashape

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.responsivepizzashape.ui.theme.ResponsivePizzaShapeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ResponsivePizzaShapeTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        ResponsivePizzaShape(
                            modifier = Modifier.padding(innerPadding),
                            totalSlices = 12,
                            color = Color.Yellow
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResponsivePizzaShape(
    modifier: Modifier = Modifier,
    totalSlices: Int = 8,
    color: Color = Color.Green
) {
    var remainingSlices by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable {
                remainingSlices = (remainingSlices + 1) % (totalSlices + 1)
            }
    ) {
        // State Hoisting
        val animatedRemainingSlices by animateFloatAsState(
            targetValue = remainingSlices.toFloat(),
            animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            animatedDrawPizza(
                animatedRemainingSlices = animatedRemainingSlices,
                totalSlices = totalSlices,
                color = color
            )
        }
    }
}

fun DrawScope.drawPizza(
    remainingSlices: Int,
    totalSlices: Int,
    color: Color
) {
    val radius = size.minDimension / 2 // minDimension은 size의 너비와 높이 중 더 작은 값을 반환
    val center = Offset(size.width / 2, size.height / 2) // 맨 ↖에서 →쪽으로 (너비/2)만큼 ↓쪽으로 (높이/2)만큼 움직인 곳
    val sliceAngle = 360f / totalSlices

    drawArc(
        color = color,
        startAngle = 180f, // 시침의 3시 방향부터 시작
        sweepAngle = sliceAngle * remainingSlices,
        useCenter = true,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2, radius * 2)
    )
}

fun DrawScope.animatedDrawPizza(
    animatedRemainingSlices: Float,
    totalSlices: Int,
    color: Color
) {
    val radius = size.minDimension / 2 // minDimension은 size의 너비와 높이 중 더 작은 값을 반환
    val center = Offset(size.width / 2, size.height / 2) // 맨 ↖에서 →쪽으로 (너비/2)만큼 ↓쪽으로 (높이/2)만큼 움직인 곳
    val sliceAngle = 360f / totalSlices

    drawArc(
        color = color,
        startAngle = 180f, // 시침의 3시 방향부터 시작
        sweepAngle = sliceAngle * animatedRemainingSlices,
        useCenter = true,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2, radius * 2)
    )
}