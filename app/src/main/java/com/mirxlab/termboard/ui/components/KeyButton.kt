package com.mirxlab.termboard.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RowScope.KeyButton(
    modifier: Modifier = Modifier,
    label: String? = null,
    icon: ImageVector? = null,
    weight: Float = 1f,
    height: Dp = 48.dp,
    isActivated: Boolean = false,
    isSpecialKey: Boolean = false,
    hapticEnabled: Boolean = true,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    onRepeatClick: (() -> Unit)? = null
) {
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        label = "keyScale"
    )

    val containerColor = when {
        isActivated -> MaterialTheme.colorScheme.primary
        isPressed -> MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.4f)
        isSpecialKey -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }

    val contentColor = when {
        isActivated -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .weight(weight)
            .height(height)
            .padding(horizontal = 2.dp, vertical = 3.dp)
            .scale(scale)
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        if (hapticEnabled) {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        }

                        // Auto-repeat task if provided (e.g. for backspace)
                        val repeatJob = if (onRepeatClick != null) {
                            scope.launch {
                                delay(400)
                                while (isPressed) {
                                    onRepeatClick()
                                    if (hapticEnabled) {
                                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    }
                                    delay(50)
                                }
                            }
                        } else null

                        tryAwaitRelease()
                        isPressed = false
                        repeatJob?.cancel()
                    },
                    onTap = {
                        onClick()
                    },
                    onLongPress = {
                        if (onLongClick != null) {
                            if (hapticEnabled) {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            }
                            onLongClick()
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = label ?: "Key",
                tint = contentColor,
                modifier = Modifier.fillMaxSize(0.5f)
            )
        } else if (label != null) {
            Text(
                text = label,
                fontSize = if (label.length > 2) 13.sp else 18.sp,
                fontWeight = if (isSpecialKey || isActivated) FontWeight.Bold else FontWeight.Normal,
                color = contentColor
            )
        }
    }
}
