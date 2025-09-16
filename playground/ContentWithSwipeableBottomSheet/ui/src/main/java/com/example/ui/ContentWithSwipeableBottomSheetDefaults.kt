package com.example.ui

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween

val BottomSheetAnimationSpec: AnimationSpec<Float> =
    tween(durationMillis = 300, easing = FastOutSlowInEasing)