package com.mirxlab.termboard.ui

import android.view.inputmethod.InputConnection
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mirxlab.termboard.data.KeyboardPreferences
import com.mirxlab.termboard.data.models.KeyboardMode
import com.mirxlab.termboard.data.models.KeyboardSettings
import com.mirxlab.termboard.engine.KeyboardEngine
import com.mirxlab.termboard.engine.KeyboardLayouts
import com.mirxlab.termboard.engine.StandardKeyboardLayout
import com.mirxlab.termboard.ime.CustomInputMethodService
import com.mirxlab.termboard.ime.InputConnectionUtils
import com.mirxlab.termboard.ime.InputMode
import com.mirxlab.termboard.ui.clipboard.ClipboardManagerWrapper
import com.mirxlab.termboard.ui.clipboard.ClipboardView
import com.mirxlab.termboard.ui.components.UtilityToolbar
import com.mirxlab.termboard.ui.emoji.EmojiPickerView
import com.mirxlab.termboard.ui.layouts.SplitLayoutView
import com.mirxlab.termboard.ui.layouts.StandardLayoutView
import com.mirxlab.termboard.ui.theme.TermboardTheme
import kotlinx.coroutines.launch

@Composable
fun KeyboardView(service: CustomInputMethodService) {
    val context = LocalContext.current
    val prefs = remember { KeyboardPreferences(context) }
    val inputConnectionProvider: () -> InputConnection? = remember(service) {
        { service.currentInputConnection }
    }

    val opacity by prefs.opacityFlow.collectAsState(initial = 0.85f)
    val hapticEnabled by prefs.hapticEnabledFlow.collectAsState(initial = true)
    val splitModeAuto by prefs.splitModeAutoFlow.collectAsState(initial = true)
    val portraitCenterGapRatio by prefs.portraitCenterGapRatioFlow.collectAsState(initial = 1.1f)
    val landscapeCenterGapRatio by prefs.landscapeCenterGapRatioFlow.collectAsState(initial = 0.75f)
    val splitKeyboardHeightDp by prefs.splitKeyboardHeightDpFlow.collectAsState(initial = 300)
    val standardKeyboardHeightDp by prefs.standardKeyboardHeightDpFlow.collectAsState(initial = 250)
    val modifierHoldDurationMs by prefs.modifierHoldDurationMsFlow.collectAsState(initial = 260)
    val keyboardModeOverride by prefs.keyboardModeOverrideFlow.collectAsState(initial = null)
    val scope = rememberCoroutineScope()
    val darkTheme by prefs.darkThemeFlow.collectAsState(initial = true)
    val storedLayers by prefs.layersFlow.collectAsState(initial = emptyList())
    val standardLayers by prefs.standardLayersFlow.collectAsState(initial = emptyList())
    val standardCompact by prefs.standardCompactFlow.collectAsState(initial = true)
    val storedCombos by prefs.combosFlow.collectAsState(initial = emptyList())
    val storedMacros by prefs.macrosFlow.collectAsState(initial = emptyList())

    val currentInputMode by service.currentInputMode
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val screenHeightDp = configuration.screenHeightDp

    val isPortrait = screenHeightDp >= screenWidthDp
    val isSplit = splitModeAuto && screenWidthDp >= 600
    val centerGapRatio = if (isPortrait) portraitCenterGapRatio else landscapeCenterGapRatio
    val keyboardMode = keyboardModeOverride ?: if (isSplit) KeyboardMode.SPLIT else KeyboardMode.STANDARD
    val keyboardHeightDp = if (keyboardMode == KeyboardMode.SPLIT) splitKeyboardHeightDp else standardKeyboardHeightDp
    val keyRowCount = if (keyboardMode == KeyboardMode.STANDARD && standardCompact) 4 else 6
    val keyHeightDp = ((keyboardHeightDp - 16) / keyRowCount).coerceIn(32, 56)

    val engine = remember(keyboardMode) {
        KeyboardEngine(
            initialSettings = KeyboardSettings(
                mode = keyboardMode,
                compactStandardLayout = standardCompact,
                centerGapRatio = centerGapRatio,
                keyboardHeightDp = keyboardHeightDp,
                keyHeightDp = keyHeightDp,
                modifierHoldDurationMs = modifierHoldDurationMs,
                translucencyAlpha = opacity,
                hapticEnabled = hapticEnabled
            )
        )
    }

    // Sync settings changes
    engine.updateSettings(
        engine.settings.value.copy(
            mode = keyboardMode,
            compactStandardLayout = standardCompact,
            centerGapRatio = centerGapRatio,
            keyboardHeightDp = keyboardHeightDp,
            keyHeightDp = keyHeightDp,
            modifierHoldDurationMs = modifierHoldDurationMs,
            translucencyAlpha = opacity,
            hapticEnabled = hapticEnabled
        )
    )
    engine.setLayers(
        if (keyboardMode == KeyboardMode.STANDARD) {
            StandardKeyboardLayout.defaultLayers + standardLayers.associateBy { it.id }
        } else {
            KeyboardLayouts.defaultLayers + storedLayers.associateBy { it.id }
        }
    )
    engine.setCombos(storedCombos)
    engine.setMacros(storedMacros)
    engine.setInputModeHandler(service::setInputMode)

    val activeLayerId by engine.activeLayerId
    val activeLayerName = engine.layers.value[activeLayerId]?.displayName ?: activeLayerId

    val clipboardWrapper = remember { ClipboardManagerWrapper(context) }

    DisposableEffect(Unit) {
        clipboardWrapper.startListening()
        onDispose {
            clipboardWrapper.stopListening()
        }
    }

    TermboardTheme(darkTheme = darkTheme) {
        val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = opacity)

        Surface(
            color = Color.Transparent,
            modifier = Modifier.fillMaxWidth()
        ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(surfaceColor)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Active Panel View
                when (currentInputMode) {
                    InputMode.MAIN_KEYBOARD -> {
                        Box(modifier = Modifier.height(keyboardHeightDp.dp)) {
                            if (engine.settings.value.mode == KeyboardMode.SPLIT) {
                                SplitLayoutView(
                                    engine = engine,
                                    inputConnectionProvider = inputConnectionProvider
                                )
                            } else {
                                StandardLayoutView(
                                    engine = engine,
                                    inputConnectionProvider = inputConnectionProvider
                                )
                            }
                        }
                    }

                    InputMode.CLIPBOARD -> {
                        Box(modifier = Modifier.height(keyboardHeightDp.dp)) {
                            ClipboardView(
                                clipboardWrapper = clipboardWrapper,
                                onClipSelected = { text ->
                                    InputConnectionUtils.commitText(
                                        service.currentInputConnection,
                                        text
                                    )
                                    engine.resetAfterExternalTextCommit()
                                    service.setInputMode(InputMode.MAIN_KEYBOARD)
                                },
                                onBackToKeyboard = { service.setInputMode(InputMode.MAIN_KEYBOARD) },
                                backgroundColor = surfaceColor
                            )
                        }
                    }

                    InputMode.EMOJI -> {
                        Box(modifier = Modifier.height(keyboardHeightDp.dp)) {
                            EmojiPickerView(
                                onEmojiSelected = { emoji ->
                                    InputConnectionUtils.commitText(
                                        service.currentInputConnection,
                                        emoji
                                    )
                                },
                                onBackspace = {
                                    InputConnectionUtils.sendBackspace(
                                        service.currentInputConnection
                                    )
                                },
                                onBackToKeyboard = { service.setInputMode(InputMode.MAIN_KEYBOARD) },
                                backgroundColor = surfaceColor
                            )
                        }
                    }
                }

                UtilityToolbar(
                    activeInputMode = currentInputMode,
                    currentKeyboardMode = keyboardMode,
                    activeLayerName = activeLayerName,
                    onInputModeChange = { service.setInputMode(it) },
                    onToggleKeyboardMode = {
                        val newMode = if (keyboardMode == KeyboardMode.SPLIT) KeyboardMode.STANDARD else KeyboardMode.SPLIT
                        scope.launch { prefs.setKeyboardModeOverride(newMode) }
                    }
                )
            }
        }
        }
    }
}
