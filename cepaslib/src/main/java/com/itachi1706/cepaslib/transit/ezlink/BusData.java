package com.itachi1706.cepaslib.transit.ezlink;

import androidx.annotation.Keep;

import com.google.gson.JsonObject;

/**
 * Created by Kenneth on 17/6/2019.
 * for com.itachi1706.cepaslib.transit.ezlink in farebot_cepas
 */
@SuppressWarnings("unused")
@Keep
final class BusData {

    private JsonObject services;
    private JsonObject premiumServices;

    BusData() {
    }

    JsonObject getServices() {
        return services;
    }

    JsonObject getPremiumServices() {
        return premiumServices;
    }

    static class OperatorName {

        private final String shortName;
        private final String longName;
        private final boolean isPremium;

        OperatorName(String publicBus) {
            this.shortName = publicBus;
            this.isPremium = false;
            switch (publicBus.toLowerCase()) {
                case "smrt" -> this.longName = "SMRT Buses";
                case "sbst" -> this.longName = "SBS Transit";
                case "tts" -> this.longName = "Tower Transit Singapore";
                case "gas" -> this.longName = "Go Ahead Singapore";
                default -> this.longName = "SG Buses";
            }
        }

        OperatorName(String premiumBus, boolean isPremium) {
            this.longName = premiumBus;
            this.shortName = premiumBus.replaceAll("[^A-Z]", "");
            this.isPremium = isPremium;
        }

        String getShortName() {
            return shortName;
        }

        String getLongName() {
            return longName;
        }

        boolean isPremium() {
            return isPremium;
        }
    }
}
