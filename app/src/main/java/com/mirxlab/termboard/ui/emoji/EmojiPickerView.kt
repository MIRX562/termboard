package com.mirxlab.termboard.ui.emoji

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirxlab.termboard.ui.components.SearchKeypad

@Composable
fun EmojiPickerView(
    onEmojiSelected: (String) -> Unit,
    onBackspace: () -> Unit,
    onBackToKeyboard: () -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.surface
) {
    val focusManager = LocalFocusManager.current
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    var query by remember { mutableStateOf("") }
    var showSearchKeys by remember { mutableStateOf(false) }
    val categories = EmojiRepository.categories
    val currentCategory = categories.getOrElse(selectedCategoryIndex) { categories.first() }
    val emojiResults = if (query.isBlank()) {
        currentCategory.emojis
    } else {
        val search = query.trim()
        categories.flatMap { category ->
            category.emojis.filter { emoji ->
                emoji.contains(search, ignoreCase = true) ||
                    category.name.contains(search, ignoreCase = true) ||
                    EmojiRepository.searchTerms[emoji]?.contains(search, ignoreCase = true) == true
            }
        }.distinct()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Top Bar: Back to Keyboard, Category Tabs, Backspace
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

            ScrollableTabRow(
                selectedTabIndex = selectedCategoryIndex,
                modifier = Modifier.weight(1f),
                edgePadding = 0.dp,
                divider = {}
            ) {
                categories.forEachIndexed { index, category ->
                    Tab(
                        selected = selectedCategoryIndex == index,
                        onClick = { selectedCategoryIndex = index },
                        text = {
                            Text(
                                text = category.icon,
                                fontSize = 18.sp
                            )
                        }
                    )
                }
            }

            IconButton(onClick = onBackspace) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Backspace",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        OutlinedTextField(
            value = query,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
                .onFocusChanged { if (it.isFocused) showSearchKeys = true },
            singleLine = true,
            placeholder = { Text("Search emoji") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    TextButton(onClick = { query = "" }) { Text("Clear") }
                }
            }
        )

        // Emoji Grid
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
        } else if (emojiResults.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No emoji found", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 44.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(emojiResults) { emoji ->
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onEmojiSelected(emoji) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = emoji,
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }
    }
}
