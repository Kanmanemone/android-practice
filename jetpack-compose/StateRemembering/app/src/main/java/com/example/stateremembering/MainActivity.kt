package com.example.stateremembering

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.stateremembering.ui.theme.StateRememberingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StateRememberingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        ButtonExample()
                    }
                }
            }
        }
    }
}

@Composable
fun ButtonExample(modifierParam: Modifier = Modifier) {
    /* 이 코드는 Creating a state object during composition without using remember 라는 에러 메시지를 유발한다.
     * 따라서, State 객체를 remember라는 이름의 '방어막'에 넣음으로서 re-composition되지 않게 해야 한다.
     * ↓ ↓ ↓
     * val count = mutableStateOf(0)
     * ↑ ↑ ↑
     */

    val count = remember {
        mutableStateOf(0)
    }

    /* onCreate()가 재실행되어도 State를 기억하게 만들려면,
     * ↓ ↓ ↓
     * val count = rememberSaveable {
     *     mutableStateOf(0)
     * }
     * ↑ ↑ ↑
     */

    Button(
        onClick = {
            Log.i("interfacer_han", "Current count value: ${++count.value}")
        },
        modifier = modifierParam
    ) {
        Text(
            text = "Count: ${count.value}",
            fontSize = 40.sp
        )
    }
}
