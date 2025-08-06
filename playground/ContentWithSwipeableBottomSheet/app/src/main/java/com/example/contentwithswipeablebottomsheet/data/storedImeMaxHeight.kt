package com.example.contentwithswipeablebottomsheet.data

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.example.contentwithswipeablebottomsheet.CwsbsApplication
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun storedImeMaxHeight(): Dp {
    val dimensDataStore = (LocalContext.current.applicationContext as CwsbsApplication).dimensDataStore

    val density = LocalDensity.current
    val imeTargetHeight by rememberUpdatedState(WindowInsets.imeAnimationTarget.getBottom(density))

    LaunchedEffect(density) {
        snapshotFlow { imeTargetHeight }
            .filter { 0 < it }
            .distinctUntilChanged()
            .collectLatest {
                with(density) {
                    dimensDataStore.setImeMaxHeight(it.toDp())
                }
            }
    }

    return dimensDataStore.imeMaxHeightStateFlow.collectAsState().value
}