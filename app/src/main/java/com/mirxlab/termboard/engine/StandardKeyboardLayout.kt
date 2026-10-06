package com.mirxlab.termboard.engine

import android.view.KeyEvent
import com.mirxlab.termboard.data.models.ArrowDirection
import com.mirxlab.termboard.data.models.KeyAction
import com.mirxlab.termboard.data.models.KeyDefinition
import com.mirxlab.termboard.data.models.Layer
import com.mirxlab.termboard.data.models.LayerActivationMode
import com.mirxlab.termboard.data.models.ModifierType

object StandardKeyboardLayout {
    val rows: List<List<KeyDefinition>> = listOf(
        listOf(
            KeyDefinition("KEY_ESC", "Esc", KeyAction.Escape, isSpecial = true),
            KeyDefinition("KEY_F1", "F1", KeyAction.KeyCode(KeyEvent.KEYCODE_F1), isSpecial = true),
            KeyDefinition("KEY_F2", "F2", KeyAction.KeyCode(KeyEvent.KEYCODE_F2), isSpecial = true),
            KeyDefinition("KEY_F3", "F3", KeyAction.KeyCode(KeyEvent.KEYCODE_F3), isSpecial = true),
            KeyDefinition("KEY_F4", "F4", KeyAction.KeyCode(KeyEvent.KEYCODE_F4), isSpecial = true),
            KeyDefinition("KEY_F5", "F5", KeyAction.KeyCode(KeyEvent.KEYCODE_F5), isSpecial = true),
            KeyDefinition("KEY_F6", "F6", KeyAction.KeyCode(KeyEvent.KEYCODE_F6), isSpecial = true),
            KeyDefinition("KEY_F7", "F7", KeyAction.KeyCode(KeyEvent.KEYCODE_F7), isSpecial = true),
            KeyDefinition("KEY_F8", "F8", KeyAction.KeyCode(KeyEvent.KEYCODE_F8), isSpecial = true),
            KeyDefinition("KEY_F9", "F9", KeyAction.KeyCode(KeyEvent.KEYCODE_F9), isSpecial = true),
            KeyDefinition("KEY_F10", "F10", KeyAction.KeyCode(KeyEvent.KEYCODE_F10), isSpecial = true),
            KeyDefinition("KEY_F11", "F11", KeyAction.KeyCode(KeyEvent.KEYCODE_F11), isSpecial = true),
            KeyDefinition("KEY_F12", "F12", KeyAction.KeyCode(KeyEvent.KEYCODE_F12), isSpecial = true),
            KeyDefinition("KEY_DEL", "Del", KeyAction.Delete, isSpecial = true)
        ),
        listOf(
            KeyDefinition("KEY_BACKTICK", "`", KeyAction.Text("`")),
            KeyDefinition("KEY_1", "1", KeyAction.Text("1")),
            KeyDefinition("KEY_2", "2", KeyAction.Text("2")),
            KeyDefinition("KEY_3", "3", KeyAction.Text("3")),
            KeyDefinition("KEY_4", "4", KeyAction.Text("4")),
            KeyDefinition("KEY_5", "5", KeyAction.Text("5")),
            KeyDefinition("KEY_6", "6", KeyAction.Text("6")),
            KeyDefinition("KEY_7", "7", KeyAction.Text("7")),
            KeyDefinition("KEY_8", "8", KeyAction.Text("8")),
            KeyDefinition("KEY_9", "9", KeyAction.Text("9")),
            KeyDefinition("KEY_0", "0", KeyAction.Text("0")),
            KeyDefinition("KEY_MINUS", "-", KeyAction.Text("-")),
            KeyDefinition("KEY_EQUAL", "=", KeyAction.Text("=")),
            KeyDefinition("KEY_BSPACE", "Backspace", KeyAction.Backspace, widthWeight = 1.5f, isSpecial = true)
        ),
        listOf(
            KeyDefinition("KEY_TAB", "Tab", KeyAction.Tab, widthWeight = 1.3f, isSpecial = true),
            letter("Q"), letter("W"), letter("E"), letter("R"), letter("T"), letter("Y"),
            letter("U"), letter("I"), letter("O"), letter("P"),
            KeyDefinition("KEY_LBRACKET", "[", KeyAction.Text("[")),
            KeyDefinition("KEY_RBRACKET", "]", KeyAction.Text("]"))
        ),
        listOf(
            KeyDefinition("KEY_CAPS", "Caps", KeyAction.ModifierKey(ModifierType.CAPS_LOCK), widthWeight = 1.5f, isSpecial = true),
            letter("A"), letter("S"), letter("D"), letter("F"), letter("G"), letter("H"),
            letter("J"), letter("K"), letter("L"),
            KeyDefinition("KEY_SEMICOLON", ";", KeyAction.Text(";")),
            KeyDefinition("KEY_QUOTE", "'", KeyAction.Text("'")),
            KeyDefinition("KEY_ENTER", "Enter", KeyAction.Enter, widthWeight = 1.8f, isSpecial = true)
        ),
        listOf(
            KeyDefinition("KEY_SHIFT_L", "Shift", KeyAction.ModifierKey(ModifierType.SHIFT), widthWeight = 1.8f, isSpecial = true),
            letter("Z"), letter("X"), letter("C"), letter("V"), letter("B"), letter("N"), letter("M"),
            KeyDefinition("KEY_COMMA", ",", KeyAction.Text(",")),
            KeyDefinition("KEY_DOT", ".", KeyAction.Text(".")),
            KeyDefinition("KEY_SLASH", "/", KeyAction.Text("/")),
            KeyDefinition("KEY_SHIFT_R", "Shift", KeyAction.ModifierKey(ModifierType.SHIFT), widthWeight = 1.8f, isSpecial = true)
        ),
        listOf(
            KeyDefinition("KEY_FN_L", "Fn", KeyAction.LayerAction("NAV", LayerActivationMode.MOMENTARY), widthWeight = 1.2f, isSpecial = true),
            KeyDefinition("KEY_META", "Meta", KeyAction.ModifierKey(ModifierType.META), widthWeight = 1.2f, isSpecial = true),
            KeyDefinition("KEY_ALT_L", "Alt", KeyAction.ModifierKey(ModifierType.ALT), widthWeight = 1.2f, isSpecial = true),
            KeyDefinition("KEY_CTRL_L", "Ctrl", KeyAction.ModifierKey(ModifierType.CTRL), widthWeight = 1.2f, isSpecial = true),
            KeyDefinition("KEY_SPACE", "Space", KeyAction.Text(" "), widthWeight = 4f),
            KeyDefinition("KEY_CTRL_R", "Ctrl", KeyAction.ModifierKey(ModifierType.CTRL), widthWeight = 1.2f, isSpecial = true),
            KeyDefinition("KEY_ALT_R", "Alt", KeyAction.ModifierKey(ModifierType.ALT), widthWeight = 1.2f, isSpecial = true),
            KeyDefinition("KEY_META_R", "Meta", KeyAction.ModifierKey(ModifierType.META), widthWeight = 1.2f, isSpecial = true),
            KeyDefinition("KEY_FN_R", "Fn", KeyAction.LayerAction("NAV", LayerActivationMode.MOMENTARY), widthWeight = 1.2f, isSpecial = true)
        )
    )

    val compactRows: List<List<KeyDefinition>> = listOf(
        listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P").map(::letter),
        listOf("A", "S", "D", "F", "G", "H", "J", "K", "L").map(::letter) +
            KeyDefinition("KEY_ENTER", "Enter", KeyAction.Enter, widthWeight = 1.5f, isSpecial = true),
        listOf(
            KeyDefinition("KEY_SHIFT_L", "Shift", KeyAction.ModifierKey(ModifierType.SHIFT), widthWeight = 1.5f, isSpecial = true),
            *listOf("Z", "X", "C", "V", "B", "N", "M").map(::letter).toTypedArray()
        ),
        listOf(
            KeyDefinition("KEY_FN_L", "Fn", KeyAction.LayerAction("NAV", LayerActivationMode.MOMENTARY), widthWeight = 1.1f, isSpecial = true),
            KeyDefinition("KEY_META", "Meta", KeyAction.ModifierKey(ModifierType.META), widthWeight = 1.1f, isSpecial = true),
            KeyDefinition("KEY_ALT_L", "Alt", KeyAction.ModifierKey(ModifierType.ALT), widthWeight = 1.1f, isSpecial = true),
            KeyDefinition("KEY_CTRL_L", "Ctrl", KeyAction.ModifierKey(ModifierType.CTRL), widthWeight = 1.1f, isSpecial = true),
            KeyDefinition("KEY_SPACE", "Space", KeyAction.Text(" "), widthWeight = 3.8f),
            KeyDefinition("KEY_PASTE", "Paste", KeyAction.KeyCode(KeyEvent.KEYCODE_V, KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON), widthWeight = 1.1f, isSpecial = true),
            KeyDefinition("KEY_CUT", "Cut", KeyAction.KeyCode(KeyEvent.KEYCODE_X, KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON), widthWeight = 1.1f, isSpecial = true),
            KeyDefinition("KEY_COPY", "Copy", KeyAction.KeyCode(KeyEvent.KEYCODE_C, KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON), widthWeight = 1.1f, isSpecial = true),
            KeyDefinition("KEY_BSPACE", "Backspace", KeyAction.Backspace, widthWeight = 1.1f, isSpecial = true)
        )
    )

    val defaultLayers: Map<String, Layer> = mapOf(
        "BASE" to Layer(
            id = "BASE",
            name = "Base QWERTY",
            displayName = "BASE",
            description = "Standard QWERTY primary typing layer"
        ),
        "NAV" to Layer(
            id = "NAV",
            name = "Navigation",
            displayName = "NAV",
            description = "Numbers, function keys, symbols, and navigation",
            keyMappings = buildMap {
                listOf('Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P')
                    .zip(listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"))
                    .forEach { (letter, digit) -> put("KEY_$letter", KeyAction.Text(digit)) }
                ('A'..'G').zip(1..7).forEach { (letter, number) ->
                    put("KEY_$letter", KeyAction.KeyCode(KeyEvent.KEYCODE_F1 + number - 1))
                }
                put("KEY_SHIFT_L", KeyAction.KeyCode(KeyEvent.KEYCODE_F8))
                put("KEY_CTRL_L", KeyAction.KeyCode(KeyEvent.KEYCODE_F9))
                put("KEY_ALT_L", KeyAction.KeyCode(KeyEvent.KEYCODE_F10))
                put("KEY_META", KeyAction.KeyCode(KeyEvent.KEYCODE_F11))
                put("KEY_FN_L", KeyAction.KeyCode(KeyEvent.KEYCODE_F12))
                put("KEY_H", KeyAction.Arrow(ArrowDirection.LEFT))
                put("KEY_J", KeyAction.Arrow(ArrowDirection.DOWN))
                put("KEY_K", KeyAction.Arrow(ArrowDirection.UP))
                put("KEY_L", KeyAction.Arrow(ArrowDirection.RIGHT))
                put("KEY_I", KeyAction.KeyCode(KeyEvent.KEYCODE_PAGE_UP))
                put("KEY_U", KeyAction.KeyCode(KeyEvent.KEYCODE_PAGE_DOWN))
                put("KEY_O", KeyAction.KeyCode(KeyEvent.KEYCODE_MOVE_HOME))
                put("KEY_P", KeyAction.KeyCode(KeyEvent.KEYCODE_MOVE_END))
                put("KEY_Z", KeyAction.Text("~"))
                put("KEY_X", KeyAction.Text("`"))
                put("KEY_C", KeyAction.Text("\\"))
                put("KEY_V", KeyAction.Text("|"))
                put("KEY_B", KeyAction.Text("="))
                put("KEY_N", KeyAction.Text("+"))
                put("KEY_M", KeyAction.Text("_"))
            }
        )
    )

    private fun letter(label: String) = KeyDefinition(
        id = "KEY_$label",
        baseLabel = label,
        baseAction = KeyAction.Text(label.lowercase())
    )
}
