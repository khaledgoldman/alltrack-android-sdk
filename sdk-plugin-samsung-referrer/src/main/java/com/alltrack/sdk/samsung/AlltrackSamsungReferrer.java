package com.alltrack.sdk.samsung;

import android.content.Context;

public class AlltrackSamsungReferrer {

   static boolean shouldReadSamsungReferrer = true;

   public static void readSamsungReferrer(Context context) {
      shouldReadSamsungReferrer = true;
   }

   public static void doNotReadSamsungReferrer() {
      shouldReadSamsungReferrer = false;
   }
}
