package com.example.objectorienteduilayer

data class MainScreenState(
    var inputtedKrSize: String,
    var inputtedUsSize: String,
    var inputtedJpSize: String,
    var inputtedEuSize: String,
    var isInputEnabled: Boolean,

    var buttonText: String,
    var isButtonEnabled: Boolean,
)