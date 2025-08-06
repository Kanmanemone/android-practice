package com.example.contentwithswipeablebottomsheet.component.core

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMaxBy
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.zIndex

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ContentWithSwipeableBottomSheet(
    sheetContent: @Composable (() -> Unit),
    modifier: Modifier = Modifier,
    sheetState: ContentWithSwipeableBottomSheetState = rememberContentWithSwipeableBottomSheetState(),
    sheetExpandedAnchorOffset: Dp = 0.dp,
    sheetPartiallyExpandedHeight: Dp = 300.dp,
    sheetShape: Shape = BottomSheetDefaults.ExpandedShape,
    sheetContainerColor: Color = BottomSheetDefaults.ContainerColor,
    sheetContentColor: Color = contentColorFor(sheetContainerColor),
    sheetTonalElevation: Dp = 0.dp,
    sheetShadowElevation: Dp = BottomSheetDefaults.Elevation,
    sheetDragHandle: @Composable (() -> Unit) = { BottomSheetDefaults.DragHandle() },
    sheetSwipeEnabled: Boolean = true,
    hasContentNavigationBarPadding: Boolean = false,
    content: @Composable (() -> Unit),
) {
    AssertSoftInputModeIsAdjustNothing()

    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
    ) {
        val layoutHeight = this.maxHeight
        val anchoredDraggableState = sheetState.anchoredDraggableState

        LaunchedEffect(density, layoutHeight, sheetExpandedAnchorOffset, sheetPartiallyExpandedHeight) {
            anchoredDraggableState.updateAnchors(
                DraggableAnchors<SheetValue> {
                    SheetValue.Expanded at with(density) { sheetExpandedAnchorOffset.toPx() }
                    SheetValue.PartiallyExpanded at with(density) { (layoutHeight - sheetPartiallyExpandedHeight).toPx() }
                    SheetValue.Hidden at with(density) { layoutHeight.toPx() }
                }
            )
        }

        ContentWithSwipeableBottomSheetLayout(
            anchoredDraggableState = anchoredDraggableState,
            body = content,
            bottomSheet = {
                BottomSheetLayout(
                    anchoredDraggableState = anchoredDraggableState,
                    sheetSwipeEnabled = sheetSwipeEnabled,
                    shape = sheetShape,
                    containerColor = sheetContainerColor,
                    contentColor = sheetContentColor,
                    tonalElevation = sheetTonalElevation,
                    shadowElevation = sheetShadowElevation,
                    dragHandle = sheetDragHandle,
                    content = sheetContent
                )
            },
            hasBodyNavigationBarPadding = hasContentNavigationBarPadding
        )
    }
}

@Composable
private fun AssertSoftInputModeIsAdjustNothing() {
    val context = LocalView.current.context
    val activity = context as Activity
    val window = activity.window
    val softInputAdjustType by remember { mutableIntStateOf(window.attributes.softInputMode and WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST) }
    LaunchedEffect(window) {
        check(softInputAdjustType == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING) {
            """
                Expected SOFT_INPUT_ADJUST_NOTHING but was $softInputAdjustType
                해결법: Activity의 setContent {} 호출 전에, window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)와 같은 코드를 사용하세요.
            """.trimIndent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContentWithSwipeableBottomSheetLayout(
    anchoredDraggableState: AnchoredDraggableState<SheetValue>,
    body: @Composable (() -> Unit),
    bottomSheet: @Composable (() -> Unit),
    hasBodyNavigationBarPadding: Boolean
) {
    if (!anchoredDraggableState.offset.isNaN()) {
        val density = LocalDensity.current
        val navigationBarHeight = if (hasBodyNavigationBarPadding) {
            with(density) { WindowInsets.navigationBars.getBottom(density) }
        } else {
            null
        }

        SubcomposeLayout { constraints ->
            val layoutWidth = constraints.maxWidth
            val layoutHeight = constraints.maxHeight
            val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

            val sheetOffsetY = anchoredDraggableState.offset

            val bodyPlaceables = subcompose("body") { body() }.fastMap {
                val sheetPartiallyExpandedOffset = anchoredDraggableState.anchors.positionOf(SheetValue.PartiallyExpanded)
                val sheetHiddenOffset = anchoredDraggableState.anchors.positionOf(SheetValue.Hidden)
                val maxHeightAffectedBySheetOffsetY = (sheetOffsetY + (navigationBarHeight ?: 0)).coerceIn(
                    minimumValue = sheetPartiallyExpandedOffset + (navigationBarHeight ?: 0),
                    maximumValue = sheetHiddenOffset
                )

                val imePlaceables = subcompose("ime") { Spacer(modifier = Modifier.imePadding()) }.fastMap { it.measure(looseConstraints) }
                val imeOffsetY = layoutHeight - (imePlaceables.fastMaxBy { it.height }?.height ?: 0)
                val maxHeightAffectedByImeOffsetY = (imeOffsetY + (navigationBarHeight ?: 0)).coerceAtMost(
                    maximumValue = sheetHiddenOffset.fastRoundToInt()
                )

                it.measure(
                    looseConstraints.copy(
                        maxHeight = minOf(
                            maxHeightAffectedBySheetOffsetY.fastRoundToInt(),
                            maxHeightAffectedByImeOffsetY
                        )
                    )
                )
            }

            val bottomSheetPlaceables = subcompose("bottomSheet") { bottomSheet() }.fastMap {
                it.measure(
                    looseConstraints.copy(
                        maxHeight = (layoutHeight - sheetOffsetY).fastRoundToInt()
                    )
                )
            }

            layout(layoutWidth, layoutHeight) {
                bodyPlaceables.fastForEach { it.place(0, 0) }
                bottomSheetPlaceables.fastForEach { it.place(0, sheetOffsetY.fastRoundToInt()) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetLayout(
    anchoredDraggableState: AnchoredDraggableState<SheetValue>,
    sheetSwipeEnabled: Boolean,
    shape: Shape,
    containerColor: Color,
    contentColor: Color,
    tonalElevation: Dp,
    shadowElevation: Dp,
    dragHandle: @Composable (() -> Unit),
    content: @Composable (() -> Unit),
) {
    val density = LocalDensity.current

    Surface(
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
    ) {
        Column(
            modifier = Modifier
                .background(containerColor)
                .zIndex(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .anchoredDraggable(
                        state = anchoredDraggableState,
                        orientation = Orientation.Vertical,
                        enabled = sheetSwipeEnabled,
                        flingBehavior = AnchoredDraggableDefaults.flingBehavior(
                            state = anchoredDraggableState,
                            positionalThreshold = { with(density) { 56.dp.toPx() } },
                            animationSpec = BottomSheetAnimationSpec
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                dragHandle()
            }
            content()
        }
    }
}
