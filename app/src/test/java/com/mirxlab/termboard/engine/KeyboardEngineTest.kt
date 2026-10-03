package com.mirxlab.termboard.engine

import com.mirxlab.termboard.data.models.KeyAction
import com.mirxlab.termboard.data.models.Combo
import com.mirxlab.termboard.data.models.KeyDefinition
import com.mirxlab.termboard.data.models.LayerActivationMode
import com.mirxlab.termboard.data.models.ModifierStatus
import com.mirxlab.termboard.data.models.ModifierState
import com.mirxlab.termboard.data.models.ModifierType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class KeyboardEngineTest {

    private lateinit var engine: KeyboardEngine

    @Before
    fun setUp() {
        engine = KeyboardEngine()
    }

    @Test
    fun testDefaultActiveLayerIsBase() {
        assertEquals("BASE", engine.activeLayerId.value)
    }

    @Test
    fun testFnTogglesSecondaryLayer() {
        val fnKey = KeyDefinition("KEY_FN_L", "Fn", KeyAction.LayerAction("NAV", LayerActivationMode.MOMENTARY))
        engine.onKeyPress(fnKey, null)

        assertEquals("NAV", engine.activeLayerId.value)

        // Pressing Fn again turns it off and returns to BASE
        engine.onKeyPress(fnKey, null)
        assertEquals("BASE", engine.activeLayerId.value)
    }

    @Test
    fun testShiftModifierState() {
        val shiftKey = KeyDefinition("KEY_SHIFT_L", "Shift", KeyAction.ModifierKey(ModifierType.SHIFT))
        engine.onKeyPress(shiftKey, null)

        assertEquals(ModifierStatus.ONE_SHOT, engine.modifierState.value.shift)
        assertTrue(engine.modifierState.value.isShiftActive())

        // Double tap locks Shift
        engine.onKeyPress(shiftKey, null)
        assertEquals(ModifierStatus.LOCKED, engine.modifierState.value.shift)
    }

    @Test
    fun testSecondaryLayerActionResolution() {
        // Activate NAV layer
        engine.setActiveLayer("NAV")
        val keyH = KeyDefinition("KEY_H", "H", KeyAction.Text("h"))

        val action = engine.resolveAction(keyH)
        assertTrue(action is KeyAction.Arrow)
    }

    @Test
    fun testLayoutDefinitionsShareKeys() {
        val standardKeyQ = KeyboardLayouts.standardRows[2].find { it.id == "KEY_Q" }
        val splitKeyQ = KeyboardLayouts.splitLeftRows[1].find { it.id == "KEY_Q" }

        assertTrue(standardKeyQ != null)
        assertTrue(splitKeyQ != null)
        assertEquals(standardKeyQ?.baseAction, splitKeyQ?.baseAction)
    }

    @Test
    fun externalPasteClearsShiftButPreservesCapsLock() {
        val capsKey = KeyDefinition("KEY_CAPS", "Caps", KeyAction.ModifierKey(ModifierType.CAPS_LOCK))
        val shiftKey = KeyDefinition("KEY_SHIFT_L", "Shift", KeyAction.ModifierKey(ModifierType.SHIFT))

        engine.onKeyPress(capsKey, null)
        engine.onKeyPress(shiftKey, null)
        engine.onKeyPress(shiftKey, null)
        engine.resetAfterExternalTextCommit()

        assertEquals(ModifierStatus.LOCKED, engine.modifierState.value.capsLock)
        assertEquals(ModifierStatus.INACTIVE, engine.modifierState.value.shift)
    }

    @Test
    fun simultaneousComboExecutesAndConsumesItsKeys() {
        val keyA = KeyDefinition("KEY_A", "A", KeyAction.Text("a"))
        val keyB = KeyDefinition("KEY_B", "B", KeyAction.Text("b"))
        engine.setCombos(
            listOf(
                Combo(
                    id = "caps",
                    name = "Caps chord",
                    keyIds = setOf(keyA.id, keyB.id),
                    resultAction = KeyAction.ModifierKey(ModifierType.CAPS_LOCK)
                )
            )
        )

        engine.onKeyDown(keyA)
        engine.onKeyDown(keyB)

        assertEquals(ModifierStatus.INACTIVE, engine.modifierState.value.capsLock)

        assertTrue(engine.onKeyUp(keyA, wasReleased = true))
        assertEquals(ModifierStatus.LOCKED, engine.modifierState.value.capsLock)
        assertTrue(engine.onKeyUp(keyB, wasReleased = true))
    }

    @Test
    fun shiftAndCapsLockInvertEachOther() {
        assertFalse(ModifierState().isShiftActive())
        assertTrue(ModifierState(shift = ModifierStatus.ONE_SHOT).isShiftActive())
        assertTrue(ModifierState(capsLock = ModifierStatus.LOCKED).isShiftActive())
        assertFalse(
            ModifierState(
                shift = ModifierStatus.ONE_SHOT,
                capsLock = ModifierStatus.LOCKED
            ).isShiftActive()
        )
    }

    @Test
    fun splitUtilityRowsHaveSixKeysOnEachHalf() {
        assertEquals(6, KeyboardLayouts.splitLeftBottomRow.size)
        assertEquals(6, KeyboardLayouts.splitRightBottomRow.size)
    }

    @Test
    fun keyCatalogCoversEveryLayoutActionAndSecondaryVariant() {
        val layoutKeys = (
            KeyboardLayouts.standardRows.flatten() +
                KeyboardLayouts.splitLeftRows.flatten() +
                KeyboardLayouts.splitRightRows.flatten() +
                KeyboardLayouts.splitLeftThumbCluster +
                KeyboardLayouts.splitRightThumbCluster +
                KeyboardLayouts.splitLeftBottomRow +
                KeyboardLayouts.splitRightBottomRow
            ).distinctBy { it.id }

        val missing = layoutKeys.filter { KeyboardKeyCatalog.specFor(it.id) == null }
        assertTrue("Missing key catalog entries: ${missing.map { it.id }}", missing.isEmpty())
        layoutKeys.forEach { key ->
            assertEquals(key.id, key.baseAction, KeyboardKeyCatalog.specFor(key.id)?.primary?.action)
        }

        val letter = KeyboardKeyCatalog.specFor("KEY_Q")
        assertEquals("q", letter?.primary?.label)
        assertEquals("Q", letter?.shifted?.label)
        assertEquals("1", letter?.secondary?.label)
        assertEquals(null, KeyboardKeyCatalog.specFor("KEY_Q", plain = true)?.secondary)
        assertEquals("!", KeyboardKeyCatalog.specFor("KEY_1")?.secondary?.label)
    }

    @Test
    fun requestedSpecialKeysUseIconLegends() {
        assertEquals(KeyLegendIcon.TAB, KeyboardKeyCatalog.specFor("KEY_TAB")?.primary?.icon)
        assertEquals(KeyLegendIcon.SHIFT, KeyboardKeyCatalog.specFor("KEY_SHIFT_L")?.primary?.icon)
        assertEquals(KeyLegendIcon.META, KeyboardKeyCatalog.specFor("KEY_META_L")?.primary?.icon)
        assertEquals(KeyLegendIcon.SPACE, KeyboardKeyCatalog.specFor("KEY_SPACE_L")?.primary?.icon)
        assertEquals(KeyLegendIcon.ENTER, KeyboardKeyCatalog.specFor("KEY_ENTER")?.primary?.icon)
        assertEquals(KeyLegendIcon.BACKSPACE, KeyboardKeyCatalog.specFor("KEY_BSPACE")?.primary?.icon)
    }

    @Test
    fun heldModifierRemainsActiveUntilItsOwnRelease() {
        val ctrl = KeyDefinition("KEY_CTRL_L", "Ctrl", KeyAction.ModifierKey(ModifierType.CTRL))
        val keyK = KeyDefinition("KEY_K", "K", KeyAction.Text("k"))

        engine.onKeyDown(ctrl)
        assertTrue(engine.onKeyHold(ctrl))
        assertEquals(ModifierStatus.HELD, engine.modifierState.value.ctrl)

        engine.onKeyDown(keyK)
        engine.onKeyUp(keyK, wasReleased = true)
        assertEquals(ModifierStatus.HELD, engine.modifierState.value.ctrl)

        engine.onKeyUp(ctrl, wasReleased = true, wasHeld = true)
        assertEquals(ModifierStatus.INACTIVE, engine.modifierState.value.ctrl)
    }

    @Test
    fun atMostFiveKeysCanBecomeActiveAtOnce() {
        val heldKeys = listOf(
            KeyDefinition("CTRL", "Ctrl", KeyAction.ModifierKey(ModifierType.CTRL)),
            KeyDefinition("SHIFT", "Shift", KeyAction.ModifierKey(ModifierType.SHIFT)),
            KeyDefinition("ALT", "Alt", KeyAction.ModifierKey(ModifierType.ALT)),
            KeyDefinition("META", "Meta", KeyAction.ModifierKey(ModifierType.META)),
            KeyDefinition("FN", "Fn", KeyAction.LayerAction("NAV", LayerActivationMode.MOMENTARY))
        )
        heldKeys.forEach { key ->
            engine.onKeyDown(key)
            assertTrue(engine.onKeyHold(key))
        }

        val sixthKey = KeyDefinition("CTRL_2", "Ctrl", KeyAction.ModifierKey(ModifierType.CTRL))
        engine.onKeyDown(sixthKey)

        assertFalse(engine.onKeyHold(sixthKey))
        assertEquals(ModifierStatus.HELD, engine.modifierState.value.ctrl)
    }

    @Test
    fun heldMomentaryLayerRestoresThePreviouslyActiveLayer() {
        val fn = KeyDefinition(
            "KEY_FN_L",
            "Fn",
            KeyAction.LayerAction("NAV", LayerActivationMode.MOMENTARY)
        )
        engine.setActiveLayer("NUM")

        engine.onKeyDown(fn)
        assertTrue(engine.onKeyHold(fn))
        assertEquals("NAV", engine.activeLayerId.value)

        engine.onKeyUp(fn, wasReleased = true, wasHeld = true)
        assertEquals("NUM", engine.activeLayerId.value)
    }

    @Test
    fun splitLayoutContainsMissingPunctuationKeys() {
        val splitKeyIds = (
            KeyboardLayouts.splitLeftRows.flatten() + KeyboardLayouts.splitRightRows.flatten()
            ).map { it.id }.toSet()

        assertTrue(splitKeyIds.containsAll(setOf(
            "KEY_BACKTICK", "KEY_MINUS", "KEY_EQUAL", "KEY_LBRACKET", "KEY_RBRACKET", "KEY_QUOTE"
        )))
    }
}
