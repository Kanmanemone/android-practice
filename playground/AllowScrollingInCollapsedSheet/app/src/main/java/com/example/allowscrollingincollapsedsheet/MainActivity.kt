package com.example.allowscrollingincollapsedsheet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.allowscrollingincollapsedsheet.ui.theme.AllowScrollingInCollapsedSheetTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AllowScrollingInCollapsedSheetTheme {
                val scrollState = rememberScrollState()
                val coroutineScope = rememberCoroutineScope()

                BottomSheetScaffold(
                    sheetContent = {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray)
                        ) {
                            Column(
                                modifier = Modifier.verticalScroll(scrollState)
                            ) {
                                SampleTexts(
                                    modifier = Modifier.background(Color.Gray),
                                    fontSize = 32.sp
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxHeight(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.Top,
                            ) {
                                IconButton(onClick = {
                                    coroutineScope.launch {
                                        scrollState.scrollBy(100f)
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "아래로 스크롤",
                                        tint = Color.DarkGray,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                                IconButton(onClick = {
                                    coroutineScope.launch {
                                        scrollState.scrollBy(-100f)
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = "위로 스크롤",
                                        tint = Color.DarkGray,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
                        }
                    },
                    scaffoldState = rememberBottomSheetScaffoldState(),
                    sheetPeekHeight = 320.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.LightGray)
                    ) {
                        SampleTexts(
                            modifier = Modifier.background(Color.Gray),
                            fontSize = 32.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SampleTexts(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 48.sp,
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