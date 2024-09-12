package com.example.objectorienteduilayer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.objectorienteduilayer.event.MainScreenEvent
import com.example.objectorienteduilayer.event.MainViewModelEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    // (1) 화면 표시용 State
    private val _mainScreenState = mutableStateOf(
        MainScreenState(
            inputtedKrSize = "",
            inputtedUsSize = "",
            inputtedJpSize = "",
            inputtedEuSize = "",
            isInputEnabled = true,

            buttonText = "변환",
            isButtonEnabled = false
        )
    )
    val mainScreenState: State<MainScreenState>
        get() = _mainScreenState

    // (2) MainScreenEvent: View에서 사용할 이벤트지만, 해당 이벤트를 트리거(발생)시키기 위해서 해당 이벤트를 담는 Flow 인스턴스만 선언 (이후에, View에서 이 Flow를 collect()하여 이벤트를 구현할 것임)
    private val _mainScreenEventFlow = MutableSharedFlow<MainScreenEvent>()
    val mainScreenEventFlow: SharedFlow<MainScreenEvent>
        get() = _mainScreenEventFlow.asSharedFlow()

    // (3) MainViewModelEvent: View로부터 받은 이벤트와 그 처리
    fun onEvent(event: MainViewModelEvent) {
        when (event) {
            is MainViewModelEvent.SizeInputted -> {
                _mainScreenState.value = when (event.country) {
                    CountryInfo.KR -> _mainScreenState.value.copy(
                        inputtedKrSize = event.inputtedSize,
                        inputtedUsSize = "",
                        inputtedJpSize = "",
                        inputtedEuSize = "",
                    )

                    CountryInfo.US -> _mainScreenState.value.copy(
                        inputtedKrSize = "",
                        inputtedUsSize = event.inputtedSize,
                        inputtedJpSize = "",
                        inputtedEuSize = "",
                    )

                    CountryInfo.JP -> _mainScreenState.value.copy(
                        inputtedKrSize = "",
                        inputtedUsSize = "",
                        inputtedJpSize = event.inputtedSize,
                        inputtedEuSize = "",
                    )

                    CountryInfo.EU -> _mainScreenState.value.copy(
                        inputtedKrSize = "",
                        inputtedUsSize = "",
                        inputtedJpSize = "",
                        inputtedEuSize = event.inputtedSize,
                    )
                }

                // '변환' 버튼 활성화 여부 결정
                _mainScreenState.value.isButtonEnabled =
                    !(_mainScreenState.value.inputtedKrSize.isEmpty() &&
                    _mainScreenState.value.inputtedUsSize.isEmpty() &&
                    _mainScreenState.value.inputtedJpSize.isEmpty() &&
                    _mainScreenState.value.inputtedEuSize.isEmpty())
            }

            is MainViewModelEvent.ConvertShoeSize -> {
                // 값 하나만 추리기
                val inputtedSizes: Map<CountryInfo, String> = mapOf(
                    CountryInfo.KR to _mainScreenState.value.inputtedKrSize,
                    CountryInfo.US to _mainScreenState.value.inputtedUsSize,
                    CountryInfo.JP to _mainScreenState.value.inputtedJpSize,
                    CountryInfo.EU to _mainScreenState.value.inputtedEuSize
                ).filter { // 빈 문자열이 아닌 값만 필터링 (이로 인해 분명 하나의 element만 남을 것임, 하나의 TextField에만 값이 들어가도록 이미 조치가 되어있기 때문)
                    it.value != ""
                }

                // (값 검증 - 1) 값이 하나인지  (사실상 나오지 않을 분기 ∵ 하나의 TextField에만 값이 들어가도록 이미 조치가 되어있기 때문)
                if (inputtedSizes.size != 1) {
                    viewModelScope.launch {
                        _mainScreenEventFlow.emit(
                            MainScreenEvent.ShowSnackbar("신발 사이즈가 2군데 이상 입력되었어요.")
                        )
                    }
                    return
                }

                // (값 검증 - 2) Double형으로 변환 가능한 지
                val inputtedSize = inputtedSizes.entries.first()
                val inputtedSizeValue = inputtedSize.value.toDoubleOrNull()

                if (inputtedSizeValue == null) {
                    viewModelScope.launch {
                        _mainScreenEventFlow.emit(
                            MainScreenEvent.ShowSnackbar("유효하지 않은 값이 입력되었어요.")
                        )
                    }
                    return
                }

                // 값 변환과 동시에 State 업데이트
                try {
                    _mainScreenState.value = _mainScreenState.value.copy(
                        inputtedKrSize = ConversionFormula.formatToTwoDecimalPlaces( // 값 반올림 함수
                            ConversionFormula.sizeToOtherSize(inputtedSize.key, inputtedSizeValue, CountryInfo.KR)), // 값 변환 함수
                        inputtedUsSize = ConversionFormula.formatToTwoDecimalPlaces(
                            ConversionFormula.sizeToOtherSize(inputtedSize.key, inputtedSizeValue, CountryInfo.US)),
                        inputtedJpSize = ConversionFormula.formatToTwoDecimalPlaces(
                            ConversionFormula.sizeToOtherSize(inputtedSize.key, inputtedSizeValue, CountryInfo.JP)),
                        inputtedEuSize = ConversionFormula.formatToTwoDecimalPlaces(
                            ConversionFormula.sizeToOtherSize(inputtedSize.key, inputtedSizeValue, CountryInfo.EU)),
                        isInputEnabled = false,

                        buttonText = "초기화",
                        isButtonEnabled = true
                    )

                // (값 검증 - 3) 값 변환 과정에서 사용되는 ConversionFormula에서 발생할 여지가 있는 Exception를 catch
                } catch (e: ConversionFormula.SizeOutOfBoundsException) {
                    viewModelScope.launch {
                        _mainScreenEventFlow.emit(
                            MainScreenEvent.ShowSnackbar("${inputtedSize.key.countryName} 사이즈의 범위는 ${e.minSize} ~ ${e.maxSize} 이내여야 해요.\n(내부 변환식의 한계)")
                        )
                    }
                    return
                }
            }

            is MainViewModelEvent.ResetData -> {
                _mainScreenState.value = _mainScreenState.value.copy(
                    inputtedKrSize = "",
                    inputtedUsSize = "",
                    inputtedJpSize = "",
                    inputtedEuSize = "",
                    isInputEnabled = true,

                    buttonText = "변환",
                    isButtonEnabled = false
                )
            }
        }
    }
}