package com.mirxlab.termboard.data.models

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class LayerSerializationTest {
    @Test
    fun textMacrosRoundTripThroughJson() {
        val macros = listOf(
            Macro(
                id = "greeting",
                name = "Greeting",
                steps = listOf(KeyAction.Text("Hello"), KeyAction.Text(" world"))
            )
        )

        val encoded = Json.encodeToString<List<Macro>>(macros)
        val decoded = Json.decodeFromString<List<Macro>>(encoded)

        assertEquals(macros, decoded)
    }

    @Test
    fun comboDefinitionsRoundTripThroughJson() {
        val combos = listOf(
            Combo(
                id = "copy",
                name = "Copy",
                keyIds = setOf("KEY_CTRL_L", "KEY_C"),
                resultAction = KeyAction.Text("copy")
            )
        )

        val encoded = Json.encodeToString<List<Combo>>(combos)
        val decoded = Json.decodeFromString<List<Combo>>(encoded)

        assertEquals(combos, decoded)
    }

    @Test
    fun layerMappingsRoundTripThroughJson() {
        val layers = listOf(
            Layer(
                id = "CUSTOM",
                name = "Custom",
                displayName = "CUS",
                keyMappings = mapOf(
                    "KEY_A" to KeyAction.Text("alpha"),
                    "KEY_H" to KeyAction.Arrow(ArrowDirection.LEFT),
                    "KEY_META" to KeyAction.ModifierKey(ModifierType.META),
                    "KEY_FN" to KeyAction.LayerAction("NAV", LayerActivationMode.MOMENTARY),
                    "KEY_ESC" to KeyAction.Escape
                )
            )
        )

        val encoded = Json.encodeToString<List<Layer>>(layers)
        val decoded = Json.decodeFromString<List<Layer>>(encoded)

        assertEquals(layers, decoded)
    }
}