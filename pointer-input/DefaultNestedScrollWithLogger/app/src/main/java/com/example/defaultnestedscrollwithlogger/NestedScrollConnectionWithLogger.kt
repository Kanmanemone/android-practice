package com.example.defaultnestedscrollwithlogger

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

class NestedScrollConnectionWithLogger(private val id: String) : NestedScrollConnection {

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val returnValue = Offset.Zero
        println("onPreScroll (${id}) available: ${available.y} return: ${returnValue.y} source: $source")
        return returnValue
    }

    override fun onPostScroll(
        consumed: Offset, available: Offset, source: NestedScrollSource
    ): Offset {
        val returnValue = Offset.Zero
        println("onPostScroll (${id}) consumed: ${consumed.y} available: ${available.y} return: ${returnValue.y} source: $source")
        return returnValue
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val returnValue = Velocity.Zero
        println("onPreFling (${id}) available: ${available.y} return: ${returnValue.y}")
        return returnValue
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        val returnValue = Velocity.Zero
        println("onPostFling (${id}) consumed: ${consumed.y} available: ${available.y} return: ${returnValue.y}")
        return returnValue
    }
}