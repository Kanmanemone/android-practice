package com.example.sheetoffsettracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.sheetoffsettracker.ui.theme.SheetOffsetTrackerTheme
import java.text.DecimalFormat

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SheetOffsetTrackerTheme {
                val scrollState = rememberScrollState()
                var dragHandleOffset by remember { mutableFloatStateOf(0f) }

                BottomSheetScaffold(
                    sheetContent = {
                        Column(
                            modifier = Modifier.verticalScroll(scrollState)
                        ) {
                            SampleTexts(
                                modifier = Modifier.background(Color.Gray)
                            )
                        }
                    },
                    modifier = Modifier.padding(WindowInsets.statusBars.asPaddingValues()),
                    scaffoldState = rememberBottomSheetScaffoldState(),
                    sheetPeekHeight = 320.dp,
                    sheetContainerColor = Color.Gray.copy(alpha = 0.5f),
                    sheetDragHandle = {
                        BottomSheetDefaults.DragHandle(
                            modifier = Modifier.onGloballyPositioned {
                                dragHandleOffset = it.positionInWindow().y
                            }
                        )
                    }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        val offsetDp = DecimalFormat("0.00").format(dragHandleOffset.toDp().value)

                        Text(
                            text = "Bottom Sheet's Offset:\n${offsetDp}.dp",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.displayLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SampleTexts(
    modifier: Modifier = Modifier
) {
    val textCount = 20
    repeat(textCount) {
        Text(
            text = "Item (${it + 1} / ${textCount})",
            modifier = modifier,
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun Float.toDp(): Dp {
    val density = LocalDensity.current.density
    return (this / density).dp
}