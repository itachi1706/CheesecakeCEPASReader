package com.itachi1706.cepaslib.persist.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.itachi1706.cepaslib.persist.db.model.SavedCard

@Dao
interface SavedCardDao {
    @Query("SELECT * FROM cards ORDER BY scanned_at DESC")
    fun selectAll(): List<SavedCard>

    @Query("SELECT * FROM cards WHERE id = :id")
    fun selectById(id: Long): SavedCard?

    @Insert
    fun insert(savedCard: SavedCard): Long

    @Delete
    fun delete(savedCard: SavedCard)
}
