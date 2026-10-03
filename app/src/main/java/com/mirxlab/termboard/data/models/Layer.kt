package com.mirxlab.termboard.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Layer(
    val id: String,
    val name: String,
    val displayName: String,
    val description: String = "",
    val icon: String? = null,
    val keyMappings: Map<String, KeyAction> = emptyMap()
)
