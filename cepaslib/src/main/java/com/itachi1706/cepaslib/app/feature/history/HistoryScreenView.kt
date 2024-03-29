/*
 * HistoryScreenView.kt
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

import android.annotation.SuppressLint
import android.content.Context
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import autodispose2.android.scope
import autodispose2.autoDispose
import com.itachi1706.cepaslib.R
import com.itachi1706.cepaslib.app.core.activity.ActivityOperations
import com.itachi1706.cepaslib.app.core.kotlin.bindView
import com.jakewharton.rxrelay3.PublishRelay
import com.wealthfront.magellan.BaseScreenView
import io.reactivex.rxjava3.core.Observable

@SuppressLint("ViewConstructor")
class HistoryScreenView(
    context: Context,
    val activityOperations: ActivityOperations,
    val listener: Listener
) :
    BaseScreenView<HistoryScreen>(context) {

    private val clicksRelay = PublishRelay.create<HistoryViewModel>()
    private val selectionRelay = PublishRelay.create<List<HistoryViewModel>>()

    private val recyclerView: RecyclerView by bindView(R.id.recycler)
    private val emptyView: View by bindView(R.id.empty)

    private var actionMode: ActionMode? = null

    init {
        inflate(context, R.layout.screen_history, this)
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        selectionRelay
            .autoDispose(scope())
            .subscribe { items ->
                if (items.isNotEmpty()) {
                    if (actionMode == null) {
                        actionMode =
                            activityOperations.startActionMode(object : ActionMode.Callback {
                                override fun onCreateActionMode(
                                    actionMode: ActionMode,
                                    menu: Menu
                                ): Boolean {
                                    actionMode.menuInflater.inflate(R.menu.action_history, menu)
                                    return true
                                }

                                override fun onActionItemClicked(
                                    actionMode: ActionMode,
                                    menuItem: MenuItem
                                ): Boolean {
                                    @Suppress("UNCHECKED_CAST")
                                    when (menuItem.itemId) {
                                        R.id.delete -> {
                                            listener.onDeleteSelectedItems(actionMode.tag as List<HistoryViewModel>)
                                        }
                                    }
                                    actionMode.finish()
                                    return false
                                }

                                override fun onPrepareActionMode(
                                    p0: ActionMode?,
                                    p1: Menu?
                                ): Boolean = false

                                override fun onDestroyActionMode(actionMode: ActionMode?) {
                                    this@HistoryScreenView.actionMode = null
                                    (recyclerView.adapter as? HistoryAdapter)?.clearSelectedItems()
                                }
                            })
                    }
                    actionMode?.title = items.size.toString()
                    actionMode?.tag = items
                } else {
                    actionMode?.finish()
                }
            }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        actionMode?.finish()
    }

    internal fun observeItemClicks(): Observable<HistoryViewModel> = clicksRelay.hide()

    internal fun setViewModels(viewModels: List<HistoryViewModel>) {
        recyclerView.adapter = HistoryAdapter(viewModels, clicksRelay, selectionRelay)
        emptyView.visibility = if (viewModels.isEmpty()) View.VISIBLE else View.GONE
    }

    interface Listener {
        fun onDeleteSelectedItems(items: List<HistoryViewModel>)
    }
}
