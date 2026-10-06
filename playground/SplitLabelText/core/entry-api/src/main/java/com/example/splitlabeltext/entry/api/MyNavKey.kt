package com.example.splitlabeltext.entry.api

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey

interface MyNavKey : NavKey {
    val title: String
    val screen: @Composable () -> Unit

    /**
     * 이 화면이 무엇을 보여주는지 설명하는 문구. 정보 다이얼로그에 표시된다.
     *
     * - 빈 문자열이면 정보 아이콘을 띄우지 않는다.
     */
    val description: String get() = ""
}
