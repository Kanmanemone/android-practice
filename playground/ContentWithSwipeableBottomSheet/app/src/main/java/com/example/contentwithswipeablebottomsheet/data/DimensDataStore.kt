package com.example.contentwithswipeablebottomsheet.data

import android.content.Context
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.math.roundToInt

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dimens")

class DimensDataStore private constructor(
    val imeMaxHeightStateFlow: StateFlow<Dp>,
    private val context: Context
) {
    companion object {
        val IME_MAX_HEIGHT_VALUE_KEY = intPreferencesKey("ime_max_height_dp")
        val DEFAULT_IME_MAX_HEIGHT_VALUE = 250.dp.value.roundToInt()

        suspend fun create(context: Context): DimensDataStore {
            val imeMaxHeightFlow: Flow<Dp> =
                context.dataStore.data.map { preferences ->
                    (preferences[IME_MAX_HEIGHT_VALUE_KEY] ?: DEFAULT_IME_MAX_HEIGHT_VALUE).dp
                }

            val imeMaxHeightStateFlow: StateFlow<Dp> =
                imeMaxHeightFlow.stateIn(
                    scope = CoroutineScope(Dispatchers.Default),
                    started = SharingStarted.WhileSubscribed(),
                    initialValue = imeMaxHeightFlow.first()
                )

            return DimensDataStore(imeMaxHeightStateFlow, context)
        }
    }

    suspend fun setImeMaxHeight(imeHeight: Dp) {
        context.dataStore.edit { preferences ->
            preferences[IME_MAX_HEIGHT_VALUE_KEY] = imeHeight.value.toInt()
        }
    }
}