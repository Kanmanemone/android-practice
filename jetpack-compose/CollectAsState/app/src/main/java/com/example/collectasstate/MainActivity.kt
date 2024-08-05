package com.example.collectasstate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = viewModel<MainViewModel>()

            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                // collectAsState()
                val count = viewModel.countFlow.collectAsState(initial = 1)

                TextExample(
                    count.value, // State Hoisting (https://kenel.tistory.com/186)
                    Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun TextExample(
    currentCount: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "Count: $currentCount",
        fontSize = 40.sp,
        modifier = modifier
    )
}