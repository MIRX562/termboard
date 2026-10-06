package com.mirxlab.termboard.engine

import android.view.KeyEvent
import com.mirxlab.termboard.data.models.KeyAction
import com.mirxlab.termboard.data.models.LayerActivationMode
import com.mirxlab.termboard.data.models.ModifierType

/** Visual and action definitions for every physical key used by either layout. */
data class KeyActionLabel(
    val label: String,
    val action: KeyAction,
    val icon: KeyLegendIcon? = null
)

enum class KeyLegendIcon {
    TAB,
    SHIFT,
    META,
    SPACE,
    ENTER,
    BACKSPACE
}

enum class KeycapLayout {
    DUAL_LEGEND,
    SINGLE_LEGEND
}

data class KeyboardKeySpec(
    val id: String,
    val primary: KeyActionLabel,
    val shifted: KeyActionLabel,
    val secondary: KeyActionLabel?,
    val layout: KeycapLayout
)

object KeyboardKeyCatalog {
    private fun action(
        label: String,
        keyAction: KeyAction,
        icon: KeyLegendIcon? = null
    ) = KeyActionLabel(label, keyAction, icon)

    private fun textKey(
        id: String,
        label: String,
        text: String = label,
        shiftedLabel: String = label.uppercase(),
        shiftedText: String = shiftedLabel,
        secondaryLabel: String? = null,
        secondaryText: String? = secondaryLabel
    ) = KeyboardKeySpec(
        id = id,
        primary = action(label, KeyAction.Text(text)),
        shifted = action(shiftedLabel, KeyAction.Text(shiftedText)),
        secondary = secondaryLabel?.let { action(it, KeyAction.Text(secondaryText ?: it)) },
        layout = if (secondaryLabel == null) KeycapLayout.SINGLE_LEGEND else KeycapLayout.DUAL_LEGEND
    )

    private fun commandKey(
        id: String,
        label: String,
        action: KeyAction,
        icon: KeyLegendIcon? = null
    ) = KeyboardKeySpec(
        id = id,
        primary = action(label, action, icon),
        shifted = action(label, action, icon),
        secondary = null,
        layout = KeycapLayout.SINGLE_LEGEND
    )

    val keys: Map<String, KeyboardKeySpec> = buildMap {
        val letterSecondaries = mapOf(
            'q' to "1", 'w' to "2", 'e' to "3", 'r' to "4", 't' to "5",
            'y' to "6", 'u' to "7", 'i' to "8", 'o' to "9", 'p' to "0",
            'a' to "@", 's' to "#", 'd' to "$", 'f' to "%", 'g' to "^",
            'h' to "&", 'j' to "*", 'k' to "(", 'l' to ")",
            'z' to "~", 'x' to "`", 'c' to "\\", 'v' to "|", 'b' to "=",
            'n' to "+", 'm' to "_"
        )
        for (letter in 'a'..'z') {
            val upper = letter.uppercaseChar()
            val id = "KEY_$upper"
            put(
                id,
                textKey(
                    id = id,
                    label = letter.toString(),
                    text = letter.toString(),
                    shiftedLabel = upper.toString(),
                    shiftedText = upper.toString(),
                    secondaryLabel = letterSecondaries[letter]
                )
            )
        }

        val digitSymbols = mapOf(
            "1" to "!", "2" to "@", "3" to "#", "4" to "$", "5" to "%",
            "6" to "^", "7" to "&", "8" to "*", "9" to "(", "0" to ")"
        )
        digitSymbols.forEach { (digit, symbol) ->
            val id = "KEY_$digit"
            put(id, textKey(id, digit, shiftedLabel = symbol, shiftedText = symbol, secondaryLabel = symbol))
        }

        put("KEY_BACKTICK", textKey("KEY_BACKTICK", "`", shiftedLabel = "~", shiftedText = "~"))
        put("KEY_MINUS", textKey("KEY_MINUS", "-", shiftedLabel = "_", shiftedText = "_", secondaryLabel = "_"))
        put("KEY_EQUAL", textKey("KEY_EQUAL", "=", shiftedLabel = "+", shiftedText = "+", secondaryLabel = "+"))
        put("KEY_LBRACKET", textKey("KEY_LBRACKET", "[", shiftedLabel = "{", shiftedText = "{"))
        put("KEY_RBRACKET", textKey("KEY_RBRACKET", "]", shiftedLabel = "}", shiftedText = "}"))
        put("KEY_SEMICOLON", textKey("KEY_SEMICOLON", ";", shiftedLabel = ":", shiftedText = ":"))
        put("KEY_QUOTE", textKey("KEY_QUOTE", "'", shiftedLabel = "\"", shiftedText = "\""))
        put("KEY_COMMA", textKey("KEY_COMMA", ",", shiftedLabel = "<", shiftedText = "<"))
        put("KEY_DOT", textKey("KEY_DOT", ".", shiftedLabel = ">", shiftedText = ">"))
        put("KEY_SLASH", textKey("KEY_SLASH", "/", shiftedLabel = "?", shiftedText = "?"))

        put("KEY_ESC", commandKey("KEY_ESC", "Esc", KeyAction.Escape))
        put("KEY_TAB", commandKey("KEY_TAB", "Tab", KeyAction.Tab, KeyLegendIcon.TAB))
        put("KEY_BSPACE", commandKey("KEY_BSPACE", "Backspace", KeyAction.Backspace, KeyLegendIcon.BACKSPACE))
        put("KEY_DEL", commandKey("KEY_DEL", "Del", KeyAction.Delete))
        put("KEY_ENTER", commandKey("KEY_ENTER", "Enter", KeyAction.Enter, KeyLegendIcon.ENTER))
        put("KEY_CAPS", commandKey("KEY_CAPS", "Caps", KeyAction.ModifierKey(ModifierType.CAPS_LOCK)))
        put("KEY_SHIFT_L", commandKey("KEY_SHIFT_L", "Shift", KeyAction.ModifierKey(ModifierType.SHIFT), KeyLegendIcon.SHIFT))
        put("KEY_SHIFT_R", commandKey("KEY_SHIFT_R", "Shift", KeyAction.ModifierKey(ModifierType.SHIFT), KeyLegendIcon.SHIFT))
        put("KEY_CTRL_L", commandKey("KEY_CTRL_L", "Ctrl", KeyAction.ModifierKey(ModifierType.CTRL)))
        put("KEY_CTRL_R", commandKey("KEY_CTRL_R", "Ctrl", KeyAction.ModifierKey(ModifierType.CTRL)))
        put("KEY_ALT_L", commandKey("KEY_ALT_L", "Alt", KeyAction.ModifierKey(ModifierType.ALT)))
        put("KEY_ALT_R", commandKey("KEY_ALT_R", "Alt", KeyAction.ModifierKey(ModifierType.ALT)))
        put("KEY_META", commandKey("KEY_META", "Meta", KeyAction.ModifierKey(ModifierType.META), KeyLegendIcon.META))
        put("KEY_META_L", commandKey("KEY_META_L", "Meta", KeyAction.ModifierKey(ModifierType.META), KeyLegendIcon.META))
        put("KEY_META_R", commandKey("KEY_META_R", "Meta", KeyAction.ModifierKey(ModifierType.META), KeyLegendIcon.META))
        put("KEY_FN_L", commandKey("KEY_FN_L", "Fn", KeyAction.LayerAction("NAV", LayerActivationMode.MOMENTARY)))
        put("KEY_FN_R", commandKey("KEY_FN_R", "Fn", KeyAction.LayerAction("NAV", LayerActivationMode.MOMENTARY)))
        put("KEY_SPACE", commandKey("KEY_SPACE", "Space", KeyAction.Text(" "), KeyLegendIcon.SPACE))
        put("KEY_SPACE_L", commandKey("KEY_SPACE_L", "Space", KeyAction.Text(" "), KeyLegendIcon.SPACE))
        put("KEY_SPACE_R", commandKey("KEY_SPACE_R", "Space", KeyAction.Text(" "), KeyLegendIcon.SPACE))

        for (number in 1..12) {
            val id = "KEY_F$number"
            put(id, commandKey(id, "F$number", KeyAction.KeyCode(KeyEvent.KEYCODE_F1 + number - 1)))
        }
        put("KEY_HOME", commandKey("KEY_HOME", "Home", KeyAction.KeyCode(KeyEvent.KEYCODE_MOVE_HOME)))
        put("KEY_END", commandKey("KEY_END", "End", KeyAction.KeyCode(KeyEvent.KEYCODE_MOVE_END)))
        put("KEY_PAGE_UP", commandKey("KEY_PAGE_UP", "PgUp", KeyAction.KeyCode(KeyEvent.KEYCODE_PAGE_UP)))
        put("KEY_PAGE_DOWN", commandKey("KEY_PAGE_DOWN", "PgDn", KeyAction.KeyCode(KeyEvent.KEYCODE_PAGE_DOWN)))
        put("KEY_COPY", commandKey("KEY_COPY", "Copy", KeyAction.KeyCode(KeyEvent.KEYCODE_C, KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON)))
        put("KEY_PASTE", commandKey("KEY_PASTE", "Paste", KeyAction.KeyCode(KeyEvent.KEYCODE_V, KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON)))
        put("KEY_CUT", commandKey("KEY_CUT", "Cut", KeyAction.KeyCode(KeyEvent.KEYCODE_X, KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON)))
        listOf("KEY_EMPTY_L1", "KEY_EMPTY_L2", "KEY_EMPTY_R1", "KEY_EMPTY_R2", "KEY_EMPTY_R3").forEach { id ->
            put(id, commandKey(id, "", KeyAction.None))
        }
    }

    val plainKeys: Map<String, KeyboardKeySpec> = keys.mapValues { (_, spec) ->
        spec.copy(secondary = null, layout = KeycapLayout.SINGLE_LEGEND)
    }

    fun specFor(id: String, plain: Boolean = false): KeyboardKeySpec? =
        (if (plain) plainKeys else keys)[id]
}
