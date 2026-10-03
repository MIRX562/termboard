package com.mirxlab.termboard.data.models

data class KeyDefinition(
    val id: String,
    val baseLabel: String,
    val baseAction: KeyAction,
    val widthWeight: Float = 1f,
    val isSpecial: Boolean = false
)
