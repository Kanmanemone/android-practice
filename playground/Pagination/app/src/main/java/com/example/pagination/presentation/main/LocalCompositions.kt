package com.example.pagination.presentation.main

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController

// LocalNavController은 UI 구성 요소의 context로서, '상수' 역할을 하기에 변수임에도 대문자로 시작하는 게 일반적이라고 함 (공식 문서에서도 이렇게 함)
val LocalNavController = compositionLocalOf<NavController> {
    error("No NavController provided")
}

val LocalMainViewModel = compositionLocalOf<MainViewModel> {
    error("MainViewModel not provided")
}