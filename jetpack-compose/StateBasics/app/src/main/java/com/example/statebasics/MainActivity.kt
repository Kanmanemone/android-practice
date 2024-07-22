package com.example.statebasics

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.statebasics.ui.theme.StateBasicsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StateBasicsTheme {
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

/* Compose Runtime (https://developer.android.com/jetpack/androidx/releases/compose-runtime)
 * Compose Runtime은 Jetpack Compose의 코어 시스템으로, 컴포저블 함수들이 실행되고 UI 상태가 업데이트되는 과정을 관리한다.
 * 컴포저블 함수가 호출될 때마다 런타임은 이를 기록하고, 관련된 상태가 변경되면 UI를 다시 렌더링한다.
 * 따라서, Observable 패턴을 구현하는 MutableState, LiveData, Flow, RxJava 등만이 Compose Runtime에게 '관찰(감지)'될 수 있다.
 */
// var count = 0 // 이와 같은 평범한 변수는 그 상태 변화를 Compose Runtime이 감지할 수 없고, 따라서 UI 암시적 렌더링의 trigger로 간주되지도 않는다.
val count = mutableStateOf(0)

@Composable
fun ButtonExample(modifierParam: Modifier = Modifier) {
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
