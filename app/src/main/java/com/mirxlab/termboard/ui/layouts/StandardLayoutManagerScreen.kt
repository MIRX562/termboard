package com.mirxlab.termboard.ui.layouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.mirxlab.termboard.data.models.KeyDefinition
import com.mirxlab.termboard.data.models.Layer
import com.mirxlab.termboard.engine.StandardKeyboardLayout
import com.mirxlab.termboard.ui.layers.StandardKeymapPreview
import com.mirxlab.termboard.ui.layers.VialActionDialog
import kotlinx.coroutines.launch

@Composable
fun StandardLayoutManagerScreen() {
    val context = LocalContext.current
    val preferences = remember { KeyboardPreferences(context) }
    val scope = rememberCoroutineScope()
    val storedLayers by preferences.standardLayersFlow.collectAsState(initial = emptyList())
    val storedCombos by preferences.combosFlow.collectAsState(initial = emptyList())
    val storedMacros by preferences.macrosFlow.collectAsState(initial = emptyList())
    val compact by preferences.standardCompactFlow.collectAsState(initial = true)
    val layers = remember(storedLayers) {
        StandardKeyboardLayout.defaultLayers + storedLayers.associateBy { it.id }
    }
    var selectedLayerId by remember { mutableStateOf("BASE") }
    var editingKey by remember { mutableStateOf<KeyDefinition?>(null) }
    val selectedLayer = layers[selectedLayerId] ?: StandardKeyboardLayout.defaultLayers.getValue("BASE")
    val rows = if (compact) StandardKeyboardLayout.compactRows else StandardKeyboardLayout.rows

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Standard layout", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Text(
            "Base and navigation layers for the standard keyboard.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TabRow(selectedTabIndex = if (selectedLayerId == "BASE") 0 else 1) {
            listOf("BASE", "NAV").forEachIndexed { index, layerId ->
                val layer = layers.getValue(layerId)
                Tab(
                    selected = selectedLayerId == layerId,
                    onClick = { selectedLayerId = layerId },
                    text = { Text(layer.displayName) }
                )
            }
        }
        StandardKeymapPreview(
            rows = rows,
            layer = selectedLayer,
            onKeyClick = { editingKey = it }
        )
        Text(
            "${selectedLayer.keyMappings.size} custom actions",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
    }

    editingKey?.let { key ->
        VialActionDialog(
            key = key,
            currentAction = selectedLayer.keyMappings[key.id],
            combos = storedCombos,
            layers = layers.values.toList(),
            macros = storedMacros,
            onDismiss = { editingKey = null },
            onSave = { action ->
                val mappings = selectedLayer.keyMappings.toMutableMap()
                if (action == null) mappings.remove(key.id) else mappings[key.id] = action
                val updated = selectedLayer.copy(keyMappings = mappings)
                val allUpdated = layers + (selectedLayerId to updated)
                scope.launch { preferences.setStandardLayers(allUpdated.values.toList()) }
                editingKey = null
            }
        )
    }
}
