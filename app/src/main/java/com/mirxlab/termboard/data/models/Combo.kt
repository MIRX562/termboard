package com.mirxlab.termboard.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Combo(
    val id: String,
    val name: String,
    val keyIds: Set<String>,
    val resultAction: KeyAction,
    val timeoutMs: Long = 350L
)
