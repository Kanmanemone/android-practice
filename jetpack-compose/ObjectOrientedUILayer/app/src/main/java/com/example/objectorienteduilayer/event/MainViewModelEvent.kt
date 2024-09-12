package com.example.objectorienteduilayer.event

import com.example.objectorienteduilayer.CountryInfo

sealed class MainViewModelEvent {
    // 인수를 가지기에, 각 인스턴스는 서로 구별됨. 따라서 data class로 두어 복수 개의 인스턴스를 가지게 해야 함
    data class SizeInputted(val country: CountryInfo, val inputtedSize: String) : MainViewModelEvent()

    // 인수를 가지지 않기에, 각 인스턴스는 전부 똑같음. 따라서 data object로 두어 하나의 인스턴스만 가지게 해야 함 (자원 절약)
    data object ConvertShoeSize : MainViewModelEvent()
    data object ResetData : MainViewModelEvent()
}