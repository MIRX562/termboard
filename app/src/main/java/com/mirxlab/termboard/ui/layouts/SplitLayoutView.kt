package com.mirxlab.termboard.ui.layouts

import android.view.inputmethod.InputConnection
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mirxlab.termboard.data.models.KeyAction
import com.mirxlab.termboard.data.models.KeyDefinition
import com.mirxlab.termboard.data.models.ModifierStatus
import com.mirxlab.termboard.data.models.ModifierType
import com.mirxlab.termboard.engine.KeyboardEngine
import com.mirxlab.termboard.engine.KeyboardKeyCatalog
import com.mirxlab.termboard.engine.KeyboardLayouts
import com.mirxlab.termboard.engine.KeyActionLabel
import com.mirxlab.termboard.ui.components.QmkKeyButton
import com.mirxlab.termboard.ui.components.toDisplayLabel
import com.mirxlab.termboard.ui.components.toArrowDirection
import com.mirxlab.termboard.ui.components.SideLayerPanel

@Composable
fun SplitLayoutView(
    engine: KeyboardEngine,
    inputConnectionProvider: () -> InputConnection?,
    modifier: Modifier = Modifier
) {
    val settings by engine.settings
    val modifierState by engine.modifierState
    val activeLayerId by engine.activeLayerId
    val layers by engine.layers

    val isShifted = modifierState.isShiftActive()
    val isFnActive = modifierState.isFnActive() || activeLayerId != "BASE"

    val leftLayers = layers.values.filter { it.id != "BASE" && it.id != "MEDIA" }
    val rightLayers = listOfNotNull(layers["MEDIA"], layers["BASE"])

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Side Layer Selector (Revealed when Fn is active)
        if (isFnActive) {
            SideLayerPanel(
                layers = leftLayers,
                activeLayerId = activeLayerId,
                onSelectLayer = { engine.setActiveLayer(it) },
                modifier = Modifier.padding(end = 4.dp)
            )
        }

        // LEFT HALF KEYBOARD (Sofle / QMK Ergonomic Split)
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            // Left Matrix Rows
            KeyboardLayouts.splitLeftRows.forEachIndexed { rowIndex, row ->
                val startPadding = when (rowIndex) {
                    1 -> 4.dp
                    2 -> 8.dp
                    3 -> 0.dp
                    else -> 0.dp
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = startPadding)
                ) {
                    row.forEach { keyDef ->
                        RenderSplitKey(keyDef, engine, inputConnectionProvider, isShifted, activeLayerId)
                    }
                }
            }

            // Left Thumb Cluster
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                KeyboardLayouts.splitLeftThumbCluster.forEach { keyDef ->
                    RenderSplitKey(keyDef, engine, inputConnectionProvider, isShifted, activeLayerId)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                KeyboardLayouts.splitLeftBottomRow.forEach { keyDef ->
                    RenderSplitKey(keyDef, engine, inputConnectionProvider, isShifted, activeLayerId)
                }
            }
        }

        // ABSOLUTE EMPTY CENTER GAP (Sofle Ergonomic Tablet Gap)
        Spacer(modifier = Modifier.weight(settings.centerGapRatio))

        // RIGHT HALF KEYBOARD (Sofle / QMK Ergonomic Split)
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End
        ) {
            // Right Matrix Rows
            KeyboardLayouts.splitRightRows.forEachIndexed { rowIndex, row ->
                val endPadding = when (rowIndex) {
                    1 -> 4.dp
                    2 -> 8.dp
                    3 -> 0.dp
                    else -> 0.dp
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = endPadding)
                ) {
                    row.forEach { keyDef ->
                        RenderSplitKey(keyDef, engine, inputConnectionProvider, isShifted, activeLayerId)
                    }
                }
            }

            // Right Thumb Cluster
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, end = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                KeyboardLayouts.splitRightThumbCluster.forEach { keyDef ->
                    RenderSplitKey(keyDef, engine, inputConnectionProvider, isShifted, activeLayerId)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                KeyboardLayouts.splitRightBottomRow.forEach { keyDef ->
                    RenderSplitKey(keyDef, engine, inputConnectionProvider, isShifted, activeLayerId)
                }
            }
        }

        // Right Side Layer Selector (Revealed when Fn is active)
        if (isFnActive) {
            SideLayerPanel(
                layers = rightLayers,
                activeLayerId = activeLayerId,
                onSelectLayer = { engine.setActiveLayer(it) },
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun RowScope.RenderSplitKey(
    keyDef: KeyDefinition,
    engine: KeyboardEngine,
    inputConnectionProvider: () -> InputConnection?,
    isShifted: Boolean,
    activeLayerId: String
) {
    val settings by engine.settings
    val modifierState by engine.modifierState
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
        onSwipe = if (keyDef.id == "KEY_SPACE_L" || keyDef.id == "KEY_SPACE_R") {
            { direction -> engine.executeAction(KeyAction.Arrow(direction.toArrowDirection()), inputConnectionProvider()) }
        } else null,
        onRepeatClick = if (keyDef.baseAction is KeyAction.Backspace || keyDef.baseAction is KeyAction.Delete) {
            { engine.onKeyPress(keyDef, inputConnectionProvider()) }
        } else null
    )
}
