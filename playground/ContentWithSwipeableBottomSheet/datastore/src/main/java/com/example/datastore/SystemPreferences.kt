package com.example.datastore

import android.content.Context
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.roundToInt

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "system")

object SystemPreferences {
    val IME_MAX_HEIGHT_VALUE_KEY = intPreferencesKey("ime_max_height_dp")
    val DEFAULT_IME_MAX_HEIGHT_VALUE = 250.dp.value.roundToInt()

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context
    }

    fun getImeMaxHeight(): Flow<Dp> {
        return appContext.dataStore.data.map { preferences ->
            (preferences[IME_MAX_HEIGHT_VALUE_KEY] ?: DEFAULT_IME_MAX_HEIGHT_VALUE).dp
        }
    }

    suspend fun setImeMaxHeight(imeHeight: Dp) {
        appContext.dataStore.edit { preferences ->
            preferences[IME_MAX_HEIGHT_VALUE_KEY] = imeHeight.value.toInt()
        }
    }
}