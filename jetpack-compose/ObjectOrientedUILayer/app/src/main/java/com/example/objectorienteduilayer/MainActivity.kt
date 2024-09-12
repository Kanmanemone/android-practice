package com.example.objectorienteduilayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.objectorienteduilayer.event.MainScreenEvent
import com.example.objectorienteduilayer.event.MainViewModelEvent
import com.example.objectorienteduilayer.ui.theme.ObjectOrientedUILayerTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ObjectOrientedUILayerTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel<MainViewModel>()
) {

    // (1) 스낵바 표시를 위한 코루틴 영역 그리고 스낵바의 상태 선언
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // (2) MainScreen에서 발생할 수 있는 이벤트 구현. ViewModel에서 트리거(발생)시킬 것이므로, ViewModel의 Flow를 collect()하는 방식으로 이벤트를 작성
    LaunchedEffect(key1 = true) { // 이 조건식(key1 = true)의 의미: MainScreen의 생애 주기에서 딱 한번 실행되고, MainScreen이 죽을 때까지 collect가 유지됨
        viewModel.mainScreenEventFlow.collectLatest { event ->
            when (event) {
                is MainScreenEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(
                // 앞에서 선언했던 '스낵바의 상태'를 주입
                hostState = snackbarHostState,
                // 스낵바의 모양 (외형 정의)
                snackbar = { snackbarData ->
                    Snackbar(
                        modifier = Modifier.padding(12.dp),
                        dismissAction = { // 닫기 버튼도 달아준다
                            TextButton(
                                onClick = { snackbarData.dismiss() }
                            ) {
                                Text(text = "닫기")
                            }
                        }
                    ) {
                        Text(text = snackbarData.visuals.message)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .padding(bottom = 56.dp), // bottom에 표시될 스낵바의 높이만큼의 공간을 추가로 확보
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val screenState = viewModel.mainScreenState.value

            Text(text = "신발 사이즈 변환기", fontSize = 40.sp)

            // (3) State Hoisting 패턴으로 하위 컴포저블 구성
            SizeInputField(CountryInfo.KR, screenState.inputtedKrSize, screenState.isInputEnabled) { inputtedSize ->
                viewModel.onEvent(MainViewModelEvent.SizeInputted(CountryInfo.KR, inputtedSize))
            }

            SizeInputField(CountryInfo.US, screenState.inputtedUsSize, screenState.isInputEnabled) { inputtedSize ->
                viewModel.onEvent(MainViewModelEvent.SizeInputted(CountryInfo.US, inputtedSize))
            }

            SizeInputField(CountryInfo.JP, screenState.inputtedJpSize, screenState.isInputEnabled) { inputtedSize ->
                viewModel.onEvent(MainViewModelEvent.SizeInputted(CountryInfo.JP, inputtedSize))
            }

            SizeInputField(CountryInfo.EU, screenState.inputtedEuSize, screenState.isInputEnabled) { inputtedSize ->
                viewModel.onEvent(MainViewModelEvent.SizeInputted(CountryInfo.EU, inputtedSize))
            }

            // (4) 버튼 구현
            Button(
                onClick = {
                    when (screenState.buttonText) {
                        "변환" -> { viewModel.onEvent(MainViewModelEvent.ConvertShoeSize) }
                        "초기화" -> { viewModel.onEvent(MainViewModelEvent.ResetData) }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                enabled = screenState.isButtonEnabled
            ) {
                Text(text = screenState.buttonText, fontSize = 30.sp)
            }
        }
    }
}

@Composable
fun SizeInputField(
    country: CountryInfo,
    valueParam: String,
    enabledParam: Boolean,
    onValueChangeParam: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "${country.countryName} ", fontSize = 30.sp)

        OutlinedTextField(
            value = valueParam,
            onValueChange = { inputtedSize ->
                // 0 ~ 9와 점(.)만 입력되도록 필터링
                var filteredInput = inputtedSize.replace(Regex("[^0-9.]"), "")
                // 점이 하나 이상 입력되지 않도록 필터링
                val dotCount = filteredInput.count { it == '.' }
                filteredInput = if (dotCount > 1) {
                    val firstDotIndex = filteredInput.indexOf('.')
                    val secondDotIndex = filteredInput.indexOf('.', firstDotIndex + 1)
                    filteredInput.substring(0, secondDotIndex) // 두 번째 점 이전까지의 문자열만 사용
                } else {
                    filteredInput
                }
                // 필터링된 값 ViewModel에 전달
                onValueChangeParam(filteredInput)
            },
            modifier = Modifier.weight(1f), // Row에서 2개의 Text()를 표시하고 남은 자리를, OutlinedTextField()가 전부 차지하게 만듦
            textStyle = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            ),
            enabled = enabledParam,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true
        )

        Text(text = " ${country.shoeSizeUnit}", fontSize = 30.sp)
    }
}