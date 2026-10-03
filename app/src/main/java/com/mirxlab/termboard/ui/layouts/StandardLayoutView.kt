package com.mirxlab.termboard.ui.layouts

import android.view.inputmethod.InputConnection
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mirxlab.termboard.data.models.KeyAction
import com.mirxlab.termboard.data.models.ModifierStatus
import com.mirxlab.termboard.data.models.ModifierType
import com.mirxlab.termboard.engine.KeyboardEngine
import com.mirxlab.termboard.engine.KeyboardKeyCatalog
import com.mirxlab.termboard.engine.KeyboardLayouts
import com.mirxlab.termboard.engine.KeyActionLabel
import com.mirxlab.termboard.ui.components.QmkKeyButton
import com.mirxlab.termboard.ui.components.toDisplayLabel
import com.mirxlab.termboard.ui.components.toArrowDirection

@Composable
fun StandardLayoutView(
    engine: KeyboardEngine,
    inputConnectionProvider: () -> InputConnection?,
    modifier: Modifier = Modifier
) {
    val settings by engine.settings
    val modifierState by engine.modifierState
    val activeLayerId by engine.activeLayerId

    val isShifted = modifierState.isShiftActive()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        KeyboardLayouts.standardRows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { keyDef ->
                    val resolvedAction = engine.resolveAction(keyDef)
                    val supportsHold = when (resolvedAction) {
                        is KeyAction.ModifierKey -> resolvedAction.modifier != ModifierType.CAPS_LOCK
                        is KeyAction.LayerAction -> resolvedAction.mode == com.mirxlab.termboard.data.models.LayerActivationMode.MOMENTARY
                        else -> false
                    }
                    val keySpec = KeyboardKeyCatalog.specFor(keyDef.id)
                    val baseFace = keySpec?.primary ?: KeyActionLabel(keyDef.baseLabel, keyDef.baseAction)
                    val shiftedFace = keySpec?.shifted ?: baseFace
                    val layerOverridesKey = activeLayerId != "BASE" && resolvedAction != keyDef.baseAction
                    val activeFace = when {
                        layerOverridesKey -> KeyActionLabel(resolvedAction.toDisplayLabel() ?: keyDef.baseLabel, resolvedAction)
                        isShifted -> shiftedFace
                        else -> baseFace
                    }
                    val primaryLabel = activeFace.label.takeIf { it.isNotBlank() }
                    val secondaryLabel = if (layerOverridesKey) baseFace.label.takeIf { it.isNotBlank() }
                    else keySpec?.secondary?.label
                    val popupOptions = buildList {
                        add(activeFace)
                        if (layerOverridesKey || isShifted) add(baseFace)
                        if (!layerOverridesKey && !isShifted && shiftedFace.action != baseFace.action) add(shiftedFace)
                        keySpec?.secondary?.let(::add)
                    }.distinctBy { it.action }

                    val modifierStatus = when (val action = keyDef.baseAction) {
                        is KeyAction.ModifierKey -> when (action.modifier) {
                            ModifierType.SHIFT -> modifierState.shift
                            ModifierType.CTRL -> modifierState.ctrl
                            ModifierType.ALT -> modifierState.alt
                            ModifierType.META -> modifierState.meta
                            ModifierType.FN -> modifierState.fn
                            ModifierType.CAPS_LOCK -> modifierState.capsLock
                        }
                        is KeyAction.LayerAction -> if (activeLayerId == action.layerId) ModifierStatus.HELD else ModifierStatus.INACTIVE
                        else -> ModifierStatus.INACTIVE
                    }

                    QmkKeyButton(
                        primaryLabel = primaryLabel,
                        secondaryLabel = secondaryLabel,
                        weight = keyDef.widthWeight,
                        height = settings.keyHeightDp.dp,
                        modifierStatus = modifierStatus,
                        isSpecialKey = keyDef.isSpecial,
                        hapticEnabled = settings.hapticEnabled,
                        alpha = settings.translucencyAlpha,
                        onClick = { engine.onKeyPress(keyDef, inputConnectionProvider()) },
                        popupOptions = popupOptions,
                        legendIcon = activeFace.icon,
                        onPopupOptionSelected = { option ->
                            engine.executeAction(option.action, inputConnectionProvider())
                        },
                        onPressStart = { engine.onKeyDown(keyDef) },
                        holdDurationMs = settings.modifierHoldDurationMs,
                        onHold = if (supportsHold) {{ engine.onKeyHold(keyDef) }} else null,
                        onPressEnd = { released, wasHeld -> engine.onKeyUp(keyDef, released, wasHeld, inputConnectionProvider()) },
                        onSwipe = if (keyDef.id == "KEY_SPACE") {
                            { direction ->
                                engine.executeAction(KeyAction.Arrow(direction.toArrowDirection()), inputConnectionProvider())
                            }
                        } else null,
                        onRepeatClick = if (keyDef.baseAction is KeyAction.Backspace || keyDef.baseAction is KeyAction.Delete) {
                            { engine.onKeyPress(keyDef, inputConnectionProvider()) }
                        } else null
                    )
                }
            }
        }
    }
}
