package com.mirxlab.termboard.data.models

enum class KeyboardMode {
    STANDARD,
    SPLIT
}

data class KeyboardSettings(
    val mode: KeyboardMode = KeyboardMode.SPLIT,
    val compactStandardLayout: Boolean = true,
    val centerGapRatio: Float = 0.75f,
    val keyHeightDp: Int = 46,
    val keyboardHeightDp: Int = 300,
    val modifierHoldDurationMs: Int = 260,
    val keySpacingDp: Int = 3,
    val keyCornerRadiusDp: Int = 6,
    val translucencyAlpha: Float = 0.85f,
    val hapticEnabled: Boolean = true,
    val defaultSecondaryLayerId: String = "NAV",
    val darkTheme: Boolean = true,
    val autoSplitLandscape: Boolean = true
)
