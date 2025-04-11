package com.example.keyboardlikebottomsheet

data class UiState(
    val inputtedText: String = "",
    val phase: UiPhase = UiPhase.Initial,
)

sealed class UiPhase {
    data object Initial : UiPhase()
    data object InputText : UiPhase()
    data object InputOtherInfo : UiPhase()
}