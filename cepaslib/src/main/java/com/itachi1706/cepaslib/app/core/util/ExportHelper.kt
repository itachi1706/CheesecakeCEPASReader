/*
 * ExportHelper.kt
 *
 * This file is part of FareBot.
 * Learn more at: https://codebutler.github.io/farebot/
 *
 * Copyright (C) 2017 Eric Butler <eric@codebutler.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.itachi1706.cepaslib.app.core.util

import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import com.google.gson.Gson
import com.itachi1706.cepaslib.card.RawCard
import com.itachi1706.cepaslib.card.serialize.CardSerializer
import com.itachi1706.cepaslib.persist.CardPersister
import com.itachi1706.cepaslib.persist.db.model.SavedCard

class ExportHelper(
    private val cardPersister: CardPersister,
    private val cardSerializer: CardSerializer,
    private val gson: Gson
) {

    private fun getPackageInfo(ctx: Context?): PackageInfo? {
        return ctx?.packageManager?.getPackageInfo(ctx.packageName, 0)
    }

    @Suppress("DEPRECATION")
    private fun getVersionCode(pInfo: PackageInfo?): Long? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pInfo?.longVersionCode
        } else {
            pInfo?.versionCode?.toLong()
        }
    }

    fun exportCards(ctx: Context?): String = gson.toJson(Export(
            versionName = getPackageInfo(ctx)?.versionName ?: "Unknown",
            versionCode = getVersionCode(getPackageInfo(ctx)) ?: 0,
            cards = cardPersister.cards.map { cardSerializer.deserialize(it.data) }
    ))

    fun importCards(exportJsonString: String): List<Long> = gson.fromJson(exportJsonString, Export::class.java)
            .cards.map { cardPersister.insertCard(SavedCard(
            type = it.cardType(),
            serial = it.tagId().hex(),
            data = cardSerializer.serialize(it)))
    }

    private data class Export(
        val versionName: String,
        val versionCode: Long,
        val cards: List<RawCard<*>>
    )
}
