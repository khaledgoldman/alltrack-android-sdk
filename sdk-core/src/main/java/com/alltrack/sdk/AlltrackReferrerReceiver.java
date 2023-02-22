package com.alltrack.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import static com.alltrack.sdk.Constants.REFERRER;

// support multiple BroadcastReceivers for the INSTALL_REFERRER:
// https://appington.wordpress.com/2012/08/01/giving-credit-for-android-app-installs/

public class AlltrackReferrerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String rawReferrer = intent.getStringExtra(REFERRER);

        if (null == rawReferrer) {
            return;
        }

        Alltrack.getDefaultInstance().sendReferrer(rawReferrer, context);
    }
}
