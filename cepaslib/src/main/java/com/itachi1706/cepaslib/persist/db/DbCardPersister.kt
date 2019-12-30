package com.itachi1706.cepaslib.persist.db

import com.itachi1706.cepaslib.persist.CardPersister
import com.itachi1706.cepaslib.persist.db.model.SavedCard

class DbCardPersister(private val db: FareBotDb) : CardPersister {
    override fun getCards(): List<SavedCard> = db.savedCardDao().selectAll()
    override fun getCard(id: Long): SavedCard? = db.savedCardDao().selectById(id)
    override fun insertCard(savedCard: SavedCard): Long = db.savedCardDao().insert(savedCard)
    override fun deleteCard(savedCard: SavedCard) = db.savedCardDao().delete(savedCard)
}
