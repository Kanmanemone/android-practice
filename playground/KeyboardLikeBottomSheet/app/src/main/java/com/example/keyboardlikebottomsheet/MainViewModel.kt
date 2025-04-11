package com.example.keyboardlikebottomsheet

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        UiState(
            inputtedText = "",
            phase = UiPhase.Initial
        )
    )

    val uiState: StateFlow<UiState>
        get() = _uiState

    fun requestPhase(phase: UiPhase) {
        _uiState.value = _uiState.value.copy(
            phase = phase
        )
    }

    fun onTextValueChange(text: String) {
        _uiState.value = _uiState.value.copy(
            inputtedText = text
        )
    }
}