package com.example.modifier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modifier.ui.theme.ModifierTheme

// Modifier.fillMaxSize()가 기본값이기 때문에, myModifier1은 Surface를 가득 채울 것이다.
val myModifier1 = Modifier
    .background(color = Color.Gray)
    .border(10.dp, color = Color.Magenta)
    .padding(10.dp)

/* Modifier.wrapContentSize()를 선제적으로 사용함으로써,
 * 기본값인 Modifier.fillMaxSize()를 Modifier.fillMaxSize()로 바꿔버렸다.
 * 이로 인해,
 * fillMaxSize()를 기준으로 background(), border(), padding()가 그려지는 걸 막았다.
 */
val myModifier2 = Modifier
    .wrapContentSize()
    .background(color = Color(0xFFFFC896))
    .border(10.dp, color = Color.White)
    .padding(10.dp)

/* myModifier2와 myModifier3는 코드 상으로는 같아보이지만 전혀 다른 결과를 도출한다.
 * 따라서, Modifier를 메소드 체이닝할 때는 체이닝의 순서에 아주 주의해야 한다.
 */
val myModifier3 = Modifier
    .background(color = Color(0xFFFFC896))
    .border(10.dp, color = Color.White)
    .padding(10.dp)
    .wrapContentSize() // 안 먹힘. 이미 fillMaxSize()을 사용하는 background(), border(), padding()이 메소드 체이닝된 후이기 때문.

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ModifierTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    Greeting("Android", myModifier1)
                }

                Surface(
                    modifier = Modifier.fillMaxSize(0.5f),
                    color = Color.Red
                ) {
                    Greeting(name = "Jetpack Compose", myModifier2)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifierParam: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        fontSize = 32.sp,
        color = Color.Blue,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifierParam
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ModifierTheme {
        Greeting("Android", myModifier1)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    ModifierTheme {
        Greeting("Jetpack Compose", myModifier2)
    }
}