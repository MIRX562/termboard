package com.mirxlab.termboard.engine

import android.view.KeyEvent
import android.view.inputmethod.InputConnection
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.mirxlab.termboard.data.models.ArrowDirection
import com.mirxlab.termboard.data.models.Combo
import com.mirxlab.termboard.data.models.KeyAction
import com.mirxlab.termboard.data.models.KeyDefinition
import com.mirxlab.termboard.data.models.KeyboardSettings
import com.mirxlab.termboard.data.models.Layer
import com.mirxlab.termboard.data.models.LayerActivationMode
import com.mirxlab.termboard.data.models.Macro
import com.mirxlab.termboard.data.models.ModifierState
import com.mirxlab.termboard.data.models.ModifierStatus
import com.mirxlab.termboard.data.models.ModifierType
import com.mirxlab.termboard.ime.InputConnectionUtils
import com.mirxlab.termboard.ime.InputMode

class KeyboardEngine(
    initialSettings: KeyboardSettings = KeyboardSettings()
) {

    private val _settings = mutableStateOf(initialSettings)
    val settings: State<KeyboardSettings> get() = _settings

    private val _layers = mutableStateOf(KeyboardLayouts.defaultLayers)
    val layers: State<Map<String, Layer>> get() = _layers

    private val _activeLayerId = mutableStateOf("BASE")
    val activeLayerId: State<String> get() = _activeLayerId

    private val _modifierState = mutableStateOf(ModifierState())
    val modifierState: State<ModifierState> get() = _modifierState

    private val _combos = mutableStateOf<List<Combo>>(emptyList())
    val combos: State<List<Combo>> get() = _combos

    private val _macros = mutableStateOf<Map<String, Macro>>(emptyMap())
    val macros: State<Map<String, Macro>> get() = _macros

    private val pressedKeyTimes = mutableMapOf<String, Long>()
    private val pendingCombosByKey = mutableMapOf<String, Combo>()
    private val triggeredComboIds = mutableSetOf<String>()
    private val activeKeyIds = mutableSetOf<String>()
    private val heldModifierSources = mutableMapOf<ModifierType, MutableSet<String>>()
    private val modifierStatusBeforeHold = mutableMapOf<ModifierType, ModifierStatus>()
    private val heldModifiersByKey = mutableMapOf<String, ModifierType>()
    private val previousLayerByHeldKey = mutableMapOf<String, String>()
    private val heldLayerIdsByKey = mutableMapOf<String, String>()
    private var inputModeHandler: ((InputMode) -> Unit)? = null

    companion object {
        const val MAX_ACTIVE_KEYS = 5
    }

    fun updateSettings(newSettings: KeyboardSettings) {
        _settings.value = newSettings
    }

    fun resetAfterExternalTextCommit() {
        consumeOneShotModifiers()
        _modifierState.value = _modifierState.value.copy(shift = ModifierStatus.INACTIVE)
    }

    fun setLayers(newLayers: Map<String, Layer>) {
        _layers.value = newLayers
        if (_activeLayerId.value !in newLayers) _activeLayerId.value = "BASE"
    }

    fun setCombos(newCombos: List<Combo>) {
        _combos.value = newCombos
    }

    fun setMacros(newMacros: List<Macro>) {
        _macros.value = newMacros.associateBy { it.id }
    }

    fun setInputModeHandler(handler: (InputMode) -> Unit) {
        inputModeHandler = handler
    }

    fun onKeyDown(keyDef: KeyDefinition): Boolean {
        if (keyDef.id !in activeKeyIds && activeKeyIds.size >= MAX_ACTIVE_KEYS) return false
        activeKeyIds.add(keyDef.id)
        pressedKeyTimes[keyDef.id] = System.currentTimeMillis()
        val combo = _combos.value.firstOrNull { candidate ->
            val pressTimes = candidate.keyIds.mapNotNull(pressedKeyTimes::get)
            candidate.keyIds.size > 1 &&
                candidate.keyIds.none { it in pendingCombosByKey } &&
                pressTimes.size == candidate.keyIds.size &&
                pressTimes.maxOrNull()!! - pressTimes.minOrNull()!! <= candidate.timeoutMs
        }

        if (combo != null) combo.keyIds.forEach { keyId -> pendingCombosByKey[keyId] = combo }
        return true
    }

    fun onKeyHold(keyDef: KeyDefinition): Boolean {
        if (keyDef.id !in activeKeyIds) return false

        return when (val action = resolveAction(keyDef)) {
            is KeyAction.ModifierKey -> {
                if (action.modifier == ModifierType.CAPS_LOCK) return false
                val sources = heldModifierSources.getOrPut(action.modifier) { mutableSetOf() }
                if (sources.isEmpty()) {
                    modifierStatusBeforeHold[action.modifier] = modifierStatus(action.modifier)
                }
                sources.add(keyDef.id)
                heldModifiersByKey[keyDef.id] = action.modifier
                setModifierStatus(action.modifier, ModifierStatus.HELD)
                if (action.modifier == ModifierType.FN) {
                    val layerId = _settings.value.defaultSecondaryLayerId
                    previousLayerByHeldKey[keyDef.id] = _activeLayerId.value
                    heldLayerIdsByKey[keyDef.id] = layerId
                    _activeLayerId.value = layerId
                }
                true
            }
            is KeyAction.LayerAction -> {
                if (action.mode != LayerActivationMode.MOMENTARY) return false
                previousLayerByHeldKey[keyDef.id] = _activeLayerId.value
                heldLayerIdsByKey[keyDef.id] = action.layerId
                _activeLayerId.value = action.layerId
                true
            }
            else -> false
        }
    }

    fun onKeyUp(
        keyDef: KeyDefinition,
        wasReleased: Boolean,
        wasHeld: Boolean = false,
        ic: InputConnection? = null
    ): Boolean {
        activeKeyIds.remove(keyDef.id)
        pressedKeyTimes.remove(keyDef.id)
        heldModifiersByKey.remove(keyDef.id)?.let { modifier ->
            val sources = heldModifierSources[modifier]
            sources?.remove(keyDef.id)
            if (sources.isNullOrEmpty()) {
                heldModifierSources.remove(modifier)
                val previousStatus = modifierStatusBeforeHold.remove(modifier) ?: ModifierStatus.INACTIVE
                setModifierStatus(modifier, previousStatus)
            }
        }
        val heldLayerId = heldLayerIdsByKey.remove(keyDef.id)
        previousLayerByHeldKey.remove(keyDef.id)?.let { previousLayer ->
            if (_activeLayerId.value == heldLayerId) _activeLayerId.value = previousLayer
        }
        val pendingCombo = pendingCombosByKey.remove(keyDef.id)
        val wasComboConsumed = pendingCombo != null
        if (pendingCombo != null) {
            if (wasReleased && triggeredComboIds.add(pendingCombo.id)) {
                executeAction(pendingCombo.resultAction, ic)
                consumeOneShotModifiers()
            }
            if (!wasReleased) {
                pendingCombo.keyIds.forEach(pendingCombosByKey::remove)
            }
            if (pendingCombo.keyIds.none { it in pendingCombosByKey }) {
                triggeredComboIds.remove(pendingCombo.id)
            }
        }
        return wasReleased && (wasComboConsumed || wasHeld)
    }

    fun setActiveLayer(layerId: String) {
        if (_layers.value.containsKey(layerId)) {
            _activeLayerId.value = layerId
        }
    }

    fun resolveAction(keyDef: KeyDefinition): KeyAction {
        val currentLayer = _layers.value[_activeLayerId.value]
        return currentLayer?.keyMappings?.get(keyDef.id) ?: keyDef.baseAction
    }

    fun onKeyPress(keyDef: KeyDefinition, ic: InputConnection?) {
        val action = resolveAction(keyDef)
        executeAction(action, ic)
    }

    fun executeAction(action: KeyAction, ic: InputConnection?) {
        when (action) {
            is KeyAction.Text -> {
                var textToCommit = action.text
                if (_modifierState.value.isShiftActive()) {
                    textToCommit = textToCommit.uppercase()
                }

                if (_modifierState.value.isCtrlActive() || _modifierState.value.isAltActive()) {
                    val char = textToCommit.firstOrNull()
                    if (char != null) {
                        val keyCode = charToKeyCode(char)
                        if (keyCode != KeyEvent.KEYCODE_UNKNOWN) {
                            InputConnectionUtils.sendDownUpKeyEvent(
                                ic,
                                keyCode,
                                _modifierState.value.getAndroidMetaState()
                            )
                        } else {
                            InputConnectionUtils.commitText(ic, textToCommit)
                        }
                    } else {
                        InputConnectionUtils.commitText(ic, textToCommit)
                    }
                } else {
                    InputConnectionUtils.commitText(ic, textToCommit)
                }

                consumeOneShotModifiers()
            }

            is KeyAction.KeyCode -> {
                InputConnectionUtils.sendDownUpKeyEvent(
                    ic,
                    action.keyCode,
                    action.metaState or _modifierState.value.getAndroidMetaState()
                )
                consumeOneShotModifiers()
            }

            is KeyAction.ModifierKey -> {
                if (modifierStatus(action.modifier) != ModifierStatus.HELD) {
                    toggleModifier(action.modifier)
                }
            }

            is KeyAction.LayerAction -> {
                when (action.mode) {
                    LayerActivationMode.MOMENTARY -> {
                        _activeLayerId.value = if (_activeLayerId.value == action.layerId) "BASE" else action.layerId
                    }
                    LayerActivationMode.TOGGLE -> {
                        _activeLayerId.value = if (_activeLayerId.value == action.layerId) "BASE" else action.layerId
                    }
                    LayerActivationMode.ONE_SHOT -> {
                        _activeLayerId.value = action.layerId
                    }
                }
            }

            is KeyAction.MacroAction -> {
                val macro = _macros.value[action.macroId]
                macro?.steps?.forEach { step ->
                    executeAction(step, ic)
                }
            }

            is KeyAction.ComboAction -> {
                _combos.value.firstOrNull { it.id == action.comboId }?.let { combo ->
                    executeAction(combo.resultAction, ic)
                }
            }

            KeyAction.Backspace -> {
                InputConnectionUtils.sendBackspace(ic)
                consumeOneShotModifiers()
            }

            KeyAction.Enter -> {
                InputConnectionUtils.sendEnter(ic)
                consumeOneShotModifiers()
            }

            KeyAction.Tab -> {
                InputConnectionUtils.sendTab(ic)
                consumeOneShotModifiers()
            }

            KeyAction.Escape -> {
                InputConnectionUtils.sendDownUpKeyEvent(
                    ic,
                    KeyEvent.KEYCODE_ESCAPE,
                    _modifierState.value.getAndroidMetaState()
                )
                consumeOneShotModifiers()
            }

            KeyAction.Delete -> {
                InputConnectionUtils.sendDownUpKeyEvent(
                    ic,
                    KeyEvent.KEYCODE_FORWARD_DEL,
                    _modifierState.value.getAndroidMetaState()
                )
                consumeOneShotModifiers()
            }

            is KeyAction.Arrow -> {
                val keyCode = when (action.direction) {
                    ArrowDirection.LEFT -> KeyEvent.KEYCODE_DPAD_LEFT
                    ArrowDirection.RIGHT -> KeyEvent.KEYCODE_DPAD_RIGHT
                    ArrowDirection.UP -> KeyEvent.KEYCODE_DPAD_UP
                    ArrowDirection.DOWN -> KeyEvent.KEYCODE_DPAD_DOWN
                }
                InputConnectionUtils.sendDownUpKeyEvent(
                    ic,
                    keyCode,
                    _modifierState.value.getAndroidMetaState()
                )
                consumeOneShotModifiers()
            }

            KeyAction.OpenClipboard -> {
                inputModeHandler?.invoke(InputMode.CLIPBOARD)
            }

            KeyAction.OpenEmoji -> {
                inputModeHandler?.invoke(InputMode.EMOJI)
            }

            KeyAction.None -> {}
        }
    }

    private fun toggleModifier(modifier: ModifierType) {
        val current = _modifierState.value
        _modifierState.value = when (modifier) {
            ModifierType.SHIFT -> current.copy(
                shift = nextModifierStatus(current.shift)
            )
            ModifierType.CTRL -> current.copy(
                ctrl = nextModifierStatus(current.ctrl)
            )
            ModifierType.ALT -> current.copy(
                alt = nextModifierStatus(current.alt)
            )
            ModifierType.META -> current.copy(
                meta = nextModifierStatus(current.meta)
            )
            ModifierType.FN -> {
                val newFnStatus = nextModifierStatus(current.fn)
                val targetLayer = if (newFnStatus != ModifierStatus.INACTIVE) _settings.value.defaultSecondaryLayerId else "BASE"
                _activeLayerId.value = targetLayer
                current.copy(fn = newFnStatus)
            }
            ModifierType.CAPS_LOCK -> current.copy(
                capsLock = if (current.capsLock == ModifierStatus.INACTIVE) ModifierStatus.LOCKED else ModifierStatus.INACTIVE
            )
        }
    }

    private fun modifierStatus(modifier: ModifierType): ModifierStatus = when (modifier) {
        ModifierType.SHIFT -> _modifierState.value.shift
        ModifierType.CTRL -> _modifierState.value.ctrl
        ModifierType.ALT -> _modifierState.value.alt
        ModifierType.META -> _modifierState.value.meta
        ModifierType.FN -> _modifierState.value.fn
        ModifierType.CAPS_LOCK -> _modifierState.value.capsLock
    }

    private fun setModifierStatus(modifier: ModifierType, status: ModifierStatus) {
        val current = _modifierState.value
        _modifierState.value = when (modifier) {
            ModifierType.SHIFT -> current.copy(shift = status)
            ModifierType.CTRL -> current.copy(ctrl = status)
            ModifierType.ALT -> current.copy(alt = status)
            ModifierType.META -> current.copy(meta = status)
            ModifierType.FN -> current.copy(fn = status)
            ModifierType.CAPS_LOCK -> current.copy(capsLock = status)
        }
    }

    private fun nextModifierStatus(status: ModifierStatus): ModifierStatus {
        return when (status) {
            ModifierStatus.INACTIVE -> ModifierStatus.ONE_SHOT
            ModifierStatus.ONE_SHOT -> ModifierStatus.LOCKED
            ModifierStatus.HELD -> ModifierStatus.INACTIVE
            ModifierStatus.LOCKED -> ModifierStatus.INACTIVE
        }
    }

    private fun consumeOneShotModifiers() {
        val current = _modifierState.value
        var updated = current

        if (current.shift == ModifierStatus.ONE_SHOT) {
            updated = updated.copy(shift = ModifierStatus.INACTIVE)
        }
        if (current.ctrl == ModifierStatus.ONE_SHOT) {
            updated = updated.copy(ctrl = ModifierStatus.INACTIVE)
        }
        if (current.alt == ModifierStatus.ONE_SHOT) {
            updated = updated.copy(alt = ModifierStatus.INACTIVE)
        }
        if (current.meta == ModifierStatus.ONE_SHOT) {
            updated = updated.copy(meta = ModifierStatus.INACTIVE)
        }
        if (current.fn == ModifierStatus.ONE_SHOT) {
            updated = updated.copy(fn = ModifierStatus.INACTIVE)
            _activeLayerId.value = "BASE"
        }

        _modifierState.value = updated
    }

    private fun charToKeyCode(c: Char): Int {
        val uppercase = c.uppercaseChar()
        return when (uppercase) {
            in 'A'..'Z' -> KeyEvent.KEYCODE_A + (uppercase - 'A')
            in '0'..'9' -> KeyEvent.KEYCODE_0 + (uppercase - '0')
            ' ' -> KeyEvent.KEYCODE_SPACE
            '\n' -> KeyEvent.KEYCODE_ENTER
            '\t' -> KeyEvent.KEYCODE_TAB
            else -> KeyEvent.KEYCODE_UNKNOWN
        }
    }
}
