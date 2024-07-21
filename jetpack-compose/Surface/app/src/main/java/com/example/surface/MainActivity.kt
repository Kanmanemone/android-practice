package com.example.surface

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.surface.ui.theme.SurfaceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SurfaceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background, // ColorScheme은 '색상 모음'이다.
                    tonalElevation = 0.dp, // <- 이게 기본값이라 생략해도 됨
                    border = BorderStroke(8.dp, Color.Red)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(), // <- 이게 기본값이라 생략해도 됨
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(0.8f),
                            shape = RoundedCornerShape(36.dp),
                            tonalElevation = LocalAbsoluteTonalElevation.current + 16.dp,
                            shadowElevation = 12.dp,
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(), // <- 이게 기본값이라 생략해도 됨
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    modifier = Modifier.fillMaxSize(0.8f),
                                    tonalElevation = LocalAbsoluteTonalElevation.current + 16.dp,
                                    contentColor = Color.Blue
                                ) {
                                    Greeting("Android")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        fontSize = 24.sp,
        modifier = modifier,
    )
}

/*

@Composable
@ComposableInferredTarget
public fun Surface(
    modifier: Modifier, // Surface에 적용될 Modifier
    shape: Shape, // Surface의 모양 정의
    color: Color, // 배경색
    contentColor: Color, // Surface 속 Composable들에게 적용될 기본 색 일괄 지정.
    tonalElevation: Dp, // 색상 톤(tone)에 의한 시각적 계층화(Elevation)
    shadowElevation: Dp, // 그림자의 크기에 의한 시각적 계층화(Elevation)
    border: BorderStroke?, // 우리가 잘 아는 그 border
    content: @Composable () -> Unit
): Unit

*/