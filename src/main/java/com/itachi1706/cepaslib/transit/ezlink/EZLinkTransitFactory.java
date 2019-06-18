/*
 * EZLinkTransitFactory.java
 *
 * This file is part of FareBot.
 * Learn more at: https://codebutler.github.io/farebot/
 *
 * Copyright (C) 2011-2012, 2014-2016 Eric Butler <eric@codebutler.com>
 * Copyright (C) 2011 Sean Cross <sean@chumby.com>
 * Copyright (C) 2012 tbonang <bonang@gmail.com>
 * Copyright (C) 2012 Victor Heng <bakavic@gmail.com>
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

package com.itachi1706.cepaslib.transit.ezlink;

import androidx.annotation.NonNull;

import com.itachi1706.cepaslib.card.cepas.CEPASCard;
import com.itachi1706.cepaslib.card.cepas.CEPASTransaction;
import com.itachi1706.cepaslib.transit.TransitFactory;
import com.itachi1706.cepaslib.transit.TransitIdentity;
import com.itachi1706.cepaslib.transit.Trip;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class EZLinkTransitFactory implements TransitFactory<CEPASCard, EZLinkTransitInfo> {

    @Override
    public boolean check(@NonNull CEPASCard cepasCard) {
        return cepasCard.getHistory(3) != null
                    && cepasCard.getHistory(3).isValid()
                    && cepasCard.getPurse(3) != null
                    && cepasCard.getPurse(3).isValid();
    }

    @NonNull
    @Override
    public TransitIdentity parseIdentity(@NonNull CEPASCard card) {
        String canNo = card.getPurse(3).getCAN().hex();
        return TransitIdentity.create(EZLinkData.getCardIssuer(canNo), canNo);
    }

    @NonNull
    @Override
    public EZLinkTransitInfo parseInfo(@NonNull CEPASCard cepasCard) {
        String serialNumber = cepasCard.getPurse(3).getCAN().hex();
        int balance = cepasCard.getPurse(3).getPurseBalance();
        EZLinkTrip[] trips = parseTrips(serialNumber, cepasCard);
        return EZLinkTransitInfo.create(serialNumber, ImmutableList.<Trip>copyOf(trips), balance);
    }

    @NonNull
    private static EZLinkTrip[] parseTrips(@NonNull String serialNumber, @NonNull CEPASCard card) {
        List<CEPASTransaction> transactions = card.getHistory(3).getTransactions();
        if (transactions != null) {
            EZLinkTrip[] trips = new EZLinkTrip[transactions.size()];
            for (int i = 0; i < trips.length; i++) {
                trips[i] = EZLinkTrip.create(transactions.get(i), EZLinkData.getCardIssuer(serialNumber));
            }
            return trips;
        }
        return new EZLinkTrip[0];
    }
}
