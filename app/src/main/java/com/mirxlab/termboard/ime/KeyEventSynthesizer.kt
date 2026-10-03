package com.mirxlab.termboard.ime

import android.os.SystemClock
import android.view.KeyEvent
import android.view.inputmethod.InputConnection
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

enum class ShiftState {
    OFF,
    ON,
    CAPS_LOCK
}

class KeyEventSynthesizer {

    private val _shiftState = mutableStateOf(ShiftState.OFF)
    val shiftState: State<ShiftState> get() = _shiftState

    private val _isCtrlActive = mutableStateOf(false)
    val isCtrlActive: State<Boolean> get() = _isCtrlActive

    private val _isAltActive = mutableStateOf(false)
    val isAltActive: State<Boolean> get() = _isAltActive

    private val _isFnActive = mutableStateOf(false)
    val isFnActive: State<Boolean> get() = _isFnActive

    fun toggleShift() {
        _shiftState.value = when (_shiftState.value) {
            ShiftState.OFF -> ShiftState.ON
            ShiftState.ON -> ShiftState.CAPS_LOCK
            ShiftState.CAPS_LOCK -> ShiftState.OFF
        }
    }

    fun toggleCtrl() {
        _isCtrlActive.value = !_isCtrlActive.value
    }

    fun toggleAlt() {
        _isAltActive.value = !_isAltActive.value
    }

    fun toggleFn() {
        _isFnActive.value = !_isFnActive.value
    }

    fun getActiveMetaState(): Int {
        var meta = 0
        if (_isCtrlActive.value) {
            meta = meta or KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON
        }
        if (_isAltActive.value) {
            meta = meta or KeyEvent.META_ALT_ON or KeyEvent.META_ALT_LEFT_ON
        }
        if (_shiftState.value != ShiftState.OFF) {
            meta = meta or KeyEvent.META_SHIFT_ON or KeyEvent.META_SHIFT_LEFT_ON
        }
        return meta
    }

    fun dispatchChar(ic: InputConnection?, char: Char) {
        if (ic == null) return

        val meta = getActiveMetaState()

        // If Ctrl or Alt is active, synthesize a shortcut key event
        if (_isCtrlActive.value || _isAltActive.value) {
            val keyCode = charToKeyCode(char)
            if (keyCode != KeyEvent.KEYCODE_UNKNOWN) {
                dispatchCombination(ic, meta, keyCode)
                // Clear transient Ctrl/Alt modifiers after combination
                _isCtrlActive.value = false
                _isAltActive.value = false
                return
            }
        }

        val textToCommit = when (_shiftState.value) {
            ShiftState.OFF -> char.lowercase()
            ShiftState.ON -> {
                _shiftState.value = ShiftState.OFF
                char.uppercase()
            }
            ShiftState.CAPS_LOCK -> char.uppercase()
        }

        InputConnectionUtils.commitText(ic, textToCommit)
    }

    fun dispatchKey(ic: InputConnection?, keyCode: Int) {
        if (ic == null) return
        val meta = getActiveMetaState()

        val eventTime = SystemClock.uptimeMillis()
        val downEvent = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0, meta)
        val upEvent = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, keyCode, 0, meta)

        ic.sendKeyEvent(downEvent)
        ic.sendKeyEvent(upEvent)

        // Consume single-use shift if active
        if (_shiftState.value == ShiftState.ON) {
            _shiftState.value = ShiftState.OFF
        }
    }

    fun dispatchCombination(ic: InputConnection?, modifierMetaState: Int, keyCode: Int) {
        if (ic == null) return
        val eventTime = SystemClock.uptimeMillis()
        val downEvent = KeyEvent(
            eventTime, eventTime,
            KeyEvent.ACTION_DOWN, keyCode, 0, modifierMetaState
        )
        val upEvent = KeyEvent(
            eventTime, eventTime,
            KeyEvent.ACTION_UP, keyCode, 0, modifierMetaState
        )
        ic.sendKeyEvent(downEvent)
        ic.sendKeyEvent(upEvent)
    }

    private fun charToKeyCode(c: Char): Int {
        val uppercase = c.uppercaseChar()
        return when (uppercase) {
            in 'A'..'Z' -> KeyEvent.KEYCODE_A + (uppercase - 'A')
            in '0'..'9' -> KeyEvent.KEYCODE_0 + (uppercase - '0')
            ' ' -> KeyEvent.KEYCODE_SPACE
            '\n' -> KeyEvent.KEYCODE_ENTER
            '\t' -> KeyEvent.KEYCODE_TAB
            else -> KeyEvent.KEYCODE_UNKNOWN
        }
    }
}
