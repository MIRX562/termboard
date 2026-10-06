package com.mirxlab.termboard.ui.clipboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirxlab.termboard.data.ClipEntity
import com.mirxlab.termboard.ui.components.SearchKeypad

@Composable
fun ClipboardView(
    clipboardWrapper: ClipboardManagerWrapper,
    onClipSelected: (String) -> Unit,
    onBackToKeyboard: () -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.surface
) {
    val clips by clipboardWrapper.getAllClips().collectAsState(initial = emptyList())
    val focusManager = LocalFocusManager.current
    var query by remember { mutableStateOf("") }
    var showSearchKeys by remember { mutableStateOf(false) }
    val filteredClips = clips.filter { it.text.contains(query.trim(), ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Top Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackToKeyboard) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardReturn,
                    contentDescription = "Back to keyboard",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = "Clipboard History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            if (clips.any { !it.isPinned }) {
                IconButton(onClick = { clipboardWrapper.clearHistory() }) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear unpinned clips",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        OutlinedTextField(
            value = query,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)
                .onFocusChanged { if (it.isFocused) showSearchKeys = true },
            singleLine = true,
            placeholder = { Text("Search clipboard") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    TextButton(onClick = { query = "" }) { Text("Clear") }
                }
            }
        )

        if (showSearchKeys) {
            SearchKeypad(
                modifier = Modifier.weight(1f),
                onCharacter = { query += it },
                onBackspace = { query = query.dropLast(1) },
                onSpace = { query += " " },
                onDone = {
                    showSearchKeys = false
                    focusManager.clearFocus()
                }
            )
        } else if (filteredClips.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (clips.isEmpty()) {
                        "Clipboard history is empty.\nCopied items will appear here."
                    } else {
                        "No clipboard items match \"$query\"."
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredClips, key = { it.id }) { clip ->
                    ClipItemCard(
                        clip = clip,
                        onClipClick = { onClipSelected(clip.text) },
                        onPinToggle = { clipboardWrapper.togglePin(clip) },
                        onDelete = { clipboardWrapper.deleteClip(clip) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ClipItemCard(
    clip: ClipEntity,
    onClipClick: () -> Unit,
    onPinToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClipClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (clip.isPinned) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = clip.text,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onPinToggle) {
                Icon(
                    imageVector = if (clip.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                    contentDescription = if (clip.isPinned) "Unpin clip" else "Pin clip",
                    tint = if (clip.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete clip",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
