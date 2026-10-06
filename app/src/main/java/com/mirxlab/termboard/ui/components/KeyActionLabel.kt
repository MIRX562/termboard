package com.mirxlab.termboard.ui.components

import android.view.KeyEvent
import com.mirxlab.termboard.data.models.ArrowDirection
import com.mirxlab.termboard.data.models.KeyAction
import com.mirxlab.termboard.data.models.ModifierType

fun KeyAction.toDisplayLabel(): String? = when (this) {
    is KeyAction.Text -> text
    is KeyAction.Arrow -> when (direction) {
        ArrowDirection.LEFT -> "←"
        ArrowDirection.DOWN -> "↓"
        ArrowDirection.UP -> "↑"
        ArrowDirection.RIGHT -> "→"
    }
    is KeyAction.KeyCode -> when (keyCode) {
        KeyEvent.KEYCODE_MOVE_HOME -> "Home"
        KeyEvent.KEYCODE_MOVE_END -> "End"
        KeyEvent.KEYCODE_PAGE_UP -> "PgUp"
        KeyEvent.KEYCODE_PAGE_DOWN -> "PgDn"
        else -> KeyEvent.keyCodeToString(keyCode).removePrefix("KEYCODE_").replace('_', ' ')
    }
    is KeyAction.ModifierKey -> when (modifier) {
        ModifierType.SHIFT -> "Shift"
        ModifierType.CTRL -> "Ctrl"
        ModifierType.ALT -> "Alt"
        ModifierType.META -> "Meta"
        ModifierType.FN -> "Fn"
        ModifierType.CAPS_LOCK -> "Caps"
    }
    is KeyAction.LayerAction -> layerId
    is KeyAction.ComboAction -> "Combo"
    KeyAction.Backspace -> "⌫"
    KeyAction.Delete -> "Del"
    KeyAction.Enter -> "↵"
    KeyAction.Tab -> "Tab"
    KeyAction.Escape -> "Esc"
    else -> null
}
