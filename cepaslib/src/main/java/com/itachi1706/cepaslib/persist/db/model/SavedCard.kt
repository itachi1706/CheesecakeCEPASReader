package com.itachi1706.cepaslib.persist.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.itachi1706.cepaslib.card.CardType
import java.util.Date

@Entity(tableName = "cards")
data class SavedCard(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "type") val type: CardType,
    @ColumnInfo(name = "serial") val serial: String,
    @ColumnInfo(name = "data") val data: String,
    @ColumnInfo(name = "scanned_at") val scannedAt: Date = Date()
)
