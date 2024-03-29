/*
 * HelpScreenView.kt
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
import android.nfc.NfcAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itachi1706.cepaslib.R
import com.itachi1706.cepaslib.app.core.kotlin.bindView
import com.itachi1706.cepaslib.card.CardType
import com.wealthfront.magellan.BaseScreenView

class HelpScreenView(context: Context) : BaseScreenView<HelpScreen>(context) {

    companion object {
        private val SUPPORTED_CARDS = listOf(
            SupportedCard(
                imageResId = R.drawable.ezlink_card,
                name = "EZ-Link",
                locationResId = R.string.location_singapore,
                cardType = CardType.CEPAS
            ),
            SupportedCard(
                imageResId = R.drawable.nets_card,
                name = "NETS FlashPay",
                locationResId = R.string.location_singapore,
                cardType = CardType.CEPAS
            ),
            SupportedCard(
                imageResId = R.drawable.sg_concession,
                name = "EZ-Link Concession Cards (Schools, Child, Senior, NSF)",
                locationResId = R.string.location_singapore,
                cardType = CardType.CEPAS
            )

        )
    }

    private val recyclerView: RecyclerView by bindView(R.id.recycler)

    init {
        inflate(context, R.layout.screen_help, this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = SupportedCardsAdapter(context, SUPPORTED_CARDS)
    }

    internal class SupportedCardsAdapter(
        private val context: Context,
        private val supportedCards: List<SupportedCard>
    ) :
        RecyclerView.Adapter<SupportedCardViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupportedCardViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            return SupportedCardViewHolder(
                layoutInflater.inflate(
                    R.layout.item_supported_card,
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: SupportedCardViewHolder, position: Int) {
            holder.bind(context, supportedCards[position])
        }

        override fun getItemCount(): Int = supportedCards.size
    }

    internal class SupportedCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textViewName: TextView by bindView(R.id.card_name)
        private val textViewLocation: TextView by bindView(R.id.card_location)
        private val textViewNote: TextView by bindView(R.id.card_note)
        private val imageView: ImageView by bindView(R.id.card_image)
        private val imageViewSecure: ImageView by bindView(R.id.card_secure)
        private val viewNotSupported: View by bindView(R.id.card_not_supported)

        init {
            imageViewSecure.setOnClickListener {
                Toast.makeText(imageViewSecure.context, R.string.keys_required, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        fun bind(context: Context, supportedCard: SupportedCard) {
            textViewName.text = supportedCard.name
            textViewLocation.setText(supportedCard.locationResId)
            imageView.setImageResource(supportedCard.imageResId)

            imageViewSecure.visibility = if (supportedCard.keysRequired) View.VISIBLE else View.GONE

            val notes = getNotes(context, supportedCard)
            if (notes != null) {
                textViewNote.text = notes
                textViewNote.visibility = View.VISIBLE
            } else {
                textViewNote.text = null
                textViewNote.visibility = View.GONE
            }

            viewNotSupported.visibility =
                if (isCardSupported(context, supportedCard)) View.GONE else View.VISIBLE
        }

        private fun getNotes(context: Context, supportedCard: SupportedCard): String? {
            val notes = ArrayList<String>()
            val extraNoteResId = supportedCard.extraNoteResId
            if (extraNoteResId != null) {
                notes.add(context.getString(extraNoteResId))
            }
            if (supportedCard.preview) {
                notes.add(context.getString(R.string.card_experimental))
            }
            if (supportedCard.cardType == CardType.CEPAS) {
                notes.add(context.getString(R.string.card_not_compatible))
            }
            if (notes.isNotEmpty()) {
                return notes.joinToString(" ")
            }
            return null
        }

        private fun isCardSupported(context: Context, supportedCard: SupportedCard): Boolean {
            if (NfcAdapter.getDefaultAdapter(context) == null) {
                return false // No NFC
            }
            val supportsMifareClassic = context.packageManager.hasSystemFeature("com.nxp.mifare")
            return !(supportedCard.cardType == CardType.MifareClassic && !supportsMifareClassic)
        }
    }

    data class SupportedCard(
        @get:DrawableRes val imageResId: Int,
        val name: String,
        @get:StringRes val locationResId: Int,
        val cardType: CardType,
        val keysRequired: Boolean = false,
        val preview: Boolean = false,
        @get:StringRes val extraNoteResId: Int? = null
    )
}
