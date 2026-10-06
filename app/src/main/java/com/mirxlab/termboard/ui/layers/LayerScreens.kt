package com.mirxlab.termboard.ui.layers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mirxlab.termboard.data.KeyboardPreferences
import com.mirxlab.termboard.data.models.Combo
import com.mirxlab.termboard.data.models.KeyAction
import com.mirxlab.termboard.data.models.KeyDefinition
import com.mirxlab.termboard.data.models.Layer
import com.mirxlab.termboard.data.models.Macro
import com.mirxlab.termboard.engine.KeyboardLayouts
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun LayerManagerScreen(
    onAddLayer: () -> Unit,
    onEditLayer: (String) -> Unit
) {
    val context = LocalContext.current
    val preferences = remember { KeyboardPreferences(context) }
    val storedLayers by preferences.layersFlow.collectAsState(initial = emptyList())
    val layers = remember(storedLayers) {
        (KeyboardLayouts.defaultLayers + storedLayers.associateBy { it.id }).values.toList()
    }
    val builtInIds = KeyboardLayouts.defaultLayers.keys
    val scope = rememberCoroutineScope()
    var deleteLayer by remember { mutableStateOf<Layer?>(null) }
    var confirmResetBase by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Layers", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                Text(
                    "${layers.size} keyboard layers",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onAddLayer) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("New layer")
            }
        }
        TextButton(onClick = { confirmResetBase = true }) {
            Text("Reset base layout")
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(layers, key = Layer::id) { layer ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(layer.name, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    if (layer.id in builtInIds) "BUILT IN" else "CUSTOM",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            if (layer.description.isNotBlank()) {
                                Text(
                                    layer.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                "${layer.keyMappings.size} remapped keys",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { onEditLayer(layer.id) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit ${layer.name}")
                        }
                        if (layer.id !in builtInIds) {
                            IconButton(onClick = { deleteLayer = layer }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete ${layer.name}")
                            }
                        }
                    }
                }
            }
        }
    }

    deleteLayer?.let { layer ->
        AlertDialog(
            onDismissRequest = { deleteLayer = null },
            title = { Text("Delete ${layer.name}?") },
            text = { Text("This layer and its key mappings will be removed.") },
            confirmButton = {
                TextButton(onClick = {
                    val remaining = layers.filterNot { it.id == layer.id }
                    scope.launch { preferences.setLayers(remaining) }
                    deleteLayer = null
                }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { deleteLayer = null }) { Text("Cancel") } }
        )
    }

    if (confirmResetBase) {
        AlertDialog(
            onDismissRequest = { confirmResetBase = false },
            title = { Text("Reset base layouts?") },
            text = { Text("Restore the default Base mappings for Split and Standard layouts? Custom layers and Nav mappings will be kept.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        preferences.setLayers(storedLayers.filterNot { it.id == "BASE" })
                        preferences.setStandardLayers(
                            preferences.standardLayersFlow.first().filterNot { it.id == "BASE" }
                        )
                    }
                    confirmResetBase = false
                }) { Text("Reset") }
            },
            dismissButton = { TextButton(onClick = { confirmResetBase = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun LayerEditorScreen(
    layerId: String,
    isNew: Boolean,
    onCancel: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val preferences = remember { KeyboardPreferences(context) }
    val storedLayers by preferences.layersFlow.collectAsState(initial = emptyList())
    val storedCombos by preferences.combosFlow.collectAsState(initial = emptyList())
    val storedMacros by preferences.macrosFlow.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var layer by remember(layerId) { mutableStateOf<Layer?>(null) }
    var editingKey by remember { mutableStateOf<KeyDefinition?>(null) }
    var creatingCombo by remember { mutableStateOf(false) }
    var creatingMacro by remember { mutableStateOf(false) }

    LaunchedEffect(layerId, isNew) {
        layer = if (isNew) {
            Layer(id = layerId, name = "New layer", displayName = "CUSTOM")
        } else {
            val savedLayers = preferences.layersFlow.first()
            val availableLayers = KeyboardLayouts.defaultLayers + savedLayers.associateBy { it.id }
            availableLayers[layerId] ?: Layer(id = layerId, name = "New layer", displayName = "CUSTOM")
        }
    }

    val currentLayer = layer
    if (currentLayer == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading layer…", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    val allLayers = remember(storedLayers) {
        KeyboardLayouts.defaultLayers + storedLayers.associateBy { it.id }
    }
    val allKeys = remember {
        (
            KeyboardLayouts.splitLeftRows.flatten() +
                KeyboardLayouts.splitRightRows.flatten() +
                KeyboardLayouts.splitLeftThumbCluster +
                KeyboardLayouts.splitRightThumbCluster +
                KeyboardLayouts.splitLeftBottomRow +
                KeyboardLayouts.splitRightBottomRow
            ).distinctBy { it.id }
    }
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel) { Text("Cancel") }
            Text(
                if (isNew) "New layer" else "Edit layer",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(onClick = {
                val updated = allLayers + (currentLayer.id to currentLayer)
                scope.launch {
                    preferences.setLayers(updated.values.toList())
                    onSaved()
                }
            }) { Text("Save") }
        }

        OutlinedTextField(
            value = currentLayer.name,
            onValueChange = { layer = currentLayer.copy(name = it) },
            label = { Text("Layer name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = currentLayer.displayName,
            onValueChange = { layer = currentLayer.copy(displayName = it) },
            label = { Text("Keyboard label") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = currentLayer.description,
            onValueChange = { layer = currentLayer.copy(description = it) },
            label = { Text("Description") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Text("Keymap", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        VialKeymapPreview(
            layer = currentLayer,
            onKeyClick = { editingKey = it }
        )
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Combos", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            TextButton(onClick = { creatingCombo = true }) { Text("New combo") }
        }
        if (storedCombos.isEmpty()) {
            Text("No combos yet", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(modifier = Modifier.heightIn(max = 148.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(storedCombos, key = Combo::id) { combo ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(combo.name, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "${combo.keyIds.joinToString(" + ")} → ${combo.resultAction::class.simpleName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = {
                                scope.launch { preferences.setCombos(storedCombos.filterNot { it.id == combo.id }) }
                            }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete ${combo.name}")
                            }
                        }
                    }
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Macros (${storedMacros.size})", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            TextButton(onClick = { creatingMacro = true }) { Text("New text macro") }
        }
        if (storedMacros.isNotEmpty()) {
            Text(
                storedMacros.joinToString(" · ") { it.name },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    editingKey?.let { key ->
        VialActionDialog(
            key = key,
            currentAction = currentLayer.keyMappings[key.id],
            combos = storedCombos,
            layers = allLayers.values.toList(),
            macros = storedMacros,
            onDismiss = { editingKey = null },
            onSave = { action ->
                val mappings = currentLayer.keyMappings.toMutableMap()
                if (action == null) mappings.remove(key.id) else mappings[key.id] = action
                layer = currentLayer.copy(keyMappings = mappings)
                editingKey = null
            }
        )
    }
    if (creatingCombo) {
        VialComboDialog(
            keys = allKeys,
            onDismiss = { creatingCombo = false },
            onSave = { combo ->
                scope.launch { preferences.setCombos(storedCombos + combo) }
                creatingCombo = false
            }
        )
    }
    if (creatingMacro) {
        VialMacroDialog(
            onDismiss = { creatingMacro = false },
            onSave = { macro: Macro ->
                scope.launch { preferences.setMacros(storedMacros + macro) }
                creatingMacro = false
            }
        )
    }
}