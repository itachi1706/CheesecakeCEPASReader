package com.itachi1706.cepaslib.persist.db

import com.itachi1706.cepaslib.persist.CardKeysPersister
import com.itachi1706.cepaslib.persist.db.model.SavedKey

class DbCardKeysPersister(private val db: FareBotDb) : CardKeysPersister {
    override fun getSavedKeys(): List<SavedKey> = db.savedKeyDao().selectAll()
    override fun getForTagId(tagId: String): SavedKey? = db.savedKeyDao().selectByCardId(tagId)
    override fun insert(savedKey: SavedKey): Long = db.savedKeyDao().insert(savedKey)
    override fun delete(savedKey: SavedKey) = db.savedKeyDao().delete(savedKey)
}
