package com.itachi1706.cepaslib.persist.db

import com.itachi1706.cepaslib.persist.CardPersister
import com.itachi1706.cepaslib.persist.db.model.SavedCard

class DbCardPersister(private val db: FareBotDb) : CardPersister {
    override val cards: List<SavedCard>
        get() = db.savedCardDao().selectAll()

    override fun getCard(id: Long): SavedCard? = db.savedCardDao().selectById(id)
    override fun insertCard(card: SavedCard): Long = db.savedCardDao().insert(card)
    override fun deleteCard(card: SavedCard) = db.savedCardDao().delete(card)
}
