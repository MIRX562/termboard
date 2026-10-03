package com.mirxlab.termboard.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.mirxlab.termboard.data.models.ModifierStatus
import com.mirxlab.termboard.data.models.ArrowDirection
import com.mirxlab.termboard.engine.KeyActionLabel
import com.mirxlab.termboard.engine.KeyLegendIcon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RowScope.QmkKeyButton(
    modifier: Modifier = Modifier,
    primaryLabel: String? = null,
    secondaryLabel: String? = null,
    icon: ImageVector? = null,
    weight: Float = 1f,
    height: Dp = 46.dp,
    modifierStatus: ModifierStatus = ModifierStatus.INACTIVE,
    isSpecialKey: Boolean = false,
    hapticEnabled: Boolean = true,
    alpha: Float = 0.85f,
    onClick: () -> Unit,
    popupOptions: List<KeyActionLabel> = emptyList(),
    legendIcon: KeyLegendIcon? = null,
    onPopupOptionSelected: (KeyActionLabel) -> Unit = {},
    onSwipe: ((SwipeDirection) -> Unit)? = null,
    holdDurationMs: Int = 260,
    onHold: (() -> Boolean)? = null,
    onPressStart: () -> Boolean = { true },
    onPressEnd: (Boolean, Boolean) -> Boolean = { _, _ -> false },
    onLongClick: (() -> Unit)? = null,
    onRepeatClick: (() -> Unit)? = null
) {
    val view = LocalView.current
    val currentOnSwipe by rememberUpdatedState(onSwipe)
    val currentOnHold by rememberUpdatedState(onHold)
    val density = LocalDensity.current
    val swipeThreshold = with(density) { 18.dp.toPx() }
    val popupOffset = with(density) { IntOffset(0, -56.dp.roundToPx()) }
    val scope = rememberCoroutineScope()
    var isPressed by remember { mutableStateOf(false) }
    var suppressClick by remember { mutableStateOf(false) }
    var showPopup by remember { mutableStateOf(false) }
    var holdActivated by remember { mutableStateOf(false) }
    var pressAccepted by remember { mutableStateOf(true) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        label = "qmkKeyScale"
    )

    val baseKeyColor = if (isSpecialKey) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }

    val containerColor = when (modifierStatus) {
        ModifierStatus.LOCKED -> MaterialTheme.colorScheme.primary
        ModifierStatus.ONE_SHOT -> MaterialTheme.colorScheme.tertiary
        ModifierStatus.HELD -> MaterialTheme.colorScheme.secondary
        ModifierStatus.INACTIVE -> if (isPressed) MaterialTheme.colorScheme.primaryContainer else baseKeyColor.copy(alpha = alpha)
    }

    val contentColor = when (modifierStatus) {
        ModifierStatus.LOCKED -> MaterialTheme.colorScheme.onPrimary
        ModifierStatus.ONE_SHOT -> MaterialTheme.colorScheme.onTertiary
        ModifierStatus.HELD -> MaterialTheme.colorScheme.onSecondary
        ModifierStatus.INACTIVE -> MaterialTheme.colorScheme.onSurface
    }

    val borderColor = if (modifierStatus != ModifierStatus.INACTIVE) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
    }

    Box(
        modifier = modifier
            .weight(weight)
            .height(height)
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .scale(scale)
            .clip(RoundedCornerShape(6.dp))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .pointerInput(onSwipe != null) {
                if (onSwipe != null) {
                    var horizontalRemainder = 0f
                    var verticalRemainder = 0f
                    detectDragGestures(
                        onDragStart = {
                            horizontalRemainder = 0f
                            verticalRemainder = 0f
                        },
                        onDragEnd = {
                            horizontalRemainder = 0f
                            verticalRemainder = 0f
                        },
                        onDragCancel = {
                            horizontalRemainder = 0f
                            verticalRemainder = 0f
                        },
                        onDrag = { change, dragAmount ->
                            horizontalRemainder += dragAmount.x
                            verticalRemainder += dragAmount.y
                            while (horizontalRemainder >= swipeThreshold) {
                                currentOnSwipe?.invoke(SwipeDirection.RIGHT)
                                horizontalRemainder -= swipeThreshold
                            }
                            while (horizontalRemainder <= -swipeThreshold) {
                                currentOnSwipe?.invoke(SwipeDirection.LEFT)
                                horizontalRemainder += swipeThreshold
                            }
                            while (verticalRemainder >= swipeThreshold) {
                                currentOnSwipe?.invoke(SwipeDirection.DOWN)
                                verticalRemainder -= swipeThreshold
                            }
                            while (verticalRemainder <= -swipeThreshold) {
                                currentOnSwipe?.invoke(SwipeDirection.UP)
                                verticalRemainder += swipeThreshold
                            }
                            change.consume()
                        }
                    )
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        suppressClick = false
                        holdActivated = false
                        pressAccepted = onPressStart()
                        if (hapticEnabled) {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        }

                        val holdJob = if (onHold != null && pressAccepted) {
                            scope.launch {
                                delay(holdDurationMs.toLong())
                                if (isPressed && currentOnHold?.invoke() == true) {
                                    holdActivated = true
                                }
                            }
                        } else null

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

                        val wasReleased = tryAwaitRelease()
                        suppressClick = onPressEnd(wasReleased, holdActivated)
                        isPressed = false
                        holdJob?.cancel()
                        repeatJob?.cancel()
                    },
                    onTap = {
                        if (suppressClick || holdActivated || !pressAccepted) {
                            suppressClick = false
                        } else {
                            onClick()
                        }
                    },
                    onLongPress = {
                        if (pressAccepted && onHold == null && popupOptions.isNotEmpty()) {
                            if (hapticEnabled) {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            }
                            showPopup = true
                        } else if (pressAccepted && onHold == null && onLongClick != null) {
                            if (hapticEnabled) {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            }
                            onLongClick()
                        }
                        suppressClick = false
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = primaryLabel ?: "Key",
                tint = contentColor,
                modifier = Modifier.fillMaxSize(0.48f)
            )
        } else {
            AdaptiveKeyLegend(
                primaryLabel = primaryLabel,
                secondaryLabel = secondaryLabel,
                primaryColor = contentColor,
                secondaryColor = contentColor.copy(alpha = 0.68f),
                primaryWeight = if (isSpecialKey || modifierStatus != ModifierStatus.INACTIVE) FontWeight.Bold else FontWeight.Medium,
                legendIcon = legendIcon
            )
        }
    }

    if (showPopup && popupOptions.isNotEmpty()) {
        Popup(
            onDismissRequest = { showPopup = false },
            alignment = Alignment.TopCenter,
            offset = popupOffset
        ) {
            Surface(
                color = MaterialTheme.colorScheme.inverseSurface,
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 8.dp
            ) {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(2.dp)
                ) {
                    popupOptions.forEach { option ->
                        TextButton(
                            onClick = {
                                onPopupOptionSelected(option)
                                showPopup = false
                            }
                        ) {
                            Text(option.label, color = MaterialTheme.colorScheme.inverseOnSurface)
                        }
                    }
                }
            }
        }
    }
}

enum class SwipeDirection {
    LEFT,
    DOWN,
    UP,
    RIGHT
}

fun SwipeDirection.toArrowDirection(): ArrowDirection = when (this) {
    SwipeDirection.LEFT -> ArrowDirection.LEFT
    SwipeDirection.DOWN -> ArrowDirection.DOWN
    SwipeDirection.UP -> ArrowDirection.UP
    SwipeDirection.RIGHT -> ArrowDirection.RIGHT
}
