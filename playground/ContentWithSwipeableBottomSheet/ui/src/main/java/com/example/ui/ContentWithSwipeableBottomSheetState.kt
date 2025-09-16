package com.example.ui

import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

enum class BottomOccupier { None, Ime, BottomSheet }

@OptIn(ExperimentalMaterial3Api::class)
class ContentWithSwipeableBottomSheetState(
    val anchoredDraggableState: AnchoredDraggableState<SheetValue>,
    val isImeAppearingState: State<Boolean>
) {
    suspend fun hide() = anchoredDraggableState.animateTo(
        targetValue = SheetValue.Hidden,
        animationSpec = BottomSheetAnimationSpec
    )

    suspend fun partiallyExpand() = anchoredDraggableState.animateTo(
        targetValue = SheetValue.PartiallyExpanded,
        animationSpec = BottomSheetAnimationSpec
    )

    suspend fun expand() = anchoredDraggableState.animateTo(
        targetValue = SheetValue.Expanded,
        animationSpec = BottomSheetAnimationSpec
    )

    val currentValue: SheetValue
        get() = anchoredDraggableState.currentValue

    val settledValue: SheetValue
        get() = anchoredDraggableState.settledValue

    val targetValue: SheetValue
        get() = anchoredDraggableState.targetValue

    val bottomOccupier: BottomOccupier
        get() = if (isImeAppearingState.value) {
            BottomOccupier.Ime
        } else {
            if (anchoredDraggableState.targetValue == SheetValue.Hidden) {
                BottomOccupier.None
            } else {
                BottomOccupier.BottomSheet
            }
        }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun rememberContentWithSwipeableBottomSheetState(
    anchoredDraggableState: AnchoredDraggableState<SheetValue> = AnchoredDraggableState(SheetValue.Hidden),
): ContentWithSwipeableBottomSheetState {
    val imeAppearingState = remember { mutableStateOf(false) }

    val density = LocalDensity.current
    val imeTargetHeight by rememberUpdatedState(WindowInsets.imeAnimationTarget.getBottom(density))
    LaunchedEffect(density) {
        snapshotFlow { 0 < imeTargetHeight }
            .distinctUntilChanged()
            .collectLatest { imeAppearingState.value = it }
    }

    return remember {
        ContentWithSwipeableBottomSheetState(
            anchoredDraggableState = anchoredDraggableState,
            isImeAppearingState = imeAppearingState
        )
    }
}