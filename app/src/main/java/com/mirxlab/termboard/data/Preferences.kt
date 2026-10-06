package com.mirxlab.termboard.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mirxlab.termboard.data.models.Layer
import com.mirxlab.termboard.data.models.Combo
import com.mirxlab.termboard.data.models.KeyboardMode
import com.mirxlab.termboard.data.models.Macro
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "termboard_prefs")

class KeyboardPreferences(private val context: Context) {

    companion object {
        val KEY_OPACITY = floatPreferencesKey("keyboard_opacity")
        val KEY_HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
        val KEY_HAPTIC_INTENSITY = floatPreferencesKey("haptic_intensity")
        val KEY_SPLIT_MODE_AUTO = booleanPreferencesKey("split_mode_auto")
        val KEY_RECENT_EMOJIS = stringPreferencesKey("recent_emojis")
        val KEY_CENTER_GAP_RATIO = floatPreferencesKey("center_gap_ratio")
        val KEY_PORTRAIT_CENTER_GAP_RATIO = floatPreferencesKey("portrait_center_gap_ratio")
        val KEY_LANDSCAPE_CENTER_GAP_RATIO = floatPreferencesKey("landscape_center_gap_ratio")
        val KEY_DARK_THEME = booleanPreferencesKey("dark_theme")
        val KEY_LAYERS = stringPreferencesKey("keyboard_layers")
        val KEY_COMBOS = stringPreferencesKey("keyboard_combos")
        val KEY_KEYBOARD_HEIGHT = intPreferencesKey("keyboard_height_dp")
        val KEY_STANDARD_KEYBOARD_HEIGHT = intPreferencesKey("standard_keyboard_height_dp")
        val KEY_MODIFIER_HOLD_DURATION = intPreferencesKey("modifier_hold_duration_ms")
        val KEY_KEYBOARD_MODE_OVERRIDE = stringPreferencesKey("keyboard_mode_override")
        val KEY_MACROS = stringPreferencesKey("keyboard_macros")
        val KEY_STANDARD_LAYERS = stringPreferencesKey("standard_keyboard_layers")
        val KEY_STANDARD_COMPACT = booleanPreferencesKey("standard_keyboard_compact")
    }

    private val json = Json { ignoreUnknownKeys = true }

    val opacityFlow: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[KEY_OPACITY] ?: 0.85f
    }

    val centerGapRatioFlow: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[KEY_CENTER_GAP_RATIO] ?: 0.75f
    }

    val portraitCenterGapRatioFlow: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[KEY_PORTRAIT_CENTER_GAP_RATIO] ?: 1.1f
    }

    val landscapeCenterGapRatioFlow: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[KEY_LANDSCAPE_CENTER_GAP_RATIO] ?: prefs[KEY_CENTER_GAP_RATIO] ?: 0.75f
    }

    val splitKeyboardHeightDpFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_KEYBOARD_HEIGHT] ?: 300
    }

    val standardKeyboardHeightDpFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_STANDARD_KEYBOARD_HEIGHT] ?: 250
    }

    val modifierHoldDurationMsFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_MODIFIER_HOLD_DURATION] ?: 260
    }

    val keyboardModeOverrideFlow: Flow<KeyboardMode?> = context.dataStore.data.map { prefs ->
        prefs[KEY_KEYBOARD_MODE_OVERRIDE]?.let { runCatching { KeyboardMode.valueOf(it) }.getOrNull() }
    }

    val darkThemeFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_DARK_THEME] ?: true
    }

    val layersFlow: Flow<List<Layer>> = context.dataStore.data.map { prefs ->
        prefs[KEY_LAYERS]?.let { raw ->
            runCatching { json.decodeFromString<List<Layer>>(raw) }.getOrDefault(emptyList())
        } ?: emptyList()
    }

    val standardLayersFlow: Flow<List<Layer>> = context.dataStore.data.map { prefs ->
        prefs[KEY_STANDARD_LAYERS]?.let { raw ->
            runCatching { json.decodeFromString<List<Layer>>(raw) }.getOrDefault(emptyList())
        } ?: emptyList()
    }

    val standardCompactFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_STANDARD_COMPACT] ?: true
    }

    val combosFlow: Flow<List<Combo>> = context.dataStore.data.map { prefs ->
        prefs[KEY_COMBOS]?.let { raw ->
            runCatching { json.decodeFromString<List<Combo>>(raw) }.getOrDefault(emptyList())
        } ?: emptyList()
    }

    val macrosFlow: Flow<List<Macro>> = context.dataStore.data.map { prefs ->
        prefs[KEY_MACROS]?.let { raw ->
            runCatching { json.decodeFromString<List<Macro>>(raw) }.getOrDefault(emptyList())
        } ?: emptyList()
    }

    suspend fun setCenterGapRatio(ratio: Float) {
        context.dataStore.edit { prefs ->
            prefs[KEY_CENTER_GAP_RATIO] = ratio.coerceIn(0.15f, 1.5f)
        }
    }

    suspend fun setPortraitCenterGapRatio(ratio: Float) {
        context.dataStore.edit { prefs ->
            prefs[KEY_PORTRAIT_CENTER_GAP_RATIO] = ratio.coerceIn(0.15f, 1.5f)
        }
    }

    suspend fun setLandscapeCenterGapRatio(ratio: Float) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LANDSCAPE_CENTER_GAP_RATIO] = ratio.coerceIn(0.15f, 1.5f)
        }
    }

    suspend fun setSplitKeyboardHeightDp(heightDp: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_KEYBOARD_HEIGHT] = heightDp.coerceIn(240, 360)
        }
    }

    suspend fun setStandardKeyboardHeightDp(heightDp: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_STANDARD_KEYBOARD_HEIGHT] = heightDp.coerceIn(190, 340)
        }
    }

    suspend fun setModifierHoldDurationMs(durationMs: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_MODIFIER_HOLD_DURATION] = durationMs.coerceIn(120, 500)
        }
    }

    suspend fun setKeyboardModeOverride(mode: KeyboardMode?) {
        context.dataStore.edit { prefs ->
            if (mode == null) prefs.remove(KEY_KEYBOARD_MODE_OVERRIDE)
            else prefs[KEY_KEYBOARD_MODE_OVERRIDE] = mode.name
        }
    }

    suspend fun setLayers(layers: List<Layer>) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LAYERS] = json.encodeToString(layers)
        }
    }

    suspend fun setStandardLayers(layers: List<Layer>) {
        context.dataStore.edit { prefs ->
            prefs[KEY_STANDARD_LAYERS] = json.encodeToString(layers)
        }
    }

    suspend fun setStandardCompact(compact: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_STANDARD_COMPACT] = compact
        }
    }

    suspend fun setCombos(combos: List<Combo>) {
        context.dataStore.edit { prefs ->
            prefs[KEY_COMBOS] = json.encodeToString(combos)
        }
    }

    suspend fun setMacros(macros: List<Macro>) {
        context.dataStore.edit { prefs ->
            prefs[KEY_MACROS] = json.encodeToString(macros)
        }
    }

    suspend fun setDarkTheme(dark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DARK_THEME] = dark
        }
    }

    val hapticEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_HAPTIC_ENABLED] ?: true
    }

    val hapticIntensityFlow: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[KEY_HAPTIC_INTENSITY] ?: 1.0f
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
