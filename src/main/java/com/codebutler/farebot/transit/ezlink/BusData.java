package com.codebutler.farebot.transit.ezlink;

import androidx.annotation.Keep;

import com.google.gson.JsonObject;

/**
 * Created by Kenneth on 17/6/2019.
 * for com.codebutler.farebot.transit.ezlink in farebot_cepas
 */
@SuppressWarnings("unused")
@Keep
final class BusData {

    private JsonObject services;
    private JsonObject premiumServices;

    BusData() {}
    JsonObject getServices() { return services; }
    JsonObject getPremiumServices() { return premiumServices; }

    static class OperatorName {

        private String shortName;
        private String longName;

        OperatorName(String publicBus) {
            this.shortName = publicBus;
            switch (publicBus.toLowerCase()) {
                case "smrt": this.longName = "SMRT Buses"; break;
                case "sbst": this.longName = "SBS Transit"; break;
                case "tts": this.longName = "Tower Transit Singapore"; break;
                case "gas": this.longName = "Go Ahead Singapore"; break;
                default: this.longName = "SG Buses";
            }
        }

        OperatorName(String premiumBus, boolean isPremium) {
            this.longName = premiumBus;
            this.shortName = premiumBus.replaceAll("[^A-Z]", "");
        }

        String getShortName() { return shortName; }
        String getLongName() { return longName; }
    }
}
