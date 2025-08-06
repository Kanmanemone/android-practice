package com.example.contentwithswipeablebottomsheet.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.example.contentwithswipeablebottomsheet.component.core.SheetValue
import kotlinx.coroutines.launch

@Composable
fun BottomSheetBackHandler(
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