package com.mirxlab.termboard.ui.clipboard

import android.content.ClipboardManager
import android.content.Context
import com.mirxlab.termboard.data.AppDatabase
import com.mirxlab.termboard.data.ClipEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ClipboardManagerWrapper(private val context: Context) {

    private val clipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    private val db = AppDatabase.getDatabase(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    private val listener = ClipboardManager.OnPrimaryClipChangedListener {
        onPrimaryClipChanged()
    }

    fun startListening() {
        clipboardManager?.addPrimaryClipChangedListener(listener)
        // Also capture current primary clip on start
        onPrimaryClipChanged()
    }

    fun stopListening() {
        clipboardManager?.removePrimaryClipChangedListener(listener)
    }

    private fun onPrimaryClipChanged() {
        val clip = clipboardManager?.primaryClip
        if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).text?.toString()
            if (!text.isNullOrBlank()) {
                scope.launch {
                    db.clipboardDao().insertClip(ClipEntity(text = text))
                }
            }
        }
    }

    fun getAllClips(): Flow<List<ClipEntity>> {
        return db.clipboardDao().getAllClips()
    }

    fun deleteClip(clip: ClipEntity) {
        scope.launch {
            db.clipboardDao().deleteClip(clip)
        }
    }

    fun togglePin(clip: ClipEntity) {
        scope.launch {
            db.clipboardDao().updateClip(clip.copy(isPinned = !clip.isPinned))
        }
    }

    fun clearHistory() {
        scope.launch {
            db.clipboardDao().clearUnpinned()
        }
    }
}
