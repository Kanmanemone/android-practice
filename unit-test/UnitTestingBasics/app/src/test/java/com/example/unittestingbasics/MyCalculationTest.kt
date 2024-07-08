package com.example.unittestingbasics

import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test

class MyCalculationTest {
    private lateinit var myCalculation: MyCalculation

    @Before
    fun setUp() {
        myCalculation = MyCalculation()
    }

    @Test
    fun sum_Given77And777_return854() { // subjectUnderTest_actionOrInput_resultState 규칙으로 함수 명명함
        val result = myCalculation.sum(77, 777)
        Truth.assertThat(result).isEqualTo(/* expected = */ 854)
    }

    @Test
    fun multiply_Given3And4_return12() {
        val result = myCalculation.multiply(3, 4)
        Truth.assertThat(result).isEqualTo(/* expected = */ 12)
    }

    @Test
    fun sumAndSqaure_Given9And5_return196() {
        val result = myCalculation.sumAndSquare(9, 5)
        Truth.assertThat(result).isEqualTo(/* expected = */ 196)
    }
}