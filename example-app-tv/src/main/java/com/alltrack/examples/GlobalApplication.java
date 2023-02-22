package com.alltrack.examples;

import android.app.Activity;
import android.app.Application;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.alltrack.sdk.Alltrack;
import com.alltrack.sdk.AlltrackAttribution;
import com.alltrack.sdk.AlltrackConfig;
import com.alltrack.sdk.AlltrackEventFailure;
import com.alltrack.sdk.AlltrackEventSuccess;
import com.alltrack.sdk.LogLevel;
import com.alltrack.sdk.OnAttributionChangedListener;
import com.alltrack.sdk.OnDeeplinkResponseListener;
import com.alltrack.sdk.OnEventTrackingFailedListener;
import com.alltrack.sdk.OnEventTrackingSucceededListener;
import com.alltrack.sdk.OnSessionTrackingFailedListener;
import com.alltrack.sdk.OnSessionTrackingSucceededListener;
import com.alltrack.sdk.AlltrackSessionFailure;
import com.alltrack.sdk.AlltrackSessionSuccess;

public class GlobalApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Configure alltrack SDK.
        String appToken = "2fm9gkqubvpc";
        String environment = AlltrackConfig.ENVIRONMENT_SANDBOX;

        AlltrackConfig config = new AlltrackConfig(this, appToken, environment);

        // Change the log level.
        config.setLogLevel(LogLevel.VERBOSE);

        // Set attribution delegate.
        config.setOnAttributionChangedListener(new OnAttributionChangedListener() {
            @Override
            public void onAttributionChanged(AlltrackAttribution attribution) {
                Log.d("example", "Attribution callback called!");
                Log.d("example", "Attribution: " + attribution.toString());
            }
        });

        // Set event success tracking delegate.
        config.setOnEventTrackingSucceededListener(new OnEventTrackingSucceededListener() {
            @Override
            public void onFinishedEventTrackingSucceeded(AlltrackEventSuccess eventSuccessResponseData) {
                Log.d("example", "Event success callback called!");
                Log.d("example", "Event success data: " + eventSuccessResponseData.toString());
            }
        });

        // Set event failure tracking delegate.
        config.setOnEventTrackingFailedListener(new OnEventTrackingFailedListener() {
            @Override
            public void onFinishedEventTrackingFailed(AlltrackEventFailure eventFailureResponseData) {
                Log.d("example", "Event failure callback called!");
                Log.d("example", "Event failure data: " + eventFailureResponseData.toString());
            }
        });

        // Set session success tracking delegate.
        config.setOnSessionTrackingSucceededListener(new OnSessionTrackingSucceededListener() {
            @Override
            public void onFinishedSessionTrackingSucceeded(AlltrackSessionSuccess sessionSuccessResponseData) {
                Log.d("example", "Session success callback called!");
                Log.d("example", "Session success data: " + sessionSuccessResponseData.toString());
            }
        });

        // Set session failure tracking delegate.
        config.setOnSessionTrackingFailedListener(new OnSessionTrackingFailedListener() {
            @Override
            public void onFinishedSessionTrackingFailed(AlltrackSessionFailure sessionFailureResponseData) {
                Log.d("example", "Session failure callback called!");
                Log.d("example", "Session failure data: " + sessionFailureResponseData.toString());
            }
        });

        // Evaluate deferred deep link to be launched.
        config.setOnDeeplinkResponseListener(new OnDeeplinkResponseListener() {
            @Override
            public boolean launchReceivedDeeplink(Uri deeplink) {
                Log.d("example", "Deferred deep link callback called!");
                Log.d("example", "Deep link URL: " + deeplink);

                return true;
            }
        });

        // Set default tracker.
        // config.setDefaultTracker("{YourDefaultTracker}");

        // Set process name.
        // config.setProcessName("com.alltrack.examples");

        // Allow to send in the background.
        config.setSendInBackground(true);

        // Enable event buffering.
        // config.setEventBufferingEnabled(true);

        // Delay first session.
        // config.setDelayStart(7);

        // Add session callback parameters.
        Alltrack.addSessionCallbackParameter("sc_foo", "sc_bar");
        Alltrack.addSessionCallbackParameter("sc_key", "sc_value");

        // Add session partner parameters.
        Alltrack.addSessionPartnerParameter("sp_foo", "sp_bar");
        Alltrack.addSessionPartnerParameter("sp_key", "sp_value");

        // Remove session callback parameters.
        Alltrack.removeSessionCallbackParameter("sc_foo");

        // Remove session partner parameters.
        Alltrack.removeSessionPartnerParameter("sp_key");

        // Remove all session callback parameters.
        Alltrack.resetSessionCallbackParameters();

        // Remove all session partner parameters.
        Alltrack.resetSessionPartnerParameters();

        // Initialise the alltrack SDK.
        Alltrack.onCreate(config);

        // Abort delay for the first session introduced with setDelayStart method.
        // Alltrack.sendFirstPackages();

        // Register onResume and onPause events of all activities
        // for applications with minSdkVersion >= 14.
        registerActivityLifecycleCallbacks(new AlltrackLifecycleCallbacks());

        // Put the SDK in offline mode.
        // Alltrack.setOfflineMode(true);

        // Disable the SDK
        // Alltrack.setEnabled(false);

        // Send push notification token.
        // Alltrack.setPushToken("token");
    }

    // You can use this class if your app is for Android 4.0 or higher
    private static final class AlltrackLifecycleCallbacks implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityResumed(Activity activity) {
            Alltrack.onResume();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Alltrack.onPause();
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }
    }
}
