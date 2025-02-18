package com.example.scrollmodifiercallorder

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scrollmodifiercallorder.ui.theme.ScrollModifierCallOrderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScrollModifierCallOrderTheme {
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
                                .nestedScrollWithLogger("1")
                                .scrollableWithLogger("2")
                                .nestedScrollWithLogger("3")
                                .scrollableWithLogger("4")
                                .verticalScrollWithLogger("4.5")
                                .nestedScrollWithLogger("5")
                                .scrollableWithLogger("6")
                                .nestedScrollWithLogger("7")
                                .scrollableWithLogger("8")
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
                                        .nestedScrollWithLogger("9")
                                        .scrollableWithLogger("10")
                                        .nestedScrollWithLogger("11")
                                        .scrollableWithLogger("12")
                                        .verticalScrollWithLogger("12.5")
                                        .nestedScrollWithLogger("13")
                                        .scrollableWithLogger("14")
                                        .nestedScrollWithLogger("15")
                                        .scrollableWithLogger("16")
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
                                                .nestedScrollWithLogger("17")
                                                .scrollableWithLogger("18")
                                                .nestedScrollWithLogger("19")
                                                .scrollableWithLogger("20")
                                                .verticalScrollWithLogger("20.5")
                                                .nestedScrollWithLogger("21")
                                                .scrollableWithLogger("22")
                                                .nestedScrollWithLogger("23")
                                                .scrollableWithLogger("24")
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

@Composable
@SuppressLint("ModifierFactoryUnreferencedReceiver")
private fun Modifier.scrollableWithLogger(id: String): Modifier {
    return this.scrollable(
        state = ScrollableState {
            println("$id scrollable() available delta: $it")
            0F
        },
        orientation = Orientation.Vertical
    )
}

/* println()이 좀 늦게 출력된다
 * 실제로 verticalScroll()이 늦게 수행된 것은 아니다
 * ScrollState.value의 변화를 LaunchedEffect로 추적하고
 * LaunchedEffect 내에서 비동기적으로 로그 메시지를 출력하기에 시차가 있는 것이다
 * 로그 메시지 자체가, 실제 스크롤이 되는 순간 출력되게 만들어지지 않았다는 얘기다
 */
@Composable
@SuppressLint("ModifierFactoryUnreferencedReceiver")
private fun Modifier.verticalScrollWithLogger(id: String): Modifier {
    val scrollState = remember {
        ScrollState(0)
    }

    LaunchedEffect(scrollState.value) {
        println("$id verticalScroll()")
    }

    return this.verticalScroll(
        state = scrollState
    )
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