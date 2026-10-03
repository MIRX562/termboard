package com.mirxlab.termboard.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClipboardDao {

    @Query("SELECT * FROM clipboard_history ORDER BY isPinned DESC, timestamp DESC")
    fun getAllClips(): Flow<List<ClipEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertClip(clip: ClipEntity): Long

    @Delete
    fun deleteClip(clip: ClipEntity)

    @Query("DELETE FROM clipboard_history WHERE id = :id")
    fun deleteById(id: Long)

    @Query("DELETE FROM clipboard_history WHERE isPinned = 0")
    fun clearUnpinned()

    @Update
    fun updateClip(clip: ClipEntity)
}
