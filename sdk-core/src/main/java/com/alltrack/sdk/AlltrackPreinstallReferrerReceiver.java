package com.alltrack.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.alltrack.sdk.Constants.EXTRA_SYSTEM_INSTALLER_REFERRER;

public class AlltrackPreinstallReferrerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        String referrer = intent.getStringExtra(EXTRA_SYSTEM_INSTALLER_REFERRER);
        if (referrer == null) {
            return;
        }

        Alltrack.getDefaultInstance().sendPreinstallReferrer(referrer, context);
    }
}
