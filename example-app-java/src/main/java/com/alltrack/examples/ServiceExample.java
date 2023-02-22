package com.alltrack.examples;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.alltrack.sdk.Alltrack;
import com.alltrack.sdk.AlltrackEvent;

/**
 * Created by pfms on 16/03/16.
 */
public class ServiceExample extends Service {
    private static final String EVENT_TOKEN_BACKGROUND = "pkd28h";

    private static boolean flip = true;

    public ServiceExample() {
        super();
        Log.d("example", "ServiceExample constructor");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("example", "ServiceExample onBind");

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("example", "ServiceExample onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int startDefaultOption = super.onStartCommand(intent, flags, startId);
        Log.d("example", "ServiceExample onStartCommand");

        if (flip) {
            Alltrack.setEnabled(false);
            flip = false;
        } else {
            Alltrack.setEnabled(true);
            flip = true;
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Log.d("example", "ServiceExample background sleeping");
                SystemClock.sleep(3000);
                Log.d("example", "ServiceExample background awake");

                AlltrackEvent event = new AlltrackEvent(EVENT_TOKEN_BACKGROUND);
                Alltrack.trackEvent(event);

                Log.d("example", "ServiceExample background event tracked");

                return null;
            }
        }.execute();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("example", "ServiceExample onDestroy");
    }
}
