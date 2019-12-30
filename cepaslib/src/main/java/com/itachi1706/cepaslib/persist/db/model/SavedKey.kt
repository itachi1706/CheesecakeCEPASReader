package com.itachi1706.cepaslib.persist.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.itachi1706.cepaslib.card.CardType
import java.util.Date

@Entity(tableName = "keys")
data class SavedKey(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "card_id") val cardId: String,
    @ColumnInfo(name = "card_type") val cardType: CardType,
    @ColumnInfo(name = "key_data") val keyData: String,
    @ColumnInfo(name = "created_at") val createdAt: Date = Date()
)
