package com.mirxlab.termboard.ui.layouts

import android.view.KeyEvent
import android.view.inputmethod.InputConnection
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.KeyboardCapslock
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mirxlab.termboard.ime.InputMode
import com.mirxlab.termboard.ime.KeyEventSynthesizer
import com.mirxlab.termboard.ime.ShiftState
import com.mirxlab.termboard.ui.components.KeyButton

enum class SymbolMode {
    ALPHA,
    NUMERIC,
    SYMBOLS
}

@Composable
fun StandardLayout(
    synthesizer: KeyEventSynthesizer,
    inputConnection: InputConnection?,
    hapticEnabled: Boolean,
    onInputModeChange: (InputMode) -> Unit
) {
    var symbolMode by remember { mutableStateOf(SymbolMode.ALPHA) }

    val shiftState by synthesizer.shiftState
    val isCtrlActive by synthesizer.isCtrlActive
    val isAltActive by synthesizer.isAltActive
    val isFnActive by synthesizer.isFnActive

    val isShifted = shiftState != ShiftState.OFF

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 2.dp)
    ) {
        when (symbolMode) {
            SymbolMode.ALPHA -> {
                // Row 1
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p").forEach { char ->
                        val displayChar = if (isShifted) char.uppercase() else char
                        KeyButton(
                            label = displayChar,
                            hapticEnabled = hapticEnabled,
                            onClick = { synthesizer.dispatchChar(inputConnection, char.first()) }
                        )
                    }
                }

                // Row 2
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("a", "s", "d", "f", "g", "h", "j", "k", "l").forEach { char ->
                        val displayChar = if (isShifted) char.uppercase() else char
                        KeyButton(
                            label = displayChar,
                            hapticEnabled = hapticEnabled,
                            onClick = { synthesizer.dispatchChar(inputConnection, char.first()) }
                        )
                    }
                }

                // Row 3: Shift, z x c v b n m, Backspace
                Row(modifier = Modifier.fillMaxWidth()) {
                    KeyButton(
                        icon = Icons.Default.KeyboardCapslock,
                        weight = 1.5f,
                        isActivated = isShifted,
                        isSpecialKey = true,
                        hapticEnabled = hapticEnabled,
                        onClick = { synthesizer.toggleShift() }
                    )

                    listOf("z", "x", "c", "v", "b", "n", "m").forEach { char ->
                        val displayChar = if (isShifted) char.uppercase() else char
                        KeyButton(
                            label = displayChar,
                            hapticEnabled = hapticEnabled,
                            onClick = { synthesizer.dispatchChar(inputConnection, char.first()) }
                        )
                    }

                    KeyButton(
                        icon = Icons.AutoMirrored.Filled.Backspace,
                        weight = 1.5f,
                        isSpecialKey = true,
                        hapticEnabled = hapticEnabled,
                        onClick = { synthesizer.dispatchKey(inputConnection, KeyEvent.KEYCODE_DEL) },
                        onRepeatClick = { synthesizer.dispatchKey(inputConnection, KeyEvent.KEYCODE_DEL) }
                    )
                }
            }

            SymbolMode.NUMERIC -> {
                // Numeric Row 1
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0").forEach { char ->
                        KeyButton(
                            label = char,
                            hapticEnabled = hapticEnabled,
                            onClick = { synthesizer.dispatchChar(inputConnection, char.first()) }
                        )
                    }
                }

                // Numeric Row 2
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("@", "#", "$", "_", "&", "-", "+", "(", ")", "/").forEach { char ->
                        KeyButton(
                            label = char,
                            hapticEnabled = hapticEnabled,
                            onClick = { synthesizer.dispatchChar(inputConnection, char.first()) }
                        )
                    }
                }

                // Numeric Row 3
                Row(modifier = Modifier.fillMaxWidth()) {
                    KeyButton(
                        label = "=\\<",
                        weight = 1.5f,
                        isSpecialKey = true,
                        hapticEnabled = hapticEnabled,
                        onClick = { symbolMode = SymbolMode.SYMBOLS }
                    )

                    listOf("*", "\"", "'", ":", ";", "!", "?").forEach { char ->
                        KeyButton(
                            label = char,
                            hapticEnabled = hapticEnabled,
                            onClick = { synthesizer.dispatchChar(inputConnection, char.first()) }
                        )
                    }

                    KeyButton(
                        icon = Icons.AutoMirrored.Filled.Backspace,
                        weight = 1.5f,
                        isSpecialKey = true,
                        hapticEnabled = hapticEnabled,
                        onClick = { synthesizer.dispatchKey(inputConnection, KeyEvent.KEYCODE_DEL) },
                        onRepeatClick = { synthesizer.dispatchKey(inputConnection, KeyEvent.KEYCODE_DEL) }
                    )
                }
            }

            SymbolMode.SYMBOLS -> {
                // Symbols Row 1
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "∆").forEach { char ->
                        KeyButton(
                            label = char,
                            hapticEnabled = hapticEnabled,
                            onClick = { synthesizer.dispatchChar(inputConnection, char.first()) }
                        )
                    }
                }

                // Symbols Row 2
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("£", "¥", "€", "¢", "^", "°", "=", "{", "}", "\\").forEach { char ->
                        KeyButton(
                            label = char,
                            hapticEnabled = hapticEnabled,
                            onClick = { synthesizer.dispatchChar(inputConnection, char.first()) }
                        )
                    }
                }

                // Symbols Row 3
                Row(modifier = Modifier.fillMaxWidth()) {
                    KeyButton(
                        label = "?123",
                        weight = 1.5f,
                        isSpecialKey = true,
                        hapticEnabled = hapticEnabled,
                        onClick = { symbolMode = SymbolMode.NUMERIC }
                    )

                    listOf("%", "©", "®", "™", "✓", "[", "]").forEach { char ->
                        KeyButton(
                            label = char,
                            hapticEnabled = hapticEnabled,
                            onClick = { synthesizer.dispatchChar(inputConnection, char.first()) }
                        )
                    }

                    KeyButton(
                        icon = Icons.AutoMirrored.Filled.Backspace,
                        weight = 1.5f,
                        isSpecialKey = true,
                        hapticEnabled = hapticEnabled,
                        onClick = { synthesizer.dispatchKey(inputConnection, KeyEvent.KEYCODE_DEL) },
                        onRepeatClick = { synthesizer.dispatchKey(inputConnection, KeyEvent.KEYCODE_DEL) }
                    )
                }
            }
        }

        // Row 4: Control Bar (Common to all modes)
        Row(modifier = Modifier.fillMaxWidth()) {
            KeyButton(
                label = if (symbolMode == SymbolMode.ALPHA) "?123" else "ABC",
                weight = 1.3f,
                isSpecialKey = true,
                hapticEnabled = hapticEnabled,
                onClick = {
                    symbolMode = if (symbolMode == SymbolMode.ALPHA) SymbolMode.NUMERIC else SymbolMode.ALPHA
                }
            )

            KeyButton(
                label = "Ctrl",
                weight = 1f,
                isActivated = isCtrlActive,
                isSpecialKey = true,
                hapticEnabled = hapticEnabled,
                onClick = { synthesizer.toggleCtrl() }
            )

            KeyButton(
                label = "Alt",
                weight = 1f,
                isActivated = isAltActive,
                isSpecialKey = true,
                hapticEnabled = hapticEnabled,
                onClick = { synthesizer.toggleAlt() }
            )

            KeyButton(
                icon = Icons.Default.EmojiEmotions,
                weight = 1f,
                isSpecialKey = true,
                hapticEnabled = hapticEnabled,
                onClick = { onInputModeChange(InputMode.EMOJI) }
            )

            KeyButton(
                icon = Icons.Default.SpaceBar,
                weight = 3.5f,
                hapticEnabled = hapticEnabled,
                onClick = { synthesizer.dispatchChar(inputConnection, ' ') }
            )

            KeyButton(
                label = "Fn",
                weight = 1f,
                isActivated = isFnActive,
                isSpecialKey = true,
                hapticEnabled = hapticEnabled,
                onClick = { synthesizer.toggleFn() }
            )

            KeyButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                weight = 1f,
                isSpecialKey = true,
                hapticEnabled = hapticEnabled,
                onClick = { synthesizer.dispatchKey(inputConnection, KeyEvent.KEYCODE_DPAD_LEFT) }
            )

            KeyButton(
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                weight = 1f,
                isSpecialKey = true,
                hapticEnabled = hapticEnabled,
                onClick = { synthesizer.dispatchKey(inputConnection, KeyEvent.KEYCODE_DPAD_RIGHT) }
            )

            KeyButton(
                icon = Icons.AutoMirrored.Filled.KeyboardReturn,
                weight = 1.5f,
                isSpecialKey = true,
                hapticEnabled = hapticEnabled,
                onClick = { synthesizer.dispatchKey(inputConnection, KeyEvent.KEYCODE_ENTER) }
            )
        }
    }
}
