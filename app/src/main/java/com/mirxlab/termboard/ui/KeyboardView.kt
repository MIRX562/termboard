package com.mirxlab.termboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mirxlab.termboard.data.KeyboardPreferences
import com.mirxlab.termboard.ime.CustomInputMethodService
import com.mirxlab.termboard.ime.InputConnectionUtils
import com.mirxlab.termboard.ime.InputMode
import com.mirxlab.termboard.ime.KeyEventSynthesizer
import com.mirxlab.termboard.ui.clipboard.ClipboardManagerWrapper
import com.mirxlab.termboard.ui.clipboard.ClipboardView
import com.mirxlab.termboard.ui.emoji.EmojiPickerView
import com.mirxlab.termboard.ui.layouts.SplitLayout
import com.mirxlab.termboard.ui.layouts.StandardLayout
import kotlinx.coroutines.MainScope

@Composable
fun KeyboardView(service: CustomInputMethodService) {
    val context = LocalContext.current
    val prefs = remember { KeyboardPreferences(context) }
    val synthesizer = remember { KeyEventSynthesizer() }
    val clipboardWrapper = remember { ClipboardManagerWrapper(context) }

    DisposableEffect(Unit) {
        clipboardWrapper.startListening()
        onDispose {
            clipboardWrapper.stopListening()
        }
    }

    val opacity by prefs.opacityFlow.collectAsState(initial = 1.0f)
    val hapticEnabled by prefs.hapticEnabledFlow.collectAsState(initial = true)
    val splitModeEnabled by prefs.splitModeEnabledFlow.collectAsState(initial = false)
    val splitModeAuto by prefs.splitModeAutoFlow.collectAsState(initial = true)

    val currentInputMode by service.currentInputMode
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp

    val isSplit = splitModeEnabled || (splitModeAuto && screenWidthDp >= 600)

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
                // Toolbar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { service.setInputMode(InputMode.MAIN_KEYBOARD) },
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = "Main Keyboard",
                            tint = if (currentInputMode == InputMode.MAIN_KEYBOARD) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }

                    IconButton(
                        onClick = { service.setInputMode(InputMode.CLIPBOARD) },
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Assignment,
                            contentDescription = "Clipboard History",
                            tint = if (currentInputMode == InputMode.CLIPBOARD) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }

                    IconButton(
                        onClick = { service.setInputMode(InputMode.EMOJI) },
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEmotions,
                            contentDescription = "Emoji Picker",
                            tint = if (currentInputMode == InputMode.EMOJI) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }

                    IconButton(
                        onClick = {
                            MainScope().run {
                                // Toggle split mode preference
                            }
                        },
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Splitscreen,
                            contentDescription = "Toggle Split Mode",
                            tint = if (isSplit) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Active Panel View
                when (currentInputMode) {
                    InputMode.MAIN_KEYBOARD -> {
                        if (isSplit) {
                            SplitLayout(
                                synthesizer = synthesizer,
                                inputConnection = service.currentInputConnection,
                                hapticEnabled = hapticEnabled,
                                onInputModeChange = { service.setInputMode(it) }
                            )
                        } else {
                            StandardLayout(
                                synthesizer = synthesizer,
                                inputConnection = service.currentInputConnection,
                                hapticEnabled = hapticEnabled,
                                onInputModeChange = { service.setInputMode(it) }
                            )
                        }
                    }

                    InputMode.CLIPBOARD -> {
                        Box(modifier = Modifier.height(240.dp)) {
                            ClipboardView(
                                clipboardWrapper = clipboardWrapper,
                                onClipSelected = { text ->
                                    InputConnectionUtils.commitText(
                                        service.currentInputConnection,
                                        text
                                    )
                                },
                                onBackToKeyboard = { service.setInputMode(InputMode.MAIN_KEYBOARD) },
                                backgroundColor = surfaceColor
                            )
                        }
                    }

                    InputMode.EMOJI -> {
                        Box(modifier = Modifier.height(240.dp)) {
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
            }
        }
    }
}
