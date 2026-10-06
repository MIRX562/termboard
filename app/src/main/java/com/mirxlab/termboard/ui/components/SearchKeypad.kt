package com.mirxlab.termboard.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchKeypad(
    onCharacter: (String) -> Unit,
    onBackspace: () -> Unit,
    onSpace: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 1.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        SearchKeyRow("qwertyuiop", onCharacter)
        SearchKeyRow("asdfghjkl", onCharacter, sideInset = 12.dp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            "zxcvbnm".forEach { character ->
                SearchKey(character.toString(), onClick = { onCharacter(character.toString()) })
            }
            SearchKey("⌫", weight = 1.5f, onClick = onBackspace)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            SearchKey("Space", weight = 5f, onClick = onSpace)
            SearchKey("Done", weight = 2f, onClick = onDone)
        }
    }
}

@Composable
private fun SearchKeyRow(
    letters: String,
    onCharacter: (String) -> Unit,
    sideInset: androidx.compose.ui.unit.Dp = 0.dp
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = sideInset),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        letters.forEach { character ->
            SearchKey(character.toString(), onClick = { onCharacter(character.toString()) })
        }
    }
}

@Composable
private fun RowScope.SearchKey(
    label: String,
    weight: Float = 1f,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.weight(weight).height(28.dp).clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(5.dp),
        tonalElevation = 1.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 2.dp),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = if (label.length > 1) 10.sp else 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
