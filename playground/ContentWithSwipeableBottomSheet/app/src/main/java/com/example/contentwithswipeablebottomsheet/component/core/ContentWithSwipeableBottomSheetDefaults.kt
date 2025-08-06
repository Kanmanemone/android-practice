package com.example.contentwithswipeablebottomsheet.component.core

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween

enum class SheetValue { Hidden, PartiallyExpanded, Expanded }

val BottomSheetAnimationSpec: AnimationSpec<Float> =
    tween(durationMillis = 300, easing = FastOutSlowInEasing)