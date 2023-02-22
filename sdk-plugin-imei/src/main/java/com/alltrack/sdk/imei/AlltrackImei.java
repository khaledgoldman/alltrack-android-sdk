package com.alltrack.sdk.imei;

public class AlltrackImei {
    static boolean isImeiToBeRead = false;

    public static void readImei() {
        AlltrackImei.isImeiToBeRead = true;
    }

    public static void doNotReadImei() {
        AlltrackImei.isImeiToBeRead = false;
    }
}
