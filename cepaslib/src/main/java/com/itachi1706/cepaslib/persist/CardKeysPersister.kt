package com.itachi1706.cepaslib.persist

import com.itachi1706.cepaslib.persist.db.model.SavedKey

interface CardKeysPersister {
    val savedKeys: List<SavedKey?>
    fun getForTagId(tagId: String): SavedKey?
    fun insert(savedKey: SavedKey): Long
    fun delete(savedKey: SavedKey)
}