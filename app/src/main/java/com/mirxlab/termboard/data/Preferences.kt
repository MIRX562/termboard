package com.mirxlab.termboard.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "termboard_prefs")

class KeyboardPreferences(private val context: Context) {

    companion object {
        val KEY_OPACITY = floatPreferencesKey("keyboard_opacity")
        val KEY_HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
        val KEY_HAPTIC_INTENSITY = floatPreferencesKey("haptic_intensity")
        val KEY_SPLIT_MODE_ENABLED = booleanPreferencesKey("split_mode_enabled")
        val KEY_SPLIT_MODE_AUTO = booleanPreferencesKey("split_mode_auto")
        val KEY_RECENT_EMOJIS = stringPreferencesKey("recent_emojis")
    }

    val opacityFlow: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[KEY_OPACITY] ?: 1.0f
    }

    val hapticEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_HAPTIC_ENABLED] ?: true
    }

    val hapticIntensityFlow: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[KEY_HAPTIC_INTENSITY] ?: 1.0f
    }

    val splitModeEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_SPLIT_MODE_ENABLED] ?: false
    }

    val splitModeAutoFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_SPLIT_MODE_AUTO] ?: true
    }

    val recentEmojisFlow: Flow<List<String>> = context.dataStore.data.map { prefs ->
        val raw = prefs[KEY_RECENT_EMOJIS] ?: ""
        if (raw.isBlank()) emptyList() else raw.split(",")
    }

    suspend fun setOpacity(opacity: Float) {
        context.dataStore.edit { prefs ->
            prefs[KEY_OPACITY] = opacity.coerceIn(0.2f, 1.0f)
        }
    }

    suspend fun setHapticEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_HAPTIC_ENABLED] = enabled
        }
    }

    suspend fun setHapticIntensity(intensity: Float) {
        context.dataStore.edit { prefs ->
            prefs[KEY_HAPTIC_INTENSITY] = intensity
        }
    }

    suspend fun setSplitModeEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SPLIT_MODE_ENABLED] = enabled
        }
    }

    suspend fun setSplitModeAuto(auto: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SPLIT_MODE_AUTO] = auto
        }
    }

    suspend fun addRecentEmoji(emoji: String) {
        context.dataStore.edit { prefs ->
            val raw = prefs[KEY_RECENT_EMOJIS] ?: ""
            val list = if (raw.isBlank()) mutableListOf() else raw.split(",").toMutableList()
            list.remove(emoji)
            list.add(0, emoji)
            if (list.size > 30) {
                list.removeAt(list.lastIndex)
            }
            prefs[KEY_RECENT_EMOJIS] = list.joinToString(",")
        }
    }
}
