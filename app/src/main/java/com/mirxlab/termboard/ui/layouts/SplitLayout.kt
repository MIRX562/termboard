package com.mirxlab.termboard.ui.layouts

import android.view.KeyEvent
import android.view.inputmethod.InputConnection
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardCapslock
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import com.mirxlab.termboard.ui.components.KeyButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mirxlab.termboard.ime.InputMode
import com.mirxlab.termboard.ime.KeyEventSynthesizer
import com.mirxlab.termboard.ime.ShiftState

@Composable
fun SplitLayout(
    synthesizer: KeyEventSynthesizer,
    inputConnection: InputConnection?,
    hapticEnabled: Boolean,
    onInputModeChange: (InputMode) -> Unit
) {
    val shiftState by synthesizer.shiftState
    val isCtrlActive by synthesizer.isCtrlActive
    val isAltActive by synthesizer.isAltActive
    val isShifted = shiftState != ShiftState.OFF

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        // Row 1
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left Row 1: q w e r t
            Row(modifier = Modifier.weight(1f)) {
                listOf("q", "w", "e", "r", "t").forEach { char ->
                    val displayChar = if (isShifted) char.uppercase() else char
                    KeyButton(
                        label = displayChar,
                        hapticEnabled = hapticEnabled,
                        onClick = { synthesizer.dispatchChar(inputConnection, char.first()) }
                    )
                }
            }

            // Center Gap Row 1: Esc key
            KeyButton(
                label = "Esc",
                modifier = Modifier.width(56.dp),
                isSpecialKey = true,
                hapticEnabled = hapticEnabled,
                onClick = { synthesizer.dispatchKey(inputConnection, KeyEvent.KEYCODE_ESCAPE) }
            )

            // Right Row 1: y u i o p
            Row(modifier = Modifier.weight(1f)) {
                listOf("y", "u", "i", "o", "p").forEach { char ->
                    val displayChar = if (isShifted) char.uppercase() else char
                    KeyButton(
                        label = displayChar,
                        hapticEnabled = hapticEnabled,
                        onClick = { synthesizer.dispatchChar(inputConnection, char.first()) }
                    )
                }
            }
        }

        // Row 2
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left Row 2: a s d f g
            Row(modifier = Modifier.weight(1f)) {
                listOf("a", "s", "d", "f", "g").forEach { char ->
                    val displayChar = if (isShifted) char.uppercase() else char
                    KeyButton(
                        label = displayChar,
                        hapticEnabled = hapticEnabled,
                        onClick = { synthesizer.dispatchChar(inputConnection, char.first()) }
                    )
                }
            }

            // Center Gap Row 2: Up Arrow
            KeyButton(
                icon = Icons.Default.KeyboardArrowUp,
                modifier = Modifier.width(56.dp),
                isSpecialKey = true,
                hapticEnabled = hapticEnabled,
                onClick = { synthesizer.dispatchKey(inputConnection, KeyEvent.KEYCODE_DPAD_UP) }
            )

            // Right Row 2: h j k l ;
            Row(modifier = Modifier.weight(1f)) {
                listOf("h", "j", "k", "l", ";").forEach { char ->
                    val displayChar = if (isShifted) char.uppercase() else char
                    KeyButton(
                        label = displayChar,
                        hapticEnabled = hapticEnabled,
                        onClick = { synthesizer.dispatchChar(inputConnection, char.first()) }
                    )
                }
            }
        }

        // Row 3
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left Row 3: Shift, z x c v
            Row(modifier = Modifier.weight(1f)) {
                KeyButton(
                    icon = Icons.Default.KeyboardCapslock,
                    weight = 1.3f,
                    isActivated = isShifted,
                    isSpecialKey = true,
                    hapticEnabled = hapticEnabled,
                    onClick = { synthesizer.toggleShift() }
                )

                listOf("z", "x", "c", "v").forEach { char ->
                    val displayChar = if (isShifted) char.uppercase() else char
                    KeyButton(
                        label = displayChar,
                        hapticEnabled = hapticEnabled,
                        onClick = { synthesizer.dispatchChar(inputConnection, char.first()) }
                    )
                }
            }

            // Center Gap Row 3: Down Arrow
            KeyButton(
                icon = Icons.Default.KeyboardArrowDown,
                modifier = Modifier.width(56.dp),
                isSpecialKey = true,
                hapticEnabled = hapticEnabled,
                onClick = { synthesizer.dispatchKey(inputConnection, KeyEvent.KEYCODE_DPAD_DOWN) }
            )

            // Right Row 3: b n m , Backspace
            Row(modifier = Modifier.weight(1f)) {
                listOf("b", "n", "m").forEach { char ->
                    val displayChar = if (isShifted) char.uppercase() else char
                    KeyButton(
                        label = displayChar,
                        hapticEnabled = hapticEnabled,
                        onClick = { synthesizer.dispatchChar(inputConnection, char.first()) }
                    )
                }

                KeyButton(
                    icon = Icons.AutoMirrored.Filled.Backspace,
                    weight = 1.3f,
                    isSpecialKey = true,
                    hapticEnabled = hapticEnabled,
                    onClick = { synthesizer.dispatchKey(inputConnection, KeyEvent.KEYCODE_DEL) },
                    onRepeatClick = { synthesizer.dispatchKey(inputConnection, KeyEvent.KEYCODE_DEL) }
                )
            }
        }

        // Row 4
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left Bottom Row: Ctrl, Alt, Space
            Row(modifier = Modifier.weight(1f)) {
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
                    icon = Icons.Default.SpaceBar,
                    weight = 2.5f,
                    hapticEnabled = hapticEnabled,
                    onClick = { synthesizer.dispatchChar(inputConnection, ' ') }
                )
            }

            // Center Gap Row 4: Clipboard drawer shortcut
            KeyButton(
                icon = Icons.AutoMirrored.Filled.Assignment,
                modifier = Modifier.width(56.dp),
                isSpecialKey = true,
                hapticEnabled = hapticEnabled,
                onClick = { onInputModeChange(InputMode.CLIPBOARD) }
            )

            // Right Bottom Row: Emoji, Arrows, Enter
            Row(modifier = Modifier.weight(1f)) {
                KeyButton(
                    icon = Icons.Default.EmojiEmotions,
                    weight = 1f,
                    isSpecialKey = true,
                    hapticEnabled = hapticEnabled,
                    onClick = { onInputModeChange(InputMode.EMOJI) }
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
                    weight = 1.3f,
                    isSpecialKey = true,
                    hapticEnabled = hapticEnabled,
                    onClick = { synthesizer.dispatchKey(inputConnection, KeyEvent.KEYCODE_ENTER) }
                )
            }
        }
    }
}
