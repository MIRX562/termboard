package com.mirxlab.termboard.data.models

import kotlinx.serialization.Serializable

@Serializable
sealed class KeyAction {
    @Serializable
    data class Text(val text: String) : KeyAction()
    @Serializable
    data class KeyCode(val keyCode: Int, val metaState: Int = 0) : KeyAction()
    @Serializable
    data class ModifierKey(val modifier: ModifierType) : KeyAction()
    @Serializable
    data class LayerAction(val layerId: String, val mode: LayerActivationMode) : KeyAction()
    @Serializable
    data class MacroAction(val macroId: String) : KeyAction()
    @Serializable
    data class ComboAction(val comboId: String) : KeyAction()
    @Serializable
    object Backspace : KeyAction()
    @Serializable
    object Enter : KeyAction()
    @Serializable
    object Tab : KeyAction()
    @Serializable
    object Escape : KeyAction()
    @Serializable
    object Delete : KeyAction()
    @Serializable
    data class Arrow(val direction: ArrowDirection) : KeyAction()
    @Serializable
    object OpenClipboard : KeyAction()
    @Serializable
    object OpenEmoji : KeyAction()
    @Serializable
    object None : KeyAction()
}

@Serializable
enum class ModifierType {
    SHIFT,
    CTRL,
    ALT,
    META,
    FN,
    CAPS_LOCK
}

@Serializable
enum class LayerActivationMode {
    MOMENTARY,
    TOGGLE,
    ONE_SHOT
}

@Serializable
enum class ArrowDirection {
    LEFT, RIGHT, UP, DOWN
}
