package com.example.ui.internal

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BottomSheetBackHandler(
    anchoredDraggableState: AnchoredDraggableState<SheetValue>
) {
    val scope = rememberCoroutineScope()
    BackHandler(enabled = (anchoredDraggableState.currentValue != SheetValue.Hidden)) {
        scope.launch {
            when (anchoredDraggableState.currentValue) {
                SheetValue.Expanded -> anchoredDraggableState.animateTo(SheetValue.PartiallyExpanded)
                SheetValue.PartiallyExpanded -> anchoredDraggableState.animateTo(SheetValue.Hidden)
                else -> {}
            }
        }
    }
}