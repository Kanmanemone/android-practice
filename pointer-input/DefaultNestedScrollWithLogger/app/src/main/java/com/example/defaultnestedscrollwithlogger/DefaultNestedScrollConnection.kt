package com.example.defaultnestedscrollwithlogger

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

// 사용되지 않음
object DefaultNestedScrollConnection : NestedScrollConnection {

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        return Offset.Zero
    }

    override fun onPostScroll(
        consumed: Offset, available: Offset, source: NestedScrollSource
    ): Offset {
        return Offset.Zero
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        return Velocity.Zero
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        return Velocity.Zero
    }
}