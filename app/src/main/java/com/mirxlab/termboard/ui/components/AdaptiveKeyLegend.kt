package com.mirxlab.termboard.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.automirrored.filled.KeyboardTab
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.KeyboardCommandKey
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirxlab.termboard.engine.KeyLegendIcon

@Composable
fun AdaptiveKeyLegend(
    primaryLabel: String?,
    secondaryLabel: String?,
    modifier: Modifier = Modifier,
    primaryColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    secondaryColor: androidx.compose.ui.graphics.Color = primaryColor.copy(alpha = 0.7f),
    primaryWeight: FontWeight = FontWeight.Medium,
    legendIcon: KeyLegendIcon? = null
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val keyWidth = maxWidth
        val label = primaryLabel.orEmpty()
        val primarySize = if (label.isBlank()) 0.sp else {
            (maxWidth.value * 1.55f / label.length.coerceAtLeast(1)).coerceIn(8f, 18f).sp
        }
        if (legendIcon == KeyLegendIcon.SPACE) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size((keyWidth.value * 0.2f).coerceIn(9f, 13f).dp),
                    tint = secondaryColor
                )
                Icon(
                    imageVector = Icons.Filled.SpaceBar,
                    contentDescription = "Space, swipe for cursor movement",
                    modifier = Modifier.size((keyWidth.value * 0.42f).coerceIn(16f, 26f).dp),
                    tint = primaryColor
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size((keyWidth.value * 0.2f).coerceIn(9f, 13f).dp),
                    tint = secondaryColor
                )
            }
        } else if (legendIcon != null) {
            val (image, description) = when (legendIcon) {
                KeyLegendIcon.TAB -> Icons.AutoMirrored.Filled.KeyboardTab to "Tab"
                KeyLegendIcon.SHIFT -> Icons.Filled.ArrowUpward to "Shift"
                KeyLegendIcon.META -> Icons.Filled.KeyboardCommandKey to "Meta"
                KeyLegendIcon.SPACE -> Icons.Filled.SpaceBar to "Space"
                KeyLegendIcon.ENTER -> Icons.AutoMirrored.Filled.KeyboardReturn to "Enter"
                KeyLegendIcon.BACKSPACE -> Icons.AutoMirrored.Filled.Backspace to "Backspace"
            }
            Icon(
                imageVector = image,
                contentDescription = description,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size((maxWidth.value * 0.62f).coerceIn(12f, 24f).dp),
                tint = primaryColor
            )
        } else if (label.isNotBlank()) {
            Text(
                text = label,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = if (secondaryLabel.isNullOrBlank()) 2.dp else 7.dp),
                color = primaryColor,
                fontSize = primarySize,
                fontWeight = primaryWeight,
                maxLines = 1,
                softWrap = false
            )
        }
        if (!secondaryLabel.isNullOrBlank() && secondaryLabel != primaryLabel) {
            Text(
                text = secondaryLabel,
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 1.dp, end = 3.dp),
                color = secondaryColor,
                fontSize = (maxWidth.value * 0.25f).coerceIn(7f, 9f).sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}
