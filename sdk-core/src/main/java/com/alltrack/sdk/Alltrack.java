package com.alltrack.sdk;

import android.content.Context;
import android.net.Uri;

import org.json.JSONObject;

import java.util.Map;

/**
 * The main interface to Alltrack.
 * Use the methods of this class to tell Alltrack about the usage of your app.
 * See the README for details.
 *
 * @author Christian Wellenbrock (@wellle)
 * @since 11th November 2011
 */
public class Alltrack {
    /**
     * Singleton Alltrack SDK instance.
     */
    private static AlltrackInstance defaultInstance;

    /**
     * Private constructor.
     */
    private Alltrack() {
    }

    /**
     * Method used to obtain Alltrack SDK singleton instance.
     *
     * @return Alltrack SDK singleton instance.
     */
    public static synchronized AlltrackInstance getDefaultInstance() {
        @SuppressWarnings("unused")
        String VERSION = "!SDK-VERSION-STRING!:com.alltrack.sdk:alltrack-android:0.0.1";

        if (defaultInstance == null) {
            defaultInstance = new AlltrackInstance();
        }
        return defaultInstance;
    }

    /**
     * Called upon SDK initialisation.
     *
     * @param alltrackConfig AlltrackConfig object used for SDK initialisation
     */
    public static void onCreate(AlltrackConfig alltrackConfig) {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.onCreate(alltrackConfig);
    }

    /**
     * Called to track event.
     *
     * @param event AlltrackEvent object to be tracked
     */
    public static void trackEvent(AlltrackEvent event) {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.trackEvent(event);
    }

    /**
     * Called upon each Activity's onResume() method call.
     */
    public static void onResume() {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.onResume();
    }

    /**
     * Called upon each Activity's onPause() method call.
     */
    public static void onPause() {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.onPause();
    }

    /**
     * Called to disable/enable SDK.
     *
     * @param enabled boolean indicating whether SDK should be enabled or disabled
     */
    public static void setEnabled(boolean enabled) {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.setEnabled(enabled);
    }

    /**
     * Get information if SDK is enabled or not.
     *
     * @return boolean indicating whether SDK is enabled or not
     */
    public static boolean isEnabled() {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        return alltrackInstance.isEnabled();
    }

    /**
     * Get information if the payload originates from Alltrack.
     *
     * @return boolean indicating whether payload originates from Alltrack or not.
     */
    public static boolean isAlltrackUninstallDetectionPayload(Map<String, String> payload) {
        return Util.isAlltrackUninstallDetectionPayload(payload);
    }

    /**
     * Called to process deep link.
     *
     * @param url Deep link URL to process
     *
     * @deprecated Use {@link #appWillOpenUrl(Uri, Context)}} instead.
     */
    @Deprecated
    public static void appWillOpenUrl(Uri url) {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.appWillOpenUrl(url);
    }

    /**
     * Called to process deep link.
     *
     * @param url Deep link URL to process
     * @param context Application context
     */
    public static void appWillOpenUrl(Uri url, Context context) {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.appWillOpenUrl(url, extractApplicationContext(context));
    }

    /**
     * Called to process referrer information sent with INSTALL_REFERRER intent.
     *
     * @param referrer Referrer content
     * @param context  Application context
     */
    public static void setReferrer(String referrer, Context context) {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.sendReferrer(referrer, extractApplicationContext(context));
    }

    /**
     * Called to set SDK to offline or online mode.
     *
     * @param enabled boolean indicating should SDK be in offline mode (true) or not (false)
     */
    public static void setOfflineMode(boolean enabled) {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.setOfflineMode(enabled);
    }

    /**
     * Called if SDK initialisation was delayed and you would like to stop waiting for timer.
     */
    public static void sendFirstPackages() {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.sendFirstPackages();
    }

    /**
     * Called to add global callback parameter that will be sent with each session and event.
     *
     * @param key   Global callback parameter key
     * @param value Global callback parameter value
     */
    public static void addSessionCallbackParameter(String key, String value) {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.addSessionCallbackParameter(key, value);
    }

    /**
     * Called to add global partner parameter that will be sent with each session and event.
     *
     * @param key   Global partner parameter key
     * @param value Global partner parameter value
     */
    public static void addSessionPartnerParameter(String key, String value) {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.addSessionPartnerParameter(key, value);
    }

    /**
     * Called to remove global callback parameter from session and event packages.
     *
     * @param key Global callback parameter key
     */
    public static void removeSessionCallbackParameter(String key) {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.removeSessionCallbackParameter(key);
    }

    /**
     * Called to remove global partner parameter from session and event packages.
     *
     * @param key Global partner parameter key
     */
    public static void removeSessionPartnerParameter(String key) {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.removeSessionPartnerParameter(key);
    }

    /**
     * Called to remove all added global callback parameters.
     */
    public static void resetSessionCallbackParameters() {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.resetSessionCallbackParameters();
    }

    /**
     * Called to remove all added global partner parameters.
     */
    public static void resetSessionPartnerParameters() {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.resetSessionPartnerParameters();
    }

    /**
     * Called to set user's push notifications token.
     *
     * @param token Push notifications token
     * @deprecated use {@link #setPushToken(String, Context)} instead.
     */
    public static void setPushToken(String token) {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.setPushToken(token);
    }

    /**
     * Called to set user's push notifications token.
     *
     * @param token   Push notifications token
     * @param context Application context
     */
    public static void setPushToken(final String token, final Context context) {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.setPushToken(token, extractApplicationContext(context));
    }

    /**
     * Called to forget the user in accordance with GDPR law.
     *
     * @param context Application context
     */
    public static void gdprForgetMe(final Context context) {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.gdprForgetMe(extractApplicationContext(context));
    }

    /**
     * Called to disable the third party sharing.
     *
     * @param context Application context
     */
    public static void disableThirdPartySharing(final Context context) {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.disableThirdPartySharing(extractApplicationContext(context));
    }

    public static void trackThirdPartySharing(
            final AlltrackThirdPartySharing alltrackThirdPartySharing)
    {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.trackThirdPartySharing(alltrackThirdPartySharing);
    }

    public static void trackMeasurementConsent(final boolean consentMeasurement) {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.trackMeasurementConsent(consentMeasurement);
    }

    /**
     * Track ad revenue from a source provider
     *
     * @param source Source of ad revenue information, see AlltrackConfig.AD_REVENUE_* for some possible sources
     * @param payload JsonObject content of the ad revenue information
     */
    public static void trackAdRevenue(final String source, final JSONObject payload) {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.trackAdRevenue(source, payload);
    }

    /**
     * Track ad revenue from a source provider
     *
     * @param alltrackAdRevenue Alltrack ad revenue information like source, revenue, currency etc
     */
    public static void trackAdRevenue(final AlltrackAdRevenue alltrackAdRevenue) {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.trackAdRevenue(alltrackAdRevenue);
    }

    /**
     * Track subscription from Google Play.
     *
     * @param subscription AlltrackPlayStoreSubscription object to be tracked
     */
    public static void trackPlayStoreSubscription(final AlltrackPlayStoreSubscription subscription) {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.trackPlayStoreSubscription(subscription);
    }

    /**
     * Called to get value of Google Play Advertising Identifier.
     *
     * @param context        Application context
     * @param onDeviceIdRead Callback to get triggered once identifier is obtained
     */
    public static void getGoogleAdId(Context context, OnDeviceIdsRead onDeviceIdRead) {
        Context appContext = null;
        if (context != null) {
            appContext = context.getApplicationContext();
        }

        Util.getGoogleAdId(appContext, onDeviceIdRead);
    }

    /**
     * Called to get value of Amazon Advertising Identifier.
     *
     * @param context Application context
     * @return Amazon Advertising Identifier
     */
    public static String getAmazonAdId(final Context context) {
        Context appContext = extractApplicationContext(context);
        if (appContext != null) {
            return Util.getFireAdvertisingId(appContext.getContentResolver());
        }

        return null;
    }

    /**
     * Called to get value of unique Alltrack device identifier.
     *
     * @return Unique Alltrack device indetifier
     */
    public static String getAdid() {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        return alltrackInstance.getAdid();
    }

    /**
     * Called to get user's current attribution value.
     *
     * @return AlltrackAttribution object with current attribution value
     */
    public static AlltrackAttribution getAttribution() {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        return alltrackInstance.getAttribution();
    }

    /**
     * Called to get native SDK version string.
     *
     * @return Native SDK version string.
     */
    public static String getSdkVersion() {
        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        return alltrackInstance.getSdkVersion();
    }

    /**
     * Used for testing purposes only. Do NOT use this method.
     *
     * @param testOptions Alltrack integration tests options
     */
    public static void setTestOptions(AlltrackTestOptions testOptions) {
        if (testOptions.teardown != null && testOptions.teardown.booleanValue()) {
            if (defaultInstance != null) {
                defaultInstance.teardown();
            }
            defaultInstance = null;
            AlltrackFactory.teardown(testOptions.context);
        }

        AlltrackInstance alltrackInstance = Alltrack.getDefaultInstance();
        alltrackInstance.setTestOptions(testOptions);
    }

    private static Context extractApplicationContext(final Context context) {
        if (context == null) {
            return null;
        }

        return context.getApplicationContext();
    }
}
