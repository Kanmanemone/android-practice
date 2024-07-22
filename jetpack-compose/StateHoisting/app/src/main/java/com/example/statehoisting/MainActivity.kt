package com.example.statehoisting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                val count = remember {
                    mutableStateOf(0)
                }

                ButtonExample(
                    currentCount = count.value,
                    Modifier.align(Alignment.Center)
                ) { newValue ->
                    count.value = newValue + 1
                }
            }
        }
    }
}

// ↓ ↓ ↓ Stateless Composable
@Composable
fun ButtonExample(
    currentCount: Int,
    modifierParam: Modifier = Modifier,
    updateCount: (Int) -> Unit,
) {
    Button(
        onClick = { updateCount(currentCount) }, // updateCount()는 ButtonExample()이 아닌, ButtonExample()를 호출한 상위 Composable에서 처리된다.
        modifier = modifierParam

    ) {
        Text(
            text = "Count: $currentCount",
            fontSize = 40.sp
        )
    }
}

// ↓ ↓ ↓ Stateful Composable
@Composable
fun ButtonExample(modifierParam: Modifier = Modifier) {
    val count = remember {
        mutableStateOf(0)
    }

    Button(
        onClick = { count.value++ },
        modifier = modifierParam
    ) {
        Text(
            text = "Count: ${count.value}",
            fontSize = 40.sp
        )
    }
}