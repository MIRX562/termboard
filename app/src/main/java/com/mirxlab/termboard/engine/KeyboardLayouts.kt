package com.mirxlab.termboard.engine

import android.view.KeyEvent
import com.mirxlab.termboard.data.models.ArrowDirection
import com.mirxlab.termboard.data.models.KeyAction
import com.mirxlab.termboard.data.models.KeyDefinition
import com.mirxlab.termboard.data.models.Layer
import com.mirxlab.termboard.data.models.LayerActivationMode
import com.mirxlab.termboard.data.models.ModifierType

object KeyboardLayouts {

    // Default layers
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
            description = "Arrow navigation and cursor controls",
            keyMappings = mapOf(
                "KEY_H" to KeyAction.Arrow(ArrowDirection.LEFT),
                "KEY_J" to KeyAction.Arrow(ArrowDirection.DOWN),
                "KEY_K" to KeyAction.Arrow(ArrowDirection.UP),
                "KEY_L" to KeyAction.Arrow(ArrowDirection.RIGHT),
                "KEY_I" to KeyAction.KeyCode(KeyEvent.KEYCODE_PAGE_UP),
                "KEY_U" to KeyAction.KeyCode(KeyEvent.KEYCODE_PAGE_DOWN),
                "KEY_Y" to KeyAction.KeyCode(KeyEvent.KEYCODE_MOVE_HOME),
                "KEY_O" to KeyAction.KeyCode(KeyEvent.KEYCODE_MOVE_END),
                "KEY_Q" to KeyAction.Escape,
                "KEY_W" to KeyAction.Delete,
                "KEY_E" to KeyAction.Tab
            )
        ),
        "NUM" to Layer(
            id = "NUM",
            name = "Numpad & Math",
            displayName = "NUM",
            description = "Numeric keypad and math operators",
            keyMappings = mapOf(
                "KEY_7" to KeyAction.Text("7"),
                "KEY_8" to KeyAction.Text("8"),
                "KEY_9" to KeyAction.Text("9"),
                "KEY_U" to KeyAction.Text("4"),
                "KEY_I" to KeyAction.Text("5"),
                "KEY_O" to KeyAction.Text("6"),
                "KEY_J" to KeyAction.Text("1"),
                "KEY_K" to KeyAction.Text("2"),
                "KEY_L" to KeyAction.Text("3"),
                "KEY_M" to KeyAction.Text("0"),
                "KEY_P" to KeyAction.Text("+"),
                "KEY_0" to KeyAction.Text("-"),
                "KEY_SLASH" to KeyAction.Text("/"),
                "KEY_DOT" to KeyAction.Text(".")
            )
        ),
        "MEDIA" to Layer(
            id = "MEDIA",
            name = "Media Controls",
            displayName = "MEDIA",
            description = "Volume, playback, and brightness controls",
            keyMappings = mapOf(
                "KEY_1" to KeyAction.KeyCode(KeyEvent.KEYCODE_MEDIA_PREVIOUS),
                "KEY_2" to KeyAction.KeyCode(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE),
                "KEY_3" to KeyAction.KeyCode(KeyEvent.KEYCODE_MEDIA_NEXT),
                "KEY_4" to KeyAction.KeyCode(KeyEvent.KEYCODE_VOLUME_MUTE),
                "KEY_5" to KeyAction.KeyCode(KeyEvent.KEYCODE_VOLUME_DOWN),
                "KEY_6" to KeyAction.KeyCode(KeyEvent.KEYCODE_VOLUME_UP)
            )
        ),
        "SYM" to Layer(
            id = "SYM",
            name = "Symbols & Brackets",
            displayName = "SYM",
            description = "Programming symbols and punctuation",
            keyMappings = mapOf(
                "KEY_Q" to KeyAction.Text("!"),
                "KEY_W" to KeyAction.Text("@"),
                "KEY_E" to KeyAction.Text("#"),
                "KEY_R" to KeyAction.Text("$"),
                "KEY_T" to KeyAction.Text("%"),
                "KEY_Y" to KeyAction.Text("^"),
                "KEY_U" to KeyAction.Text("&"),
                "KEY_I" to KeyAction.Text("*"),
                "KEY_O" to KeyAction.Text("("),
                "KEY_P" to KeyAction.Text(")"),
                "KEY_A" to KeyAction.Text("{"),
                "KEY_S" to KeyAction.Text("}"),
                "KEY_D" to KeyAction.Text("["),
                "KEY_F" to KeyAction.Text("]"),
                "KEY_G" to KeyAction.Text("="),
                "KEY_H" to KeyAction.Text("+"),
                "KEY_J" to KeyAction.Text("-"),
                "KEY_K" to KeyAction.Text("_"),
                "KEY_L" to KeyAction.Text("\\")
            )
        )
    )

    // Sofle Ergonomic Split Layout definitions (Left Half & Right Half)
    val splitLeftRows: List<List<KeyDefinition>> = listOf(
        // Row 0
        listOf(
            KeyDefinition("KEY_ESC", "Esc", KeyAction.Escape, isSpecial = true),
            KeyDefinition("KEY_BACKTICK", "`", KeyAction.Text("`")),
            KeyDefinition("KEY_1", "1", KeyAction.Text("1")),
            KeyDefinition("KEY_2", "2", KeyAction.Text("2")),
            KeyDefinition("KEY_3", "3", KeyAction.Text("3")),
            KeyDefinition("KEY_4", "4", KeyAction.Text("4")),
            KeyDefinition("KEY_5", "5", KeyAction.Text("5"))
        ),
        // Row 1
        listOf(
            KeyDefinition("KEY_TAB", "Tab", KeyAction.Tab, isSpecial = true),
            KeyDefinition("KEY_Q", "Q", KeyAction.Text("q")),
            KeyDefinition("KEY_W", "W", KeyAction.Text("w")),
            KeyDefinition("KEY_E", "E", KeyAction.Text("e")),
            KeyDefinition("KEY_R", "R", KeyAction.Text("r")),
            KeyDefinition("KEY_T", "T", KeyAction.Text("t"))
        ),
        // Row 2
        listOf(
            KeyDefinition("KEY_CAPS", "Caps", KeyAction.ModifierKey(ModifierType.CAPS_LOCK), isSpecial = true),
            KeyDefinition("KEY_A", "A", KeyAction.Text("a")),
            KeyDefinition("KEY_S", "S", KeyAction.Text("s")),
            KeyDefinition("KEY_D", "D", KeyAction.Text("d")),
            KeyDefinition("KEY_F", "F", KeyAction.Text("f")),
            KeyDefinition("KEY_G", "G", KeyAction.Text("g"))
        ),
        // Row 3
        listOf(
            KeyDefinition("KEY_SHIFT_L", "Shift", KeyAction.ModifierKey(ModifierType.SHIFT), isSpecial = true),
            KeyDefinition("KEY_Z", "Z", KeyAction.Text("z")),
            KeyDefinition("KEY_X", "X", KeyAction.Text("x")),
            KeyDefinition("KEY_C", "C", KeyAction.Text("c")),
            KeyDefinition("KEY_V", "V", KeyAction.Text("v")),
            KeyDefinition("KEY_B", "B", KeyAction.Text("b"))
        )
    )

    val splitLeftThumbCluster: List<KeyDefinition> = listOf(
        KeyDefinition("KEY_FN_L", "Fn", KeyAction.LayerAction("NAV", LayerActivationMode.MOMENTARY), isSpecial = true),
        KeyDefinition("KEY_META_L", "Meta", KeyAction.ModifierKey(ModifierType.META), isSpecial = true),
        KeyDefinition("KEY_ALT_L", "Alt", KeyAction.ModifierKey(ModifierType.ALT), isSpecial = true),
        KeyDefinition("KEY_CTRL_L", "Ctrl", KeyAction.ModifierKey(ModifierType.CTRL), isSpecial = true),
        KeyDefinition("KEY_SPACE_L", "Space", KeyAction.Text(" "), widthWeight = 1.5f)
    )

    val splitLeftBottomRow: List<KeyDefinition> = listOf(
        KeyDefinition("KEY_HOME", "Home", KeyAction.KeyCode(KeyEvent.KEYCODE_MOVE_HOME), isSpecial = true),
        KeyDefinition("KEY_COPY", "Copy", KeyAction.KeyCode(KeyEvent.KEYCODE_C, KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON), isSpecial = true),
        KeyDefinition("KEY_PASTE", "Paste", KeyAction.KeyCode(KeyEvent.KEYCODE_V, KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON), isSpecial = true),
        KeyDefinition("KEY_CUT", "Cut", KeyAction.KeyCode(KeyEvent.KEYCODE_X, KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON), isSpecial = true),
        KeyDefinition("KEY_EMPTY_L1", "", KeyAction.None),
        KeyDefinition("KEY_EMPTY_L2", "", KeyAction.None)
    )

    val splitRightRows: List<List<KeyDefinition>> = listOf(
        // Row 0
        listOf(
            KeyDefinition("KEY_6", "6", KeyAction.Text("6")),
            KeyDefinition("KEY_7", "7", KeyAction.Text("7")),
            KeyDefinition("KEY_8", "8", KeyAction.Text("8")),
            KeyDefinition("KEY_9", "9", KeyAction.Text("9")),
            KeyDefinition("KEY_0", "0", KeyAction.Text("0")),
            KeyDefinition("KEY_MINUS", "-", KeyAction.Text("-")),
            KeyDefinition("KEY_EQUAL", "=", KeyAction.Text("=")),
            KeyDefinition("KEY_DEL", "Del", KeyAction.Delete, isSpecial = true)
        ),
        // Row 1
        listOf(
            KeyDefinition("KEY_Y", "Y", KeyAction.Text("y")),
            KeyDefinition("KEY_U", "U", KeyAction.Text("u")),
            KeyDefinition("KEY_I", "I", KeyAction.Text("i")),
            KeyDefinition("KEY_O", "O", KeyAction.Text("o")),
            KeyDefinition("KEY_P", "P", KeyAction.Text("p")),
            KeyDefinition("KEY_LBRACKET", "[", KeyAction.Text("[")),
            KeyDefinition("KEY_RBRACKET", "]", KeyAction.Text("]")),
            KeyDefinition("KEY_BSPACE", "Backspace", KeyAction.Backspace, isSpecial = true)
        ),
        // Row 2
        listOf(
            KeyDefinition("KEY_H", "H", KeyAction.Text("h")),
            KeyDefinition("KEY_J", "J", KeyAction.Text("j")),
            KeyDefinition("KEY_K", "K", KeyAction.Text("k")),
            KeyDefinition("KEY_L", "L", KeyAction.Text("l")),
            KeyDefinition("KEY_SEMICOLON", ";", KeyAction.Text(";")),
            KeyDefinition("KEY_QUOTE", "'", KeyAction.Text("'")),
            KeyDefinition("KEY_ENTER", "Enter", KeyAction.Enter, isSpecial = true)
        ),
        // Row 3
        listOf(
            KeyDefinition("KEY_N", "N", KeyAction.Text("n")),
            KeyDefinition("KEY_M", "M", KeyAction.Text("m")),
            KeyDefinition("KEY_COMMA", ",", KeyAction.Text(",")),
            KeyDefinition("KEY_DOT", ".", KeyAction.Text(".")),
            KeyDefinition("KEY_SLASH", "/", KeyAction.Text("/")),
            KeyDefinition("KEY_SHIFT_R", "Shift", KeyAction.ModifierKey(ModifierType.SHIFT), isSpecial = true)
        )
    )

    val splitRightThumbCluster: List<KeyDefinition> = listOf(
        KeyDefinition("KEY_SPACE_R", "Space", KeyAction.Text(" "), widthWeight = 1.5f),
        KeyDefinition("KEY_CTRL_R", "Ctrl", KeyAction.ModifierKey(ModifierType.CTRL), isSpecial = true),
        KeyDefinition("KEY_ALT_R", "Alt", KeyAction.ModifierKey(ModifierType.ALT), isSpecial = true),
        KeyDefinition("KEY_META_R", "Meta", KeyAction.ModifierKey(ModifierType.META), isSpecial = true),
        KeyDefinition("KEY_FN_R", "Fn", KeyAction.LayerAction("NAV", LayerActivationMode.MOMENTARY), isSpecial = true)
    )

    val splitRightBottomRow: List<KeyDefinition> = listOf(
        KeyDefinition("KEY_END", "End", KeyAction.KeyCode(KeyEvent.KEYCODE_MOVE_END), isSpecial = true),
        KeyDefinition("KEY_PAGE_UP", "PgUp", KeyAction.KeyCode(KeyEvent.KEYCODE_PAGE_UP), isSpecial = true),
        KeyDefinition("KEY_PAGE_DOWN", "PgDn", KeyAction.KeyCode(KeyEvent.KEYCODE_PAGE_DOWN), isSpecial = true),
        KeyDefinition("KEY_EMPTY_R1", "", KeyAction.None),
        KeyDefinition("KEY_EMPTY_R2", "", KeyAction.None),
        KeyDefinition("KEY_EMPTY_R3", "", KeyAction.None)
    )
}
