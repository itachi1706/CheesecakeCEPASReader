/*
 * CardScreen.kt
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

package com.itachi1706.cepaslib.app.feature.card

import android.content.Context
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import autodispose2.autoDispose
import com.itachi1706.cepaslib.R
import com.itachi1706.cepaslib.app.core.activity.ActivityOperations
import com.itachi1706.cepaslib.app.core.inject.ScreenScope
import com.itachi1706.cepaslib.app.core.transit.TransitFactoryRegistry
import com.itachi1706.cepaslib.app.core.ui.ActionBarOptions
import com.itachi1706.cepaslib.app.core.ui.FareBotScreen
import com.itachi1706.cepaslib.app.core.util.ActionBarOptionsDefaults
import com.itachi1706.cepaslib.app.feature.card.advanced.CardAdvancedScreen
import com.itachi1706.cepaslib.app.feature.card.map.TripMapScreen
import com.itachi1706.cepaslib.app.feature.main.MainActivity
import com.itachi1706.cepaslib.card.Card
import com.itachi1706.cepaslib.card.RawCard
import com.itachi1706.cepaslib.transit.TransitInfo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class CardScreen(private val rawCard: RawCard<*>) :
    FareBotScreen<CardScreen.Component, CardScreenView>() {

    data class Content(
        val card: Card,
        val transitInfo: TransitInfo?,
        val viewModels: List<TransactionViewModel>
    )

    private var content: Content? = null

    @Inject
    lateinit var activityOperations: ActivityOperations
    @Inject
    lateinit var transitFactoryRegistry: TransitFactoryRegistry

    override fun getActionBarOptions(): ActionBarOptions =
        ActionBarOptionsDefaults.getActionBarOptionsDefault(shadow = false)

    override fun onCreateView(context: Context): CardScreenView = CardScreenView(context)

    override fun onShow(context: Context) {
        super.onShow(context)

        activityOperations.menuItemClick
            .autoDispose(this)
            .subscribe { menuItem ->
                when (menuItem.itemId) {
                    R.id.card_advanced -> {
                        content?.let {
                            navigator.goTo(CardAdvancedScreen(it.card, it.transitInfo))
                        }
                    }
                }
            }

        loadContent()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(this)
            .subscribe { content ->
                this.content = content
                if (content.transitInfo != null) {
                    (activity as AppCompatActivity).supportActionBar?.apply {
                        title = content.transitInfo.getCardName(view.resources)
                        subtitle = content.transitInfo.serialNumber
                    }
                    view.setTransitInfo(content.transitInfo, content.viewModels)
                } else {
                    (activity as AppCompatActivity).supportActionBar?.apply {
                        title = context.getString(R.string.unknown_card)
                    }
                    view.setError(context.getString(R.string.unknown_card_desc))
                }
                activity.invalidateOptionsMenu()
            }

        view.observeItemClicks()
            .autoDispose(this)
            .subscribe { viewModel ->
                if (viewModel is TransactionViewModel.TripViewModel) {
                    val trip = viewModel.trip
                    if (trip.startStation?.hasLocation() == true || trip.endStation?.hasLocation() == true) {
                        navigator.goTo(TripMapScreen(trip))
                    }
                }
            }
    }

    override fun onUpdateMenu(menu: Menu?) {
        menu?.clear()
        activity.menuInflater.inflate(R.menu.screen_card, menu)
        menu?.findItem(R.id.card_advanced)?.isVisible = content != null
    }

    private fun loadContent(): Single<Content> = Single.create { e ->
        try {
            val card = rawCard.parse()
            val transitInfo = transitFactoryRegistry.parseTransitInfo(card)
            val viewModels = createViewModels(transitInfo)
            e.onSuccess(Content(card, transitInfo, viewModels))
        } catch (ex: Exception) {
            e.onError(ex)
        }
    }

    private fun createViewModels(transitInfo: TransitInfo?): List<TransactionViewModel> {
        val subscriptions = transitInfo?.subscriptions?.map {
            TransactionViewModel.SubscriptionViewModel(activity, it)
        } ?: listOf()
        val trips =
            transitInfo?.trips?.map { TransactionViewModel.TripViewModel(activity, it) } ?: listOf()
        val refills =
            transitInfo?.refills?.map { TransactionViewModel.RefillViewModel(activity, it) }
                ?: listOf()
        return subscriptions + (trips + refills).sortedByDescending { it.date }
    }

    override fun createComponent(parentComponent: MainActivity.MainActivityComponent): Component =
        DaggerCardScreen_Component.builder()
            .mainActivityComponent(parentComponent)
            .build()

    override fun inject(component: Component) {
        component.inject(this)
    }

    @ScreenScope
    @dagger.Component(dependencies = [MainActivity.MainActivityComponent::class])
    interface Component {
        fun inject(screen: CardScreen)
    }
}
