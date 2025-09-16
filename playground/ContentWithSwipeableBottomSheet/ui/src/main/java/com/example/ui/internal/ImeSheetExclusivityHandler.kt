package com.example.ui.internal

import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imeAnimationSource
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ImeSheetExclusivityHandler(
    anchoredDraggableState: AnchoredDraggableState<SheetValue>
) {
    val imeController = LocalSoftwareKeyboardController.current
    val isImeSettledVisible by rememberIsImeSettledVisible()

    // ↓ LaunchedEffect 요약: ime가 전부 올라온 직후 트리거, bottomSheet를 hidden 상태로
    LaunchedEffect(Unit) {
        snapshotFlow { isImeSettledVisible }
            .filter { it } // ime가 완전히(settled) 올라왔는데(visible),
            .filter { anchoredDraggableState.settledValue != SheetValue.Hidden } // bottomSheet가 숨겨진(hidden) 상태가 아니라면 (= ime와 bottomSheet가 겹쳐져 있는 상태라면),
            .collectLatest {
                anchoredDraggableState.snapTo(SheetValue.Hidden)
            }
    }

    // ↓ LaunchedEffect 요약: bottomSheet가 조금이라도 올라오기 직전 트리거, bottomSheet를 즉시 배치 후 ime를 hide
    val isBottomSheetTransitioningFromHidden by remember {
        derivedStateOf {
            anchoredDraggableState.settledValue == SheetValue.Hidden
                    && anchoredDraggableState.targetValue != SheetValue.Hidden
        }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { isBottomSheetTransitioningFromHidden }
            .filter { it } // bottomSheet가 숨겨진(hidden) 상태에서 벗어나려 하는데,
            .filter { isImeSettledVisible } // 이미 완전히(settled) 자리잡은(visible) ime가 있다면,
            .collectLatest {
                when (anchoredDraggableState.targetValue) {
                    SheetValue.PartiallyExpanded -> {
                        // 먼저 bottomSheet를 즉시 배치하고,
                        anchoredDraggableState.snapTo(SheetValue.PartiallyExpanded)
                        // ime를 숨김 (이렇게 해야 자연스럽게 스르륵 bottomSheet가 드러나는 화면이 나옴. ime는 시스템 window라 항상 bottomSheet보다 위에 표시되기 때문)
                        imeController?.hide()
                    }

                    SheetValue.Expanded -> {
                        anchoredDraggableState.snapTo(SheetValue.PartiallyExpanded)
                        imeController?.hide()
                        anchoredDraggableState.animateTo(SheetValue.Expanded)
                    }

                    else -> {}
                }
            }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun rememberIsImeSettledVisible(): State<Boolean> {
    val density = LocalDensity.current
    val source = WindowInsets.imeAnimationSource
    val target = WindowInsets.imeAnimationTarget

    return remember {
        derivedStateOf {
            val sourceHeight = source.getBottom(density)
            val targetHeight = target.getBottom(density)

            sourceHeight > 0 && sourceHeight == targetHeight
        }
    }
}