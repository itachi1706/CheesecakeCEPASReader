/*
 * HistoryViewModel.kt
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

package com.itachi1706.cepaslib.app.feature.history

import com.itachi1706.cepaslib.card.RawCard
import com.itachi1706.cepaslib.persist.db.model.SavedCard
import com.itachi1706.cepaslib.transit.TransitIdentity

data class HistoryViewModel(
    val savedCard: SavedCard,
    val rawCard: RawCard<*>,
    val transitIdentity: TransitIdentity? = null,
    val parseException: Exception? = null,
    var isSelected: Boolean = false
)
