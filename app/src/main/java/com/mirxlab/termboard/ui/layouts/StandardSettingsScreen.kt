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
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mirxlab.termboard.data.KeyboardPreferences
import kotlinx.coroutines.launch

@Composable
fun StandardSettingsScreen() {
    val context = LocalContext.current
    val preferences = remember { KeyboardPreferences(context) }
    val scope = rememberCoroutineScope()
    val heightDp by preferences.standardKeyboardHeightDpFlow.collectAsState(initial = 250)
    val compact by preferences.standardCompactFlow.collectAsState(initial = true)

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Standard settings", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Text(
            "Standard keyboard size and row arrangement.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        Text("Standard keyboard height: ${heightDp}dp", style = MaterialTheme.typography.titleSmall)
        Slider(
            value = heightDp.toFloat(),
            onValueChange = { scope.launch { preferences.setStandardKeyboardHeightDp(it.toInt()) } },
            valueRange = 190f..340f,
            steps = 14
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Compact layout", style = MaterialTheme.typography.titleSmall)
                Text(
                    "Use four rows; move numbers and function keys to Nav.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = compact,
                onCheckedChange = { scope.launch { preferences.setStandardCompact(it) } }
            )
        }
    }
}
