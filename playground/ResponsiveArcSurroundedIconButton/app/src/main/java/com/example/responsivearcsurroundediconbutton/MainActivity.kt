package com.example.responsivearcsurroundediconbutton

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.sharp.Done
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.responsivearcsurroundediconbutton.ui.theme.ResponsiveArcSurroundedIconButtonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ResponsiveArcSurroundedIconButtonTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        var level1 by remember { mutableIntStateOf(0) }
                        val maxLevel1 = 8
                        var level2 by remember { mutableIntStateOf(0) }
                        val maxLevel2 = 30
                        var level3 by remember { mutableIntStateOf(0) }
                        val maxLevel3 = 3

                        ResponsiveArcSurroundedIconButton(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "전송",
                            currentLevel = level1,
                            maxLevel = maxLevel1,
                            arcColor = Color.Red,
                            arcWidth = 10
                        ) {
                            level1 = if(level1 < maxLevel1) {
                                level1 + 1
                            } else {
                                0
                            }
                        }

                        ResponsiveArcSurroundedIconButton(
                            imageVector = Icons.Default.Call,
                            contentDescription = "전화",
                            currentLevel = level2,
                            maxLevel = maxLevel2,
                            arcColor = Color.Green,
                            arcWidth = 20
                        ) {
                            level2 = if(level2 < maxLevel2) {
                                level2 + 1
                            } else {
                                0
                            }
                        }

                        ResponsiveArcSurroundedIconButton(
                            imageVector = Icons.Sharp.Done,
                            contentDescription = "완료",
                            currentLevel = level3,
                            maxLevel = maxLevel3,
                            arcColor = Color.Yellow,
                            arcWidth = 30
                        ) {
                            level3 = if(level3 < maxLevel3) {
                                level3 + 1
                            } else {
                                0
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResponsiveArcSurroundedIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    currentLevel: Int,
    maxLevel: Int,
    arcColor: Color,
    arcWidth: Int,
    onIconClick: () -> Unit
) {
    Box(
        modifier = Modifier.size((40 + arcWidth).dp),
        contentAlignment = Alignment.Center
    ) {
        // 레이어 1
        val animatedCurrentLevel by animateFloatAsState(
            targetValue = currentLevel.toFloat(),
            animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            animatedDrawArc(
                currentLevel = animatedCurrentLevel,
                maxLevel = maxLevel,
                color = arcColor
            )
        }

        // 레이어 2
        FilledTonalIconButton(
            onClick = {
                onIconClick()
            }
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription
            )
        }
    }
}

private fun DrawScope.animatedDrawArc(
    currentLevel: Float, maxLevel: Int, color: Color
) {
    val radius = (size.minDimension / 2) // minDimension은 size의 너비와 높이 중 더 작은 값을 반환
    val center = Offset(size.width / 2, size.height / 2) // 맨 ↖에서 →쪽으로 (너비/2)만큼 ↓쪽으로 (높이/2)만큼 움직인 곳
    val anglePerLevel = 360f / maxLevel

    drawArc(
        color = color,
        startAngle = 180f, // 시침의 3시 방향부터 시작
        sweepAngle = anglePerLevel * currentLevel,
        useCenter = true,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2, radius * 2)
    )
}