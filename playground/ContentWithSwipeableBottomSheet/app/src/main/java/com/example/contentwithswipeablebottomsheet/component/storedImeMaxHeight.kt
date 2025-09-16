package com.example.contentwithswipeablebottomsheet.component

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.datastore.SystemPreferences
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

// DataStore에 저장된 ime의 최대 높이를 꺼내옴
// 동시에 UI에서 더 큰 높이가 감지되면 값을 갱신함
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun storedImeMaxHeight(): Dp {
    val density = LocalDensity.current
    val imeTargetHeight by rememberUpdatedState(WindowInsets.imeAnimationTarget.getBottom(density))

    LaunchedEffect(density) {
        snapshotFlow { imeTargetHeight }
            .filter { 0 < it }
            .distinctUntilChanged()
            .collectLatest {
                with(density) {
                    SystemPreferences.setImeMaxHeight(it.toDp())
                }
            }
    }

    return SystemPreferences.getImeMaxHeight().collectAsState(
        initial = SystemPreferences.DEFAULT_IME_MAX_HEIGHT_VALUE.dp
    ).value
}