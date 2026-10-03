package com.mirxlab.termboard.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Macro(
    val id: String,
    val name: String,
    val steps: List<KeyAction>
)
