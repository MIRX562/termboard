package com.mirxlab.termboard.data.models

import android.view.KeyEvent

enum class ModifierStatus {
    INACTIVE,
    ONE_SHOT,
    HELD,
    LOCKED
}

data class ModifierState(
    val shift: ModifierStatus = ModifierStatus.INACTIVE,
    val ctrl: ModifierStatus = ModifierStatus.INACTIVE,
    val alt: ModifierStatus = ModifierStatus.INACTIVE,
    val meta: ModifierStatus = ModifierStatus.INACTIVE,
    val fn: ModifierStatus = ModifierStatus.INACTIVE,
    val capsLock: ModifierStatus = ModifierStatus.INACTIVE
) {
    fun isShiftActive(): Boolean =
        (shift != ModifierStatus.INACTIVE) xor (capsLock != ModifierStatus.INACTIVE)
    fun isCtrlActive(): Boolean = ctrl != ModifierStatus.INACTIVE
    fun isAltActive(): Boolean = alt != ModifierStatus.INACTIVE
    fun isMetaActive(): Boolean = meta != ModifierStatus.INACTIVE
    fun isFnActive(): Boolean = fn != ModifierStatus.INACTIVE
    fun isCapsLockActive(): Boolean = capsLock != ModifierStatus.INACTIVE

    fun getAndroidMetaState(): Int {
        var metaState = 0
        if (isCtrlActive()) metaState = metaState or KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON
        if (isAltActive()) metaState = metaState or KeyEvent.META_ALT_ON or KeyEvent.META_ALT_LEFT_ON
        if (shift != ModifierStatus.INACTIVE) metaState = metaState or KeyEvent.META_SHIFT_ON or KeyEvent.META_SHIFT_LEFT_ON
        if (isCapsLockActive()) metaState = metaState or KeyEvent.META_CAPS_LOCK_ON
        if (isMetaActive()) metaState = metaState or KeyEvent.META_META_ON or KeyEvent.META_META_LEFT_ON
        return metaState
    }
}
