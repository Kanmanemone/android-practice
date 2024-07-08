package com.example.testingviewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class CalculationViewModelTest {

    /* InstantTaskExecutorRule 규칙(Rule)
     * 기본적으로 백그라운드 스레드에서 실행되는 LiveData를 메인 스레드에서 실행되도록 강제함.
     * LiveData가 백그라운드 스레드에서 그 값이 변경된다면, 메인 스레드에서 변화를 감지하지 못할 수도 있기 때문.
     * 또, 데이터의 변화를 감지하기 전에 테스트가 끝나버릴 여지도 있음.
     * 따라서, LiveData가 관여되는 테스트에는 InstantTaskExecutorRule 규칙이 많이 사용됨.
     */
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var calculation: Calculation
    private lateinit var calculationViewModel: CalculationViewModel

    @Before
    fun setUp() {
        // Dependency (Test double) 초기화
        calculation = Mockito.mock(Calculation::class.java)
        Mockito.`when`(calculation.sum(2, 3)).thenReturn(5)
        Mockito.`when`(calculation.multiply(5, 4)).thenReturn(20)
        Mockito.`when`(calculation.sumAndSquare(4, 7)).thenReturn(121)

        // Dependent 초기화
        calculationViewModel = CalculationViewModel(calculation)
    }

    @Test
    fun onSumClicked_withOperands2And3_updateLiveData() { // subjectUnderTest_actionOrInput_resultState 규칙으로 함수 명명함
        calculationViewModel.operand1.value = 2
        calculationViewModel.operand2.value = 3
        calculationViewModel.onSumClicked()
        val result = calculationViewModel.result.value
        Truth.assertThat(result).isEqualTo(/* expected = */ 5)
    }

    @Test
    fun onMultiplyClicked_withOperands5And4_updateLiveData() {
        calculationViewModel.operand1.value = 5
        calculationViewModel.operand2.value = 4
        calculationViewModel.onMultiplyClicked()
        val result = calculationViewModel.result.value
        Truth.assertThat(result).isEqualTo(/* expected = */ 20)
    }

    @Test
    fun onSumAndSquare_withOperands4And7_updateLiveData() {
        calculationViewModel.operand1.value = 4
        calculationViewModel.operand2.value = 7
        calculationViewModel.onSumAndSquareClicked()
        val result = calculationViewModel.result.value
        Truth.assertThat(result).isEqualTo(/* expected = */ 121)
    }
}