/*
 * HelpScreen.kt
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

package com.itachi1706.cepaslib.app.feature.help

import android.content.Context
import com.itachi1706.cepaslib.R
import com.itachi1706.cepaslib.app.core.inject.ScreenScope
import com.itachi1706.cepaslib.app.core.ui.ActionBarOptions
import com.itachi1706.cepaslib.app.core.ui.FareBotScreen
import com.itachi1706.cepaslib.app.core.util.ActionBarOptionsDefaults
import com.itachi1706.cepaslib.app.feature.main.MainActivity
import dagger.Component

class HelpScreen : FareBotScreen<HelpScreen.HelpComponent, HelpScreenView>() {

    override fun getTitle(context: Context): String = context.getString(R.string.supported_cards)

    override fun getActionBarOptions(): ActionBarOptions =
        ActionBarOptionsDefaults.getActionBarOptionsDefault()

    override fun onCreateView(context: Context): HelpScreenView = HelpScreenView(context)

    override fun createComponent(parentComponent: MainActivity.MainActivityComponent): HelpComponent =
        DaggerHelpScreen_HelpComponent.builder()
            .mainActivityComponent(parentComponent)
            .build()

    override fun inject(component: HelpComponent) {
        component.inject(this)
    }

    @ScreenScope
    @Component(dependencies = [MainActivity.MainActivityComponent::class])
    interface HelpComponent {
        fun inject(helpScreen: HelpScreen)
    }
}
