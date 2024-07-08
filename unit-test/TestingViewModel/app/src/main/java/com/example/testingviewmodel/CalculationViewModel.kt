package com.example.testingviewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalculationViewModel(private val calculation: Calculation) : ViewModel() {
    private val _operand1: MutableLiveData<Int> = MutableLiveData(0)
    val operand1: MutableLiveData<Int>
        get() = _operand1

    private val _operand2: MutableLiveData<Int> = MutableLiveData(0)
    val operand2: MutableLiveData<Int>
        get() = _operand2

    private val _result: MutableLiveData<Int> = MutableLiveData(0)
    val result: LiveData<Int>
        get() = _result

    fun onSumClicked() {
        _result.value = calculation.sum(_operand1.value!!, _operand2.value!!)
    }

    fun onMultiplyClicked() {
        _result.value = calculation.multiply(_operand1.value!!, _operand2.value!!)
    }

    fun onSumAndSquareClicked() {
        _result.value = calculation.sumAndSquare(_operand1.value!!, _operand2.value!!)
    }
}