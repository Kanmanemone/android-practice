package com.example.pointerinputchangelogger

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

@SuppressLint("ModifierFactoryUnreferencedReceiver", "ReturnFromAwaitPointerEventScope")
fun Modifier.pointerInputChangeLogger(componentName: String) = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent()
            Log.d(componentName, "PointerEvent.type = ${event.type}")
            event.changes.forEach { change ->
                Log.d(
                    componentName,
                    """
                        PointerInputChange(
                            id = ${change.id}, 
                            uptimeMillis = ${change.uptimeMillis},
                            position = ${change.position}, 
                            pressed = ${change.pressed},
                            pressure = ${change.pressure}, 
                            previousUptimeMillis = ${change.previousUptimeMillis},
                            previousPosition = ${change.previousPosition}, 
                            previousPressed = ${change.previousPressed},
                            isInitiallyConsumed = ${change.isConsumed}, 
                            type = ${change.type},
                            scrollDelta = ${change.scrollDelta} 
                        ) 
                    """.trimIndent()
                )
            }
        }
    }
}