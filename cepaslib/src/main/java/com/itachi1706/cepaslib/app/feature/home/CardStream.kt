/*
 * CardStream.kt
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

package com.itachi1706.cepaslib.app.feature.home

import com.itachi1706.cepaslib.app.core.app.FareBotApplication
import com.itachi1706.cepaslib.app.core.kotlin.Optional
import com.itachi1706.cepaslib.app.core.kotlin.filterAndGetOptional
import com.itachi1706.cepaslib.app.core.nfc.NfcStream
import com.itachi1706.cepaslib.app.core.nfc.TagReaderFactory
import com.itachi1706.cepaslib.card.RawCard
import com.itachi1706.cepaslib.card.serialize.CardSerializer
import com.itachi1706.cepaslib.persist.CardPersister
import com.itachi1706.cepaslib.persist.db.model.SavedCard
import com.jakewharton.rxrelay3.BehaviorRelay
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class CardStream(
    private val application: FareBotApplication,
    private val cardPersister: CardPersister,
    private val cardSerializer: CardSerializer,
    private val nfcStream: NfcStream,
    private val tagReaderFactory: TagReaderFactory
) {

    private val loadingRelay: BehaviorRelay<Boolean> = BehaviorRelay.createDefault(false)
    private val errorRelay: PublishRelay<Throwable> = PublishRelay.create()
    private val sampleRelay: PublishRelay<RawCard<*>> = PublishRelay.create()

    fun observeCards(): Observable<RawCard<*>> {
        val realCards = nfcStream.observe()
            .observeOn(Schedulers.io())
            .doOnNext { loadingRelay.accept(true) }
            .map { tag ->
                Optional(
                    try {
                        val rawCard = tagReaderFactory.getTagReader(tag.id, tag).readTag()
                        if (rawCard.isUnauthorized) {
                            throw CardUnauthorizedException()
                        }
                        rawCard
                    } catch (error: Throwable) {
                        errorRelay.accept(error)
                        loadingRelay.accept(false)
                        null
                    }
                )
            }
            .filterAndGetOptional()

        val sampleCards = sampleRelay
            .observeOn(Schedulers.io())
            .doOnNext { loadingRelay.accept(true) }
            .delay(3, TimeUnit.SECONDS)

        return Observable.merge(realCards, sampleCards)
            .doOnNext { card ->
                application.updateTimestamp(card.tagId().hex())
                cardPersister.insertCard(
                    SavedCard(
                        type = card.cardType(),
                        serial = card.tagId().hex(),
                        data = cardSerializer.serialize(card)
                    )
                )
            }
            .doOnNext { loadingRelay.accept(false) }
    }

    fun observeLoading(): Observable<Boolean> = loadingRelay.hide()

    fun observeErrors(): Observable<Throwable> = errorRelay.hide()

    class CardUnauthorizedException : Throwable() {
        override val message: String
            get() = "Unauthorized"
    }
}
