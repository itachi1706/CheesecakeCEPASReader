/*
 * FareBotScreen.kt
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

package com.itachi1706.cepaslib.app.core.ui

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.CallSuper
import com.itachi1706.cepaslib.app.feature.main.MainActivity
import autodispose2.OutsideScopeException
import autodispose2.lifecycle.CorrespondingEventsFunction
import autodispose2.lifecycle.LifecycleScopeProvider
import com.jakewharton.rxrelay3.BehaviorRelay
import com.wealthfront.magellan.Screen
import com.wealthfront.magellan.ScreenView
import io.reactivex.rxjava3.core.Observable

@Suppress("FINITE_BOUNDS_VIOLATION_IN_JAVA")
abstract class FareBotScreen<C, V> : Screen<V>(), LifecycleScopeProvider<ScreenLifecycleEvent>
        where V : ViewGroup, V : ScreenView<*> {

    private val lifecycleRelay = BehaviorRelay.create<ScreenLifecycleEvent>()

    override fun createView(context: Context): V {
        val parentComponent = (activity as MainActivity).component
        inject(createComponent(parentComponent))
        return onCreateView(context)
    }

    companion object {
        private val CORRESPONDING_EVENTS = CorrespondingEventsFunction<ScreenLifecycleEvent> { lastEvent ->
            when (lastEvent) {
                ScreenLifecycleEvent.RESUME -> ScreenLifecycleEvent.PAUSE
                ScreenLifecycleEvent.SHOW -> ScreenLifecycleEvent.HIDE
                else -> throw OutsideScopeException("what! $lastEvent")
            }
        }
    }

    @Deprecated("override getActionBarOptions instead", replaceWith = ReplaceWith("getActionBarOptions()"))
    final override fun getActionBarColorRes(): Int {
        return super.getActionBarColorRes()
    }

    open fun getActionBarOptions(): ActionBarOptions = ActionBarOptions()

    @CallSuper
    override fun onResume(context: Context) {
        super.onResume(context)
        lifecycleRelay.accept(ScreenLifecycleEvent.RESUME)
    }

    @CallSuper
    override fun onShow(context: Context) {
        super.onShow(context)
        lifecycleRelay.accept(ScreenLifecycleEvent.SHOW)
    }

    @CallSuper
    override fun onPause(context: Context) {
        super.onPause(context)
        lifecycleRelay.accept(ScreenLifecycleEvent.PAUSE)
    }

    @CallSuper
    override fun onHide(context: Context) {
        super.onHide(context)
        lifecycleRelay.accept(ScreenLifecycleEvent.HIDE)
    }

    final override fun lifecycle(): Observable<ScreenLifecycleEvent>? =
        lifecycleRelay.hide()

    final override fun correspondingEvents(): CorrespondingEventsFunction<ScreenLifecycleEvent> =
            CORRESPONDING_EVENTS

    final override fun peekLifecycle(): ScreenLifecycleEvent = lifecycleRelay.value!!

    protected abstract fun onCreateView(context: Context): V

    protected abstract fun createComponent(parentComponent: MainActivity.MainActivityComponent): C

    protected abstract fun inject(component: C)
}
