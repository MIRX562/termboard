package com.mirxlab.termboard.ime

import android.os.SystemClock
import android.view.KeyEvent
import android.view.inputmethod.InputConnection
import kotlin.math.abs

/**
 * Safe utility functions for interacting with [InputConnection].
 */
object InputConnectionUtils {

    fun commitText(ic: InputConnection?, text: CharSequence) {
        if (ic == null || text.isEmpty()) return
        ic.commitText(text, 1)
    }

    fun sendBackspace(ic: InputConnection?) {
        if (ic == null) return
        val selectedText = ic.getSelectedText(0)
        if (!selectedText.isNullOrEmpty()) {
            ic.commitText("", 1)
        } else {
            ic.deleteSurroundingText(1, 0)
        }
    }

    fun sendEnter(ic: InputConnection?) {
        if (ic == null) return
        sendDownUpKeyEvent(ic, KeyEvent.KEYCODE_ENTER)
    }

    fun sendTab(ic: InputConnection?) {
        if (ic == null) return
        sendDownUpKeyEvent(ic, KeyEvent.KEYCODE_TAB)
    }

    fun moveCursor(ic: InputConnection?, offset: Int) {
        if (ic == null || offset == 0) return
        val keyCode = if (offset < 0) KeyEvent.KEYCODE_DPAD_LEFT else KeyEvent.KEYCODE_DPAD_RIGHT
        val count = abs(offset)
        repeat(count) {
            sendDownUpKeyEvent(ic, keyCode)
        }
    }

    fun deleteSurroundingText(ic: InputConnection?, beforeLength: Int, afterLength: Int) {
        if (ic == null) return
        ic.deleteSurroundingText(beforeLength, afterLength)
    }

    fun getSelectedText(ic: InputConnection?): CharSequence? {
        return ic?.getSelectedText(0)
    }

    fun sendDownUpKeyEvent(ic: InputConnection?, keyCode: Int, metaState: Int = 0) {
        if (ic == null) return
        val eventTime = SystemClock.uptimeMillis()
        val downEvent = KeyEvent(
            eventTime, eventTime,
            KeyEvent.ACTION_DOWN, keyCode, 0, metaState
        )
        val upEvent = KeyEvent(
            eventTime, eventTime,
            KeyEvent.ACTION_UP, keyCode, 0, metaState
        )
        ic.sendKeyEvent(downEvent)
        ic.sendKeyEvent(upEvent)
    }

    fun sendSyntheticCombination(ic: InputConnection?, modifierMetaState: Int, keyCode: Int) {
        if (ic == null) return
        val eventTime = SystemClock.uptimeMillis()
        val downEvent = KeyEvent(
            eventTime, eventTime,
            KeyEvent.ACTION_DOWN, keyCode, 0, modifierMetaState
        )
        val upEvent = KeyEvent(
            eventTime, eventTime,
            KeyEvent.ACTION_UP, keyCode, 0, modifierMetaState
        )
        ic.sendKeyEvent(downEvent)
        ic.sendKeyEvent(upEvent)
    }
}
