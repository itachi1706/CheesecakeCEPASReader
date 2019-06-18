/*
 * EZLinkData.java
 *
 * This file is part of FareBot.
 * Learn more at: https://codebutler.github.io/farebot/
 *
 * Copyright (C) 2016 Eric Butler <eric@codebutler.com>
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

package com.codebutler.farebot.transit.ezlink;

import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

import com.codebutler.farebot.R;
import com.codebutler.farebot.transit.Station;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

final class EZLinkData {

    // Get data from http://api.itachi1706.com/api/busservices.php
    static void initialize(@NonNull Resources resources) {
        if (initData) return; // Already initialized
        String busData = readFromRaw(resources, R.raw.busdata);
        Gson gson = new Gson();
        BusData buses = gson.fromJson(busData, BusData.class);
        // Get basic bus data
        for (Map.Entry<String, JsonElement> publicBuses : buses.getServices().entrySet())
            for (JsonElement publicBusSvc : publicBuses.getValue().getAsJsonArray())
                SG_BUSES.put(publicBusSvc.getAsString(), new BusData.OperatorName(publicBuses.getKey()));

        // Get premium bus data
        for (Map.Entry<String, JsonElement> premiumBuses : buses.getPremiumServices().entrySet())
            for (JsonElement premiumBusSvc : premiumBuses.getValue().getAsJsonArray())
                SG_BUSES.put(premiumBusSvc.getAsString(), new BusData.OperatorName(premiumBuses.getKey(), true));
        initData = true; // Finish init
    }

    private static String readFromRaw(Resources resources, @RawRes int resId) {
        //Get Data From Text Resource File Contains Json Data.
        InputStream inputStream = resources.openRawResource(resId);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return byteArrayOutputStream.toString();
    }

    static final HashMap<String, BusData.OperatorName> SG_BUSES = new HashMap<>();
    static boolean initData = false;

    // Data snagged from http://www.sgwiki.com/wiki/North_East_Line
    // Coordinates taken from respective Wikipedia MRT pages
    private static final Map<String, Station> MRT_STATIONS = new TreeMap<String, Station>() {
        private static final long serialVersionUID = 1L;

        {
            // Transaction Codes
            put("GTM", Station.create("GTM Manual Top-up", "GTM", "GTM", null, null));
            put("PSC", Station.create("Passenger Service Centre Top-up", "GTM", "GTM", null, null));

            // North-East Line (NEL)
            put("HBF", Station.create("HarbourFront", "NE1 / CC29", "HBF", "1.265297", "103.82225"));
            put("HBC", Station.create("HarbourFront", "NE1 / CC29", "HBC", "1.265297", "103.82225"));
            put("OTP", Station.create("Outram Park", "NE3 / EW16", "OTP", "1.280225", "103.839486"));
            put("CNT", Station.create("Chinatown", "NE4 / DT19", "CNT", "1.28485", "103.844006"));
            put("CQY", Station.create("Clarke Quay", "NE5", "CQY", "1.288708", "103.846606"));
            put("DBN", Station.create("Dhoby Ghaut", "CC1 / NS24 / NE6", "DBN", "1.299156", "103.845736"));
            put("LTI", Station.create("Little India", "NE7 / DT12", "LTI", "1.306725", "103.849175"));
            put("FRP", Station.create("Farrer Park", "NE8", "FRP", "1.312314", "103.854028"));
            put("BNK", Station.create("Boon Keng", "NE9", "BNK", "1.319483", "103.861722"));
            put("PTP", Station.create("Potong Pasir", "NE10", "PTP", "1.331161", "103.869058"));
            put("WLH", Station.create("Woodleigh", "NE11", "WLH", "1.339181", "103.870744"));
            put("SER", Station.create("Serangoon", "NE12 / CC13", "SER", "1.349944", "103.873092"));
            put("KVN", Station.create("Kovan", "NE13", "KVN", "1.360214", "103.884864"));
            put("HGN", Station.create("Hougang", "NE14", "HGN", "1.371292", "103.892161"));
            put("BGK", Station.create("Buangkok", "NE15", "BGK", "1.382728", "103.892789"));
            put("SKG", Station.create("Sengkang", "NE16 / STC", "SKG", "1.391653", "103.895133"));
            put("PGL", Station.create("Punggol", "NE17 / PTC", "PGL", "1.405264", "103.902097"));
            put("PGC", Station.create("Punggol Coast", "NE18", "PGC", "1.414600", "103.910900"));

            // Downtown Line (DTL)
            put("BPJ", Station.create("Bukit Panjang", "DT1 / BP6", "BPJ", "1.377926", "103.763077"));
            put("BKP", Station.create("Bukit Panjang", "DT1 / BP6", "BPJ", "1.377926", "103.763077"));
            put("CSW", Station.create("Cashew", "DT2", "CSW", "1.368972", "103.764442"));
            put("HVW", Station.create("Hillview", "DT3", "HVW", "1.362734", "103.767473"));
            put("BTW", Station.create("Beauty World", "DT5", "BTW", "1.340935", "103.775691"));
            put("KAP", Station.create("King Albert Park", "DT6", "KAP", "1.335502", "103.783739"));
            put("SAV", Station.create("Sixth Avenue", "DT7", "SAV", "1.330670", "103.797372"));
            put("TKK", Station.create("Tan Kah Kee", "DT8", "TKK", "1.325963", "103.807280"));
            put("STV", Station.create("Stevens", "DT10", "STV", "1.320009", "103.825868"));
            put("RCR", Station.create("Rochor", "DT13", "RCR", "1.304045", "103.852392"));
            put("PMN", Station.create("Promenade", "CC4 / DT15", "PMN", "1.293131", "103.861064"));
            put("BFD", Station.create("Bayfront", "CE1 / DT16", "BFT", "1.282347", "103.859317"));
            put("DTN", Station.create("Downtown", "DT17", "DTN", "1.279458", "103.852931"));
            put("TLA", Station.create("Telok Ayer", "DT18", "TLA", "1.282050", "103.848472"));
            put("FCN", Station.create("Fort Canning", "DT20", "FCN", "1.292402", "103.844313"));
            put("BCL", Station.create("Bencoolen", "DT21", "BCL", "1.298422", "103.849911"));
            put("JLB", Station.create("Jalan Besar", "DT22", "JLB", "1.305449", "103.855527"));
            put("BDM", Station.create("Bendemeer", "DT23", "BDM", "1.313778", "103.863039"));
            put("GLB", Station.create("Geylang Bahru", "DT24", "GLB", "1.321377", "103.871765"));
            put("MTR", Station.create("Mattar", "DT25", "MTR", "1.327038", "103.882993"));
            put("UBI", Station.create("Ubi", "DT27", "UBI", "1.329956", "103.899208"));
            put("KKB", Station.create("Kaki Bukit", "DT28", "KKB", "1.334955", "103.907810"));
            put("BDN", Station.create("Bedok North", "DT29", "BDN", "1.334766", "103.918125"));
            put("BDR", Station.create("Bedok Reservoir", "DT30", "BDR", "1.336631", "103.932036"));
            put("TPW", Station.create("Tampines West", "DT31", "TPW", "1.346246", "103.938321"));
            put("TPE", Station.create("Tampines East", "DT33", "TPE", "1.356055", "103.954381"));
            put("UPC", Station.create("Upper Changi", "DT34", "UPC", "1.341632", "103.961420"));
            put("XPD", Station.create("Expo", "CG1 / DT35", "XPO", "1.335469", "103.961767"));

            // Circle Line (CCL)
            put("DBG", Station.create("Dhoby Ghaut", "CC1 / NS24 / NE6", "DBG", "1.299156", "103.845736"));
            put("BBS", Station.create("Bras Basah", "CC2", "BBS", "1.296931", "103.850631"));
            put("EPN", Station.create("Esplanade", "CC3", "EPN", "1.293436", "103.855381"));
            put("PMD", Station.create("Promenade", "CC4 / DT15", "PMD", "1.293131", "103.861064"));
            put("NCH", Station.create("Nicoll Highway", "CC5", "NCH", "1.299697", "103.863611"));
            put("SDM", Station.create("Stadium", "CC6", "SDM", "1.302856", "1.302856"));
            put("MBT", Station.create("Mountbatten", "CC7", "MBT", "1.306306", "103.882531"));
            put("DKT", Station.create("Dakota", "CC8", "DKT", "1.308289", "103.888253"));
            put("PYL", Station.create("Paya Lebar", "CC9 / EW8", "PYL", "1.317767", "103.892381"));
            put("MPS", Station.create("MacPherson", "CC10 / DT26", "MPS", "1.32665", "103.890019"));
            put("TAS", Station.create("Tai Seng", "CC11", "TAS", "1.335833", "103.887942"));
            put("BLY", Station.create("Bartley", "CC12", "BLY", "1.342756", "103.879697"));
            put("SER", Station.create("Serangoon", "CC13 / NE12", "SER", "1.349944", "103.873092"));
            put("SRC", Station.create("Serangoon", "CC13 / NE12", "SER", "1.349944", "103.873092"));
            put("LRC", Station.create("Lorong Chuan", "CC14", "LRC", "1.351636", "103.864064"));
            put("BSH", Station.create("Bishan", "CC15 / NS17", "BSH", "1.351236", "103.848456"));
            // Alternate name (Circle line entrance)
            put("BSC", Station.create("Bishan", "CC15 / NS17", "BSC", "1.351236", "103.848456"));
            put("MRM", Station.create("Marymount", "CC16", "MRM", "1.349078", "103.839492"));
            put("CDT", Station.create("Caldecott", "CC17", "CDT", "1.337761", "103.839447"));
            put("BTN", Station.create("Botanic Gardens", "CC19 / DT9", "BTN", "1.322519", "103.815406"));
            put("FRR", Station.create("Farrer Road", "CC20", "FRR", "1.317319", "103.807431"));
            put("HLV", Station.create("Holland Village", "CC21", "HLV", "1.312078", "103.796208"));
            // Reserved for Alternate name (Circle line entrance)
            //put("", Station.create("Buona Vista", "EW21 / CC22", "", "1.17", "103.5"));
            put("ONH", Station.create("one-north", "CC23", "ONH", "1.299331", "103.787067"));
            put("KRG", Station.create("Kent Ridge", "CC24", "KRG", "1.293383", "103.784394"));
            put("HPV", Station.create("Haw Par Villa", "CC25", "HPV", "1.282386", "103.781867"));
            put("PPJ", Station.create("Pasir Panjang", "CC26", "PPJ", "1.276167", "103.791358"));
            put("LBD", Station.create("Labrador Park", "CC27", "LBD", "1.272267", "103.802908"));
            put("TLB", Station.create("Telok Blangah", "CC28", "TLB", "1.270572", "103.809678"));
            // Reserved for Alternate name (Circle line entrance)
            //put("", Station.create("HarbourFront", "CC20", "", "1.265297", "103.82225"));

            // Marina Bay Extension (CCL)
            put("BFT", Station.create("Bayfront", "CE1 / DT16", "BFT", "1.282347", "103.859317"));

            // Changi Airport Extension (EWL)
            put("TNM", Station.create("Tanah Merah", "EW4", "TNM", "1.327358", "103.946344"));
            put("XPO", Station.create("Expo", "CG1 / DT35", "XPO", "1.335469", "103.961767"));
            put("CGA", Station.create("Changi Airport", "CG2", "CGA", "1.357372", "103.988836"));

            // East-West Line (EWL)
            put("PSR", Station.create("Pasir Ris", "EW1", "PSR", "1.372411", "103.949369"));
            put("TAM", Station.create("Tampines", "EW2 / DT32", "TAM", "1.352528", "103.945322"));
            put("SIM", Station.create("Simei", "EW3", "SIM", "1.343444", "103.953172"));
            put("TNM", Station.create("Tanah Merah", "EW4", "TNM", "1.327358", "103.946344"));
            put("BDK", Station.create("Bedok", "EW5", "BDK", "1.324039", "103.930036"));
            put("KEM", Station.create("Kembangan", "EW6", "KEM", "1.320983", "103.912842"));
            put("EUN", Station.create("Eunos", "EW7", "EUN", "1.319725", "103.903108"));
            put("PYL", Station.create("Paya Lebar", "EW8 / CC9", "PYL", "1.317767", "103.892381"));
            put("ALJ", Station.create("Aljunied", "EW9", "ALJ", "1.316442", "103.882981"));
            put("KAL", Station.create("Kallang", "EW10", "KAL", "1.311469", "103.8714"));
            put("LVR", Station.create("Lavender", "EW11", "LVR", "1.307167", "103.863008"));
            put("BGS", Station.create("Bugis", "EW12 / DT14", "BGS", "1.300194", "103.85615"));
            // Alternate name (Downtown line entrance)
            put("BGD", Station.create("Bugis", "EW12 / DT14", "BGD", "1.300194", "103.85615"));
            put("CTH", Station.create("City Hall", "EW13 / NS25", "CTH", "1.293239", "103.852219"));
            put("RFP", Station.create("Raffles Place", "EW14 / NS26", "RFP", "1.283881", "103.851533"));
            put("TPG", Station.create("Tanjong Pagar", "EW15", "TPG", "1.276439", "103.845711"));
            put("OTP", Station.create("Outram Park", "EW16 / NE3", "OTP", "1.280225", "103.839486"));
            // Alternate name (Northeast line entrance)
            put("OTN", Station.create("Outram Park", "EW16 / NE3", "OTN", "1.280225", "103.839486"));
            put("TIB", Station.create("Tiong Bahru", "EW17", "TIB", "1.286081", "103.826958"));
            put("RDH", Station.create("Redhill", "EW18", "RDH", "1.289733", "103.81675"));
            put("QUE", Station.create("Queenstown", "EW19", "QUE", "1.294442", "103.806114"));
            put("COM", Station.create("Commonwealth", "EW20", "COM", "1.302558", "103.798225"));
            put("BNV", Station.create("Buona Vista", "EW21 / CC22", "BNV", "1.306817", "103.790428"));
            put("DVR", Station.create("Dover", "EW22", "DVR", "1.311314", "103.778658"));
            put("CLE", Station.create("Clementi", "EW23", "CLE", "1.315303", "103.765244"));
            put("JUR", Station.create("Jurong East", "EW24 / NS1", "JUR", "1.333415", "103.742119"));
            put("CNG", Station.create("Chinese Garden", "EW25", "CNG", "1.342711", "103.732467"));
            put("LKS", Station.create("Lakeside", "EW26", "LKS", "1.344589", "103.721139"));
            put("BNL", Station.create("Boon Lay", "EW27", "BNL", "1.338883", "103.706208"));
            put("PNR", Station.create("Pioneer", "EW28", "PNR", "1.337578", "103.697217"));
            put("JKN", Station.create("Joo Koon", "EW29", "JKN", "1.327739", "103.678486"));
            // Tuas West Extension (EWL)
            put("GCL", Station.create("Gul Circle", "EW30", "GCL", "1.319867", "103.661069"));
            put("TCR", Station.create("Tuas Crescent", "EW31", "TCR", "1.320812", "103.648374"));
            put("TWR", Station.create("Tuas West Road", "EW32", "TWR", "1.329568", "103.640132"));
            put("TLK", Station.create("Tuas Link", "EW33", "TLK", "1.340231", "103.636669"));

            // North-South Line (NSL)
            put("JUR", Station.create("Jurong East", "NS1 / EW24", "JUR", "1.333415", "103.742119"));
            put("BBT", Station.create("Bukit Batok", "NS2", "BBT", "1.349073", "103.749664"));
            put("BGB", Station.create("Bukit Gombak", "NS3", "BGB", "1.358702", "103.751787"));
            put("CCK", Station.create("Choa Chu Kang", "NS4 / BP1", "CCK", "1.385092", "103.744322"));
            put("YWT", Station.create("Yew Tee", "NS5", "YWT", "1.396986", "103.747239"));
            put("KRJ", Station.create("Kranji", "NS7", "KRJ", "1.425047", "103.761853"));
            put("MSL", Station.create("Marsiling", "NS8", "MSL", "1.432636", "103.774283"));
            put("WDL", Station.create("Woodlands", "NS9", "WDL", "1.437094", "103.786483"));
            put("ADM", Station.create("Admiralty", "NS10", "ADM", "1.440689", "103.800933"));
            put("SBW", Station.create("Sembawang", "NS11", "SBW", "1.449025", "103.820153"));
            put("YIS", Station.create("Yishun", "NS13", "YIS", "1.429464", "103.835239"));
            put("KTB", Station.create("Khatib", "NS14", "KTB", "1.417167", "103.8329"));
            put("YCK", Station.create("Yio Chu Kang", "NS15", "YCK", "1.381906", "103.844817"));
            put("AMK", Station.create("Ang Mo Kio", "NS16", "AMK", "1.370017", "103.84945"));
            put("BSH", Station.create("Bishan", "NS17 / CC15", "BSH", "1.351236", "103.848456"));
            put("BDL", Station.create("Braddell", "NS18", "BDL", "1.340339", "103.846725"));
            put("TAP", Station.create("Toa Payoh", "NS19", "TAP", "1.332703", "103.847808"));
            put("NOV", Station.create("Novena", "NS20", "NOV", "1.320394", "103.843689"));
            put("NEW", Station.create("Newton", "NS21 / DT11", "NEW", "1.312956", "103.838442"));
            // Alternate name (Downtown line entrance)
            put("NTD", Station.create("Newton", "NS21 / DT11", "NTD", "1.312956", "103.838442"));
            put("ORC", Station.create("Orchard", "NS22", "ORC", "1.304314", "103.831939"));
            put("SOM", Station.create("Somerset", "NS23", "SOM", "1.300514", "103.839028"));
            put("DBG", Station.create("Dhoby Ghaut", "NS24 / NE6 / CC1", "DBG", "1.299156", "103.845736"));
            put("CTH", Station.create("City Hall", "NS25 / EW13", "CTH", "1.293239", "103.852219"));
            put("RFP", Station.create("Raffles Place", "NS26 / EW14", "RFP", "1.283881", "103.851533"));
            put("MRB", Station.create("Marina Bay", "NS27 / CE2", "MRB", "1.276097", "103.854675"));
            put("MSP", Station.create("Marina South Pier", "NS28", "MSP", "1.270958", "103.863242"));

            // Sengkang LRT (East Loop)
            put("SE1", Station.create("Compassvale", "SE1", "SE1", "1.39455", "103.900183"));
            put("SE2", Station.create("Rumbia", "SE2", "SE2", "1.391094", "103.906306"));
            put("SE3", Station.create("Bakau", "SE3", "SE3", "1.387853", "103.905267"));
            put("SE4", Station.create("Kangkar", "SE4", "SE4", "1.383739", "103.902194"));
            put("SE5", Station.create("Ranggung", "SE5", "SE5", "1.383619", "103.897736"));
            // Sengkang LRT (West Loop)
            put("SW1", Station.create("Cheng Lim", "SW1", "SW1", "1.39634", "103.893757"));
            put("SW2", Station.create("Farmway", "SW2", "SW2", "1.397272", "103.888953"));
            put("SW3", Station.create("Kupang", "SW3", "SW3", "1.398538", "103.881365"));
            put("SW4", Station.create("Thanggam", "SW4", "SW4", "1.397371", "103.87542"));
            put("SW5", Station.create("Fernvale", "SW5", "SW5", "1.391935", "103.876142"));
            put("SW6", Station.create("Layar", "SW6", "SW6", "1.392180", "103.879895"));
            put("SW7", Station.create("Tongkang", "SW7", "SW7", "1.389286", "103.886145"));
            put("SW8", Station.create("Renjong", "SW8", "SW8", "1.386614", "103.890425"));

            // Punggol LRT (East Loop)
            put("PE1", Station.create("Cove", "PE1", "PE1", "1.399316", "103.906342"));
            put("PE2", Station.create("Meridian", "PE2", "PE2", "1.396931", "103.909312"));
            put("PE3", Station.create("Coral Edge", "PE3", "PE3", "1.393455", "103.912179"));
            put("PE4", Station.create("Riviera", "PE4", "PE4", "1.39463", "103.916509"));
            put("PE5", Station.create("Kadaloor", "PE5", "PE5", "1.399332", "103.916502"));
            put("PE6", Station.create("Oasis", "PE6", "PE6", "1.401622", "103.91369"));
            put("PE7", Station.create("Damai", "PE7", "PE7", "1.405292", "103.907818"));
            // Punggol LRT (West Loop)
            put("PW1", Station.create("Sam Kee", "PW1", "PW1", "1.411111", "103.904928"));
            put("PW2", Station.create("Teck Lee", "PW2", "PW2", "1.41280", "103.906233"));
            put("PW3", Station.create("Punggol Point", "PW3", "PW3", "1.418104", "103.906559"));
            put("PW4", Station.create("Samudera", "PW4", "PW4", "1.417075", "103.90231"));
            put("PW5", Station.create("Nibong", "PW5", "PW5", "1.413042", "103.900293"));
            put("PW6", Station.create("Sumang", "PW6", "PW6", "1.409524", "103.898490"));
            put("PW7", Station.create("Soo Teck", "PW7", "PW7", "1.405700", "103.897246"));

            // Bukit Panjang LRT
            put("BP2", Station.create("South View", "BP2", "BP2", "1.380293", "103.745294"));
            put("BP3", Station.create("Keat Hong", "BP3", "BP3", "1.378601", "103.749057"));
            put("BP4", Station.create("Teck Whye", "BP4", "BP4", "1.376641", "103.753695"));
            put("BP5", Station.create("Phoenix", "BP5", "BP5", "1.378618", "103.758033"));
            put("BP7", Station.create("Petir", "BP7", "BP7", "1.377753", "103.766665"));
            put("BP8", Station.create("Pending", "BP8", "BP8", "1.376068", "103.770917"));
            put("BP9", Station.create("Bangkit", "BP9", "BP9", "1.380013", "103.772658"));
            put("BP10", Station.create("Fajar", "BP10", "BP10", "1.384524", "103.770824"));
            put("BP11", Station.create("Segar", "BP11", "BP11", "1.387772", "103.769598"));
            put("BP12", Station.create("Jelapang", "BP12", "BP12", "1.386691", "103.764494"));
            put("BP13", Station.create("Senja", "BP13", "BP13", "1.382700898", "103.762363"));
            put("BP14", Station.create("Ten Mile Junction", "BP14", "BP14", "1.380349", "103.760129"));
        }
    };

    private EZLinkData() {
    }

    @Nullable
    static Station getStation(String code) {
        return MRT_STATIONS.get(code);
    }

    @NonNull
    static String getCardIssuer(@NonNull String canNo) {
        int issuerId = Integer.parseInt(canNo.substring(0, 3));
        switch (issuerId) {
            case 100:
                return "EZ-Link";
            case 111:
                return "NETS";
            default:
                return "CEPAS";
        }
    }
}
