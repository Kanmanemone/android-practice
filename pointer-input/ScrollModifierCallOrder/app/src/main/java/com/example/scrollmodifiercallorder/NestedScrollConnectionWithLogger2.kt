package com.example.scrollmodifiercallorder

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity

// 테스트용 및 놀이(장난감)용 클래스.
@Composable
@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.nestedScrollWithLogger2(id: String): Modifier {
    return this.nestedScroll(NestedScrollConnectionWithSimpleLogger2(id))
}

class NestedScrollConnectionWithSimpleLogger2(private val id: String) : NestedScrollConnection {

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        println("$id onPreScroll available delta: $available")
        return Offset.Zero
    }

    override fun onPostScroll(
        consumed: Offset, available: Offset, source: NestedScrollSource
    ): Offset {
        println("$id onPostScroll available delta: $available")
        return available //Offset.Zero
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        //println("$id onPreFling available velocity: $available")
        return Velocity.Zero
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        //println("$id onPostFling available velocity: $available")
        return Velocity.Zero
    }
}