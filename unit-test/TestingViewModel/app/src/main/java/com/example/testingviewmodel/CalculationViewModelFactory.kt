package com.example.testingviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CalculationViewModelFactory(private val calculation: Calculation) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalculationViewModel::class.java)) {
            return CalculationViewModel(calculation) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }
}