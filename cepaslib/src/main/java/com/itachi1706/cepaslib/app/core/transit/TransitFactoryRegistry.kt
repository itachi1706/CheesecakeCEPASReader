/*
 * TransitFactoryRegistry.kt
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

package com.itachi1706.cepaslib.app.core.transit

import com.itachi1706.cepaslib.card.Card
import com.itachi1706.cepaslib.card.cepas.CEPASCard
import com.itachi1706.cepaslib.transit.TransitFactory
import com.itachi1706.cepaslib.transit.TransitIdentity
import com.itachi1706.cepaslib.transit.TransitInfo
import com.itachi1706.cepaslib.transit.ezlink.EZLinkTransitFactory

class TransitFactoryRegistry {

    private val registry =
        mutableMapOf<Class<out Card>, MutableList<TransitFactory<Card, TransitInfo>>>()

    init {
        registerFactory(CEPASCard::class.java, EZLinkTransitFactory())
    }

    fun parseTransitIdentity(card: Card): TransitIdentity? = findFactory(card)?.parseIdentity(card)

    fun parseTransitInfo(card: Card): TransitInfo? = findFactory(card)?.parseInfo(card)

    @Suppress("UNCHECKED_CAST")
    private fun registerFactory(cardClass: Class<out Card>, factory: TransitFactory<*, *>) {
        var factories = registry[cardClass]
        if (factories == null) {
            factories = mutableListOf()
            registry[cardClass] = factories
        }
        factories.add(factory as TransitFactory<Card, TransitInfo>)
    }

    private fun findFactory(card: Card): TransitFactory<Card, out TransitInfo>? =
        registry[card.parentClass]?.find { it.check(card) }
}
