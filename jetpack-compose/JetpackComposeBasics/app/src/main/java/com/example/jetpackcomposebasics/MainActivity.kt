package com.example.jetpackcomposebasics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.jetpackcomposebasics.ui.theme.JetpackComposeBasicsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { // Composable UI의 시작점
            JetpackComposeBasicsTheme { // (생략 가능) 사용자 정의가 가능한 Theme. Surface 및 Composable에 대한 전역 설정이라고 보면 된다.
                /* (생략 가능) Surface
                 * Composable들의 Container.
                 * Surface( ... )의 ...는 Surface에 대한 설정이지,
                 * Surface 내부에 존재하는 Greeting에 대한 설정은 아님에 유의한다.
                 * Surface 내부에 존재하는 Greeting에 대한 전역적 설정은 Theme의 몫이다.
                 */
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Composable을 가지고 있는 함수 실행
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

// 매개변수가 없는 @Composable 함수에는 @Preview를 달 수 있다.
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetpackComposeBasicsTheme {
        Greeting("Android")
    }
}

// Default parameter가 있는 @Composable 함수에는 @Preview를 달 수 있다.
@Preview(showBackground = true)
@Composable
fun GreetingPreviewWithDefaultParameter(name: String = "interfacer_han") {
    JetpackComposeBasicsTheme {
        Greeting(name)
    }
}

// 매개변수에 @PreviewParameter가 붙은 @Composable 함수에는 @Preview를 달 수 있다.
@Preview(showBackground = true)
@Composable
fun GreetingPreviewWithProvider(@PreviewParameter(MyParameterProvider::class) name: String) {
    JetpackComposeBasicsTheme {
        Greeting(name)
    }
}

class MyParameterProvider : PreviewParameterProvider<String> {
    override val values = sequenceOf("steve", "kevin")
}
