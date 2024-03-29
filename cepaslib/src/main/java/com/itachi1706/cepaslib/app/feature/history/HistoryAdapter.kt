/*
 * HistoryAdapter.kt
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
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.itachi1706.cepaslib.R
import com.itachi1706.cepaslib.app.core.kotlin.bindView
import com.itachi1706.cepaslib.app.core.kotlin.inflate
import com.jakewharton.rxrelay3.PublishRelay
import java.text.DateFormat
import java.text.SimpleDateFormat

internal class HistoryAdapter(
    private val viewModels: List<HistoryViewModel>,
    private val clicksRelay: PublishRelay<HistoryViewModel>,
    private val selectionRelay: PublishRelay<List<HistoryViewModel>>
) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder =
        HistoryViewHolder(parent.inflate(R.layout.item_history))

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val viewModel = viewModels[position]
        holder.update(viewModel)
        holder.itemView.setOnClickListener {
            if (hasSelectedItems()) {
                viewModel.isSelected = !viewModel.isSelected
                notifySelectionChanged()
            } else {
                clicksRelay.accept(viewModel)
            }
        }
        holder.itemView.setOnLongClickListener {
            if (!hasSelectedItems()) {
                viewModel.isSelected = true
                notifySelectionChanged()
                true
            } else {
                false
            }
        }
    }

    override fun getItemCount(): Int = viewModels.size

    private fun hasSelectedItems(): Boolean = viewModels.any { it.isSelected }

    private fun notifySelectionChanged() {
        notifyDataSetChanged()
        selectionRelay.accept(viewModels.filter { it.isSelected })
    }

    fun clearSelectedItems() {
        for (viewModel in viewModels) {
            viewModel.isSelected = false
        }
        notifySelectionChanged()
    }

    internal class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewCardName: TextView by bindView(R.id.card_name)
        private val textViewCardSerial: TextView by bindView(R.id.card_serial)
        private val textViewCardTime: TextView by bindView(R.id.card_time)
        private val textViewCardDate: TextView by bindView(R.id.card_date)

        @SuppressLint("SetTextI18n")
        fun update(viewModel: HistoryViewModel) {
            val scannedAt = viewModel.savedCard.scannedAt
            val identity = viewModel.transitIdentity
            val savedCard = viewModel.savedCard

            val timeInstance = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)
            val dateInstance = SimpleDateFormat.getDateInstance(DateFormat.SHORT)

            textViewCardDate.text = dateInstance.format(scannedAt)
            textViewCardTime.text = timeInstance.format(scannedAt)

            if (identity != null) {
                val serial = identity.serialNumber ?: savedCard.serial
                textViewCardName.text = identity.name
                textViewCardSerial.text = serial
            } else {
                textViewCardName.text = itemView.resources.getString(R.string.unknown_card)
                textViewCardSerial.text = "${savedCard.type} - ${savedCard.serial}"
            }

            itemView.isSelected = viewModel.isSelected
        }
    }
}
