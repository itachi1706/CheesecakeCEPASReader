/*
 * RequestPermissionsResult.kt
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

package com.itachi1706.cepaslib.app.core.activity

data class RequestPermissionsResult(
    val requestCode: Int,
    val permissions: Array<out String>,
    val grantResults: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RequestPermissionsResult

        if (requestCode != other.requestCode) return false
        if (!permissions.contentEquals(other.permissions)) return false
        return grantResults.contentEquals(other.grantResults)
    }

    override fun hashCode(): Int {
        var result = requestCode
        result = 31 * result + permissions.contentHashCode()
        result = 31 * result + grantResults.contentHashCode()
        return result
    }
}
