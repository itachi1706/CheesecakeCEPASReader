/*
 * CardAdvancedScreen.kt
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

package com.itachi1706.cepaslib.app.feature.card.advanced

import android.content.Context
import com.itachi1706.cepaslib.R
import com.itachi1706.cepaslib.app.core.inject.ScreenScope
import com.itachi1706.cepaslib.app.core.ui.ActionBarOptions
import com.itachi1706.cepaslib.app.core.ui.FareBotScreen
import com.itachi1706.cepaslib.app.core.util.ActionBarOptionsDefaults
import com.itachi1706.cepaslib.app.feature.main.MainActivity
import com.itachi1706.cepaslib.card.Card
import com.itachi1706.cepaslib.transit.TransitInfo

class CardAdvancedScreen(private val card: Card, private val transitInfo: TransitInfo?) :
    FareBotScreen<CardAdvancedScreen.Component, CardAdvancedScreenView>() {

    override fun getActionBarOptions(): ActionBarOptions =
        ActionBarOptionsDefaults.getActionBarOptionsDefault()

    override fun onCreateView(context: Context): CardAdvancedScreenView =
        CardAdvancedScreenView(context)

    override fun getTitle(context: Context): String = activity.getString(R.string.advanced)

    override fun onShow(context: Context) {
        super.onShow(context)
        if (transitInfo != null) {
            val transitInfoUi = transitInfo.getAdvancedUi(activity)
            if (transitInfoUi != null) {
                view.addTab(transitInfo.getCardName(context.resources), transitInfoUi)
            }
        }
        view.addTab(card.cardType.toString(), card.getAdvancedUi(activity))
    }

    override fun createComponent(parentComponent: MainActivity.MainActivityComponent): Component =
        DaggerCardAdvancedScreen_Component.builder()
            .mainActivityComponent(parentComponent)
            .build()

    override fun inject(component: Component) {
        component.inject(this)
    }

    @ScreenScope
    @dagger.Component(dependencies = [MainActivity.MainActivityComponent::class])
    interface Component {
        fun inject(screen: CardAdvancedScreen)
    }
}
