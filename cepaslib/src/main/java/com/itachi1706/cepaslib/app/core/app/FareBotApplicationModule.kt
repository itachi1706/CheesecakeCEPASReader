/*
 * FareBotApplicationModule.kt
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

package com.itachi1706.cepaslib.app.core.app

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.itachi1706.cepaslib.app.core.nfc.TagReaderFactory
import com.itachi1706.cepaslib.app.core.serialize.CardKeysSerializer
import com.itachi1706.cepaslib.app.core.serialize.gson.*
import com.itachi1706.cepaslib.app.core.transit.TransitFactoryRegistry
import com.itachi1706.cepaslib.app.core.util.ExportHelper
import com.itachi1706.cepaslib.base.util.ByteArray
import com.itachi1706.cepaslib.card.CardType
import com.itachi1706.cepaslib.card.cepas.CEPASTypeAdapterFactory
import com.itachi1706.cepaslib.card.serialize.CardSerializer
import com.itachi1706.cepaslib.persist.CardKeysPersister
import com.itachi1706.cepaslib.persist.CardPersister
import com.itachi1706.cepaslib.persist.db.DbCardKeysPersister
import com.itachi1706.cepaslib.persist.db.DbCardPersister
import com.itachi1706.cepaslib.persist.db.FareBotDb
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import java.util.*

@Module
class FareBotApplicationModule {

    @Provides
    fun provideSharedPreferences(application: FareBotApplication): SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(application)

    @Provides
    fun provideGson(): Gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, EpochDateTypeAdapter())
            .registerTypeAdapterFactory(CEPASTypeAdapterFactory.create())
            .registerTypeAdapterFactory(RawCardGsonTypeAdapterFactory())
            .registerTypeAdapter(ByteArray::class.java, ByteArrayGsonTypeAdapter())
            .registerTypeAdapter(CardType::class.java, CardTypeGsonTypeAdapter())
            .create()

    @Provides
    fun provideCardSerializer(gson: Gson): CardSerializer = GsonCardSerializer(gson)

    @Provides
    fun provideCardKeysSerializer(gson: Gson): CardKeysSerializer = GsonCardKeysSerializer(gson)

    @Provides
    fun provideFareBotDb(application: FareBotApplication): FareBotDb = FareBotDb.getInstance(application)

    @Provides
    fun provideCardPersister(db: FareBotDb): CardPersister = DbCardPersister(db)

    @Provides
    fun provideCardKeysPersister(db: FareBotDb): CardKeysPersister = DbCardKeysPersister(db)

    @Provides
    fun provideExportHelper(cardPersister: CardPersister, cardSerializer: CardSerializer, gson: Gson): ExportHelper =
            ExportHelper(cardPersister, cardSerializer, gson)

    @Provides
    fun provideTagReaderFactory(): TagReaderFactory {
        return TagReaderFactory()
    }

    @Provides
    fun provideTransitFactoryRegistry(): TransitFactoryRegistry =
            TransitFactoryRegistry()
}
