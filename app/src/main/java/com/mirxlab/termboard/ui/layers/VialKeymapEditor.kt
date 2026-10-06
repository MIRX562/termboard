package com.mirxlab.termboard.ui.layers

import android.view.KeyEvent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirxlab.termboard.data.models.ArrowDirection
import com.mirxlab.termboard.data.models.Combo
import com.mirxlab.termboard.data.models.KeyAction
import com.mirxlab.termboard.data.models.KeyDefinition
import com.mirxlab.termboard.data.models.Layer
import com.mirxlab.termboard.data.models.LayerActivationMode
import com.mirxlab.termboard.data.models.Macro
import com.mirxlab.termboard.data.models.ModifierType
import com.mirxlab.termboard.engine.KeyboardKeyCatalog
import com.mirxlab.termboard.engine.KeyboardLayouts
import com.mirxlab.termboard.ui.components.AdaptiveKeyLegend
import com.mirxlab.termboard.ui.components.toDisplayLabel
import java.util.UUID

@Composable
fun VialKeymapPreview(
    layer: Layer,
    onKeyClick: (KeyDefinition) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                "SPLIT KEYBOARD",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(10.dp))
            androidx.compose.foundation.layout.BoxWithConstraints(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .widthIn(min = maxWidth),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    KeyboardHalf(
                        rows = KeyboardLayouts.splitLeftRows,
                        thumbKeys = KeyboardLayouts.splitLeftThumbCluster,
                        bottomKeys = KeyboardLayouts.splitLeftBottomRow,
                        layer = layer,
                        onKeyClick = onKeyClick
                    )
                    Spacer(Modifier.width(112.dp))
                    KeyboardHalf(
                        rows = KeyboardLayouts.splitRightRows,
                        thumbKeys = KeyboardLayouts.splitRightThumbCluster,
                        bottomKeys = KeyboardLayouts.splitRightBottomRow,
                        layer = layer,
                        onKeyClick = onKeyClick
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Tap a key to edit its action",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StandardKeymapPreview(
    rows: List<List<KeyDefinition>>,
    layer: Layer,
    onKeyClick: (KeyDefinition) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "STANDARD KEYBOARD",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    row.forEach { key -> KeyCap(key, layer.keyMappings[key.id], onKeyClick) }
                }
            }
        }
    }
}

@Composable
private fun KeyboardHalf(
    rows: List<List<KeyDefinition>>,
    thumbKeys: List<KeyDefinition>,
    bottomKeys: List<KeyDefinition>,
    layer: Layer,
    onKeyClick: (KeyDefinition) -> Unit
) {
    Column(modifier = Modifier.width(342.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { key ->
                    KeyCap(key, layer.keyMappings[key.id], onKeyClick)
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            thumbKeys.forEach { key ->
                KeyCap(key, layer.keyMappings[key.id], onKeyClick)
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            bottomKeys.forEach { key ->
                KeyCap(key, layer.keyMappings[key.id], onKeyClick)
            }
        }
    }
}

@Composable
private fun RowScope.KeyCap(
    key: KeyDefinition,
    action: KeyAction?,
    onKeyClick: (KeyDefinition) -> Unit
) {
    val spec = KeyboardKeyCatalog.specFor(key.id)
    val layerOverride = action != null && action != key.baseAction
    val primary = if (layerOverride) action?.toDisplayLabel() ?: key.baseLabel
    else spec?.primary?.label ?: key.baseLabel
    val secondary = if (layerOverride) spec?.primary?.label else spec?.secondary?.label
    Surface(
        modifier = Modifier
            .weight(key.widthWeight)
            .height(54.dp)
            .padding(1.5.dp)
            .clickable { onKeyClick(key) },
        color = if (action != null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(5.dp),
        tonalElevation = if (action != null) 1.dp else 0.dp
    ) {
        AdaptiveKeyLegend(
            primaryLabel = primary,
            secondaryLabel = secondary,
            primaryColor = if (action != null) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
            secondaryColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.72f),
            primaryWeight = FontWeight.SemiBold,
            legendIcon = if (layerOverride) null else spec?.primary?.icon
        )
    }
}

private enum class VialActionChoice(val label: String) {
    BASE("Use base key"),
    TEXT("Insert text"),
    KEY_CODE("Send keycode"),
    ESCAPE("Escape"),
    ENTER("Enter"),
    TAB("Tab"),
    DELETE("Delete"),
    BACKSPACE("Backspace"),
    LEFT("Arrow left"),
    DOWN("Arrow down"),
    UP("Arrow up"),
    RIGHT("Arrow right"),
    SHIFT("Shift"),
    CTRL("Control"),
    ALT("Alt"),
    META("Super / Meta"),
    CAPS_LOCK("Caps Lock"),
    FN("Fn layer"),
    LAYER("Switch layer"),
    COMBO("Run combo"),
    MACRO("Run macro"),
    CLIPBOARD("Open clipboard"),
    EMOJI("Open emoji picker"),
    NONE("No action")
}

private data class KeyCodeChoice(val label: String, val code: Int, val metaState: Int = 0)

private val keyCodeChoices = buildList {
    add(KeyCodeChoice("Home", KeyEvent.KEYCODE_MOVE_HOME))
    add(KeyCodeChoice("End", KeyEvent.KEYCODE_MOVE_END))
    add(KeyCodeChoice("Page Up", KeyEvent.KEYCODE_PAGE_UP))
    add(KeyCodeChoice("Page Down", KeyEvent.KEYCODE_PAGE_DOWN))
    add(KeyCodeChoice("Copy", KeyEvent.KEYCODE_C, KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON))
    add(KeyCodeChoice("Paste", KeyEvent.KEYCODE_V, KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON))
    add(KeyCodeChoice("Cut", KeyEvent.KEYCODE_X, KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON))
    add(KeyCodeChoice("Escape", KeyEvent.KEYCODE_ESCAPE))
    add(KeyCodeChoice("Forward Delete", KeyEvent.KEYCODE_FORWARD_DEL))
    add(KeyCodeChoice("Volume Up", KeyEvent.KEYCODE_VOLUME_UP))
    add(KeyCodeChoice("Volume Down", KeyEvent.KEYCODE_VOLUME_DOWN))
    add(KeyCodeChoice("Mute", KeyEvent.KEYCODE_VOLUME_MUTE))
    add(KeyCodeChoice("Media Play/Pause", KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE))
    add(KeyCodeChoice("Media Previous", KeyEvent.KEYCODE_MEDIA_PREVIOUS))
    add(KeyCodeChoice("Media Next", KeyEvent.KEYCODE_MEDIA_NEXT))
    for (number in 1..12) add(KeyCodeChoice("F$number", KeyEvent.KEYCODE_F1 + number - 1))
}

@Composable
fun VialActionDialog(
    key: KeyDefinition,
    currentAction: KeyAction?,
    combos: List<Combo>,
    layers: List<Layer>,
    macros: List<Macro>,
    onDismiss: () -> Unit,
    onSave: (KeyAction?) -> Unit
) {
    var choice by remember(key.id, currentAction) { mutableStateOf(actionChoice(currentAction)) }
    var text by remember(key.id, currentAction) {
        mutableStateOf(
            (currentAction as? KeyAction.Text)?.text
                ?: (key.baseAction as? KeyAction.Text)?.text.orEmpty()
        )
    }
    var comboId by remember(key.id, currentAction) {
        mutableStateOf((currentAction as? KeyAction.ComboAction)?.comboId ?: combos.firstOrNull()?.id)
    }
    var layerId by remember(key.id, currentAction) {
        mutableStateOf((currentAction as? KeyAction.LayerAction)?.layerId ?: layers.firstOrNull()?.id)
    }
    var layerMode by remember(key.id, currentAction) {
        mutableStateOf((currentAction as? KeyAction.LayerAction)?.mode ?: LayerActivationMode.MOMENTARY)
    }
    var macroId by remember(key.id, currentAction) {
        mutableStateOf((currentAction as? KeyAction.MacroAction)?.macroId ?: macros.firstOrNull()?.id)
    }
    var keyCode by remember(key.id, currentAction) {
        mutableStateOf(
            (currentAction as? KeyAction.KeyCode)?.let { action ->
                keyCodeChoices.firstOrNull { it.code == action.keyCode && it.metaState == action.metaState }
            } ?: keyCodeChoices.first()
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Assign ${key.baseLabel}") },
        text = {
            Column(modifier = Modifier.heightIn(max = 400.dp).verticalScroll(rememberScrollState())) {
                VialActionChoice.entries.forEach { option ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { choice = option },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = choice == option, onClick = { choice = option })
                        Text(option.label)
                    }
                }
                when (choice) {
                    VialActionChoice.TEXT -> OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Text to insert") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    VialActionChoice.KEY_CODE -> keyCodeChoices.forEach { option ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { keyCode = option },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = keyCode == option, onClick = { keyCode = option })
                            Text(option.label)
                        }
                    }
                    VialActionChoice.COMBO -> {
                        if (combos.isEmpty()) {
                            Text(
                                "Create a combo from the layer editor first.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(12.dp)
                            )
                        } else {
                            combos.forEach { combo ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable { comboId = combo.id },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(selected = comboId == combo.id, onClick = { comboId = combo.id })
                                    Column {
                                        Text(combo.name)
                                        Text(
                                            "${combo.keyIds.size} keys · ${actionLabel(combo.resultAction)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                    VialActionChoice.LAYER -> layers.forEach { layer ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { layerId = layer.id },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = layerId == layer.id, onClick = { layerId = layer.id })
                            Text(layer.name)
                        }
                    }.also {
                        LayerActivationMode.entries.forEach { mode ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { layerMode = mode },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = layerMode == mode, onClick = { layerMode = mode })
                                Text(mode.name.lowercase().replace('_', ' '))
                            }
                        }
                    }
                    VialActionChoice.MACRO -> {
                        if (macros.isEmpty()) {
                            Text("Create a text macro in the layer editor first.", modifier = Modifier.padding(12.dp))
                        } else {
                            macros.forEach { macro ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable { macroId = macro.id },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(selected = macroId == macro.id, onClick = { macroId = macro.id })
                                    Text(macro.name)
                                }
                            }
                        }
                    }
                    else -> Unit
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = when (choice) {
                    VialActionChoice.TEXT -> text.isNotEmpty()
                    VialActionChoice.COMBO -> comboId != null && combos.any { it.id == comboId }
                    VialActionChoice.LAYER -> layerId != null && layers.any { it.id == layerId }
                    VialActionChoice.MACRO -> macroId != null && macros.any { it.id == macroId }
                    else -> true
                },
                onClick = { onSave(actionFor(choice, text, comboId, layerId, macroId, keyCode, layerMode)) }
            ) { Text("Apply") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun VialComboDialog(
    keys: List<KeyDefinition>,
    onDismiss: () -> Unit,
    onSave: (Combo) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedKeys by remember { mutableStateOf(emptySet<String>()) }
    var resultChoice by remember { mutableStateOf(VialActionChoice.TEXT) }
    var resultText by remember { mutableStateOf("") }
    var resultKeyCode by remember { mutableStateOf(keyCodeChoices.first()) }
    val actionChoices = VialActionChoice.entries.filter {
        it != VialActionChoice.BASE && it != VialActionChoice.COMBO &&
            it != VialActionChoice.LAYER && it != VialActionChoice.MACRO
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create combo") },
        text = {
            Column(modifier = Modifier.heightIn(max = 480.dp).verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Combo name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "Choose at least two keys",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 12.dp)
                )
                keys.forEach { key ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable {
                            selectedKeys = if (key.id in selectedKeys) selectedKeys - key.id else selectedKeys + key.id
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = key.id in selectedKeys,
                            onCheckedChange = { checked ->
                                selectedKeys = if (checked) selectedKeys + key.id else selectedKeys - key.id
                            }
                        )
                        Text("${key.baseLabel} · ${key.id}")
                    }
                }
                Text(
                    "Result action",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 12.dp)
                )
                actionChoices.forEach { option ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { resultChoice = option },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = resultChoice == option, onClick = { resultChoice = option })
                        Text(option.label)
                    }
                }
                if (resultChoice == VialActionChoice.TEXT) {
                    OutlinedTextField(
                        value = resultText,
                        onValueChange = { resultText = it },
                        label = { Text("Text to insert") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (resultChoice == VialActionChoice.KEY_CODE) {
                    keyCodeChoices.forEach { option ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { resultKeyCode = option },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = resultKeyCode == option, onClick = { resultKeyCode = option })
                            Text(option.label)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank() && selectedKeys.size >= 2 &&
                    (resultChoice != VialActionChoice.TEXT || resultText.isNotEmpty()),
                onClick = {
                    val result = actionFor(resultChoice, resultText, null, null, null, resultKeyCode, LayerActivationMode.MOMENTARY)
                        ?: return@TextButton
                    onSave(
                        Combo(
                            id = "combo_${UUID.randomUUID()}",
                            name = name.trim(),
                            keyIds = selectedKeys,
                            resultAction = result
                        )
                    )
                }
            ) { Text("Create") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

private fun actionChoice(action: KeyAction?): VialActionChoice = when (action) {
    is KeyAction.Text -> VialActionChoice.TEXT
    is KeyAction.KeyCode -> VialActionChoice.KEY_CODE
    KeyAction.Escape -> VialActionChoice.ESCAPE
    KeyAction.Enter -> VialActionChoice.ENTER
    KeyAction.Tab -> VialActionChoice.TAB
    KeyAction.Delete -> VialActionChoice.DELETE
    KeyAction.Backspace -> VialActionChoice.BACKSPACE
    is KeyAction.Arrow -> when (action.direction) {
        ArrowDirection.LEFT -> VialActionChoice.LEFT
        ArrowDirection.DOWN -> VialActionChoice.DOWN
        ArrowDirection.UP -> VialActionChoice.UP
        ArrowDirection.RIGHT -> VialActionChoice.RIGHT
    }
    is KeyAction.ModifierKey -> when (action.modifier) {
        ModifierType.SHIFT -> VialActionChoice.SHIFT
        ModifierType.CTRL -> VialActionChoice.CTRL
        ModifierType.ALT -> VialActionChoice.ALT
        ModifierType.META -> VialActionChoice.META
        ModifierType.CAPS_LOCK -> VialActionChoice.CAPS_LOCK
        ModifierType.FN -> VialActionChoice.FN
    }
    is KeyAction.LayerAction -> VialActionChoice.LAYER
    is KeyAction.ComboAction -> VialActionChoice.COMBO
    is KeyAction.MacroAction -> VialActionChoice.MACRO
    KeyAction.OpenClipboard -> VialActionChoice.CLIPBOARD
    KeyAction.OpenEmoji -> VialActionChoice.EMOJI
    KeyAction.None -> VialActionChoice.NONE
    else -> VialActionChoice.BASE
}

private fun actionFor(
    choice: VialActionChoice,
    text: String,
    comboId: String?,
    layerId: String?,
    macroId: String?,
    keyCode: KeyCodeChoice,
    layerMode: LayerActivationMode
): KeyAction? = when (choice) {
    VialActionChoice.BASE -> null
    VialActionChoice.TEXT -> KeyAction.Text(text)
    VialActionChoice.KEY_CODE -> KeyAction.KeyCode(keyCode.code, keyCode.metaState)
    VialActionChoice.ESCAPE -> KeyAction.Escape
    VialActionChoice.ENTER -> KeyAction.Enter
    VialActionChoice.TAB -> KeyAction.Tab
    VialActionChoice.DELETE -> KeyAction.Delete
    VialActionChoice.BACKSPACE -> KeyAction.Backspace
    VialActionChoice.LEFT -> KeyAction.Arrow(ArrowDirection.LEFT)
    VialActionChoice.DOWN -> KeyAction.Arrow(ArrowDirection.DOWN)
    VialActionChoice.UP -> KeyAction.Arrow(ArrowDirection.UP)
    VialActionChoice.RIGHT -> KeyAction.Arrow(ArrowDirection.RIGHT)
    VialActionChoice.SHIFT -> KeyAction.ModifierKey(ModifierType.SHIFT)
    VialActionChoice.CTRL -> KeyAction.ModifierKey(ModifierType.CTRL)
    VialActionChoice.ALT -> KeyAction.ModifierKey(ModifierType.ALT)
    VialActionChoice.META -> KeyAction.ModifierKey(ModifierType.META)
    VialActionChoice.CAPS_LOCK -> KeyAction.ModifierKey(ModifierType.CAPS_LOCK)
    VialActionChoice.FN -> KeyAction.ModifierKey(ModifierType.FN)
    VialActionChoice.LAYER -> layerId?.let { KeyAction.LayerAction(it, layerMode) }
    VialActionChoice.COMBO -> comboId?.let(KeyAction::ComboAction)
    VialActionChoice.MACRO -> macroId?.let(KeyAction::MacroAction)
    VialActionChoice.CLIPBOARD -> KeyAction.OpenClipboard
    VialActionChoice.EMOJI -> KeyAction.OpenEmoji
    VialActionChoice.NONE -> KeyAction.None
}

private fun actionLabel(action: KeyAction): String = when (action) {
    is KeyAction.Text -> "Text ${action.text}"
    is KeyAction.KeyCode -> KeyEvent.keyCodeToString(action.keyCode).removePrefix("KEYCODE_")
    is KeyAction.Arrow -> "Arrow ${action.direction.name.lowercase()}"
    KeyAction.Escape -> "Escape"
    KeyAction.Enter -> "Enter"
    KeyAction.Tab -> "Tab"
    KeyAction.Delete -> "Delete"
    KeyAction.Backspace -> "Backspace"
    KeyAction.OpenClipboard -> "Open clipboard"
    KeyAction.OpenEmoji -> "Open emoji picker"
    KeyAction.None -> "No action"
    else -> "Action"
}

@Composable
fun VialMacroDialog(
    onDismiss: () -> Unit,
    onSave: (Macro) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create text macro") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Macro name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Text to insert") },
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(enabled = name.isNotBlank() && text.isNotEmpty(), onClick = {
                onSave(
                    Macro(
                        id = "macro_${UUID.randomUUID()}",
                        name = name.trim(),
                        steps = listOf(KeyAction.Text(text))
                    )
                )
            }) { Text("Create") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
