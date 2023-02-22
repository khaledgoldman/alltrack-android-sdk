package com.alltrack.sdk.webbridge;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.app.Application;

import com.alltrack.sdk.Alltrack;
import com.alltrack.sdk.AlltrackAttribution;
import com.alltrack.sdk.AlltrackConfig;
import com.alltrack.sdk.AlltrackEvent;
import com.alltrack.sdk.AlltrackEventFailure;
import com.alltrack.sdk.AlltrackEventSuccess;
import com.alltrack.sdk.AlltrackFactory;
import com.alltrack.sdk.AlltrackSessionFailure;
import com.alltrack.sdk.AlltrackSessionSuccess;
import com.alltrack.sdk.AlltrackTestOptions;
import com.alltrack.sdk.AlltrackThirdPartySharing;
import com.alltrack.sdk.LogLevel;
import com.alltrack.sdk.OnAttributionChangedListener;
import com.alltrack.sdk.OnDeeplinkResponseListener;
import com.alltrack.sdk.OnDeviceIdsRead;
import com.alltrack.sdk.OnEventTrackingFailedListener;
import com.alltrack.sdk.OnEventTrackingSucceededListener;
import com.alltrack.sdk.OnSessionTrackingFailedListener;
import com.alltrack.sdk.OnSessionTrackingSucceededListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by uerceg on 22/07/16.
 */
public class AlltrackBridgeInstance {
    private static final String LOG_LEVEL_VERBOSE = "VERBOSE";
    private static final String LOG_LEVEL_DEBUG = "DEBUG";
    private static final String LOG_LEVEL_INFO = "INFO";
    private static final String LOG_LEVEL_WARN = "WARN";
    private static final String LOG_LEVEL_ERROR = "ERROR";
    private static final String LOG_LEVEL_ASSERT = "ASSERT";
    private static final String LOG_LEVEL_SUPPRESS = "SUPPRESS";

    private static final String JAVASCRIPT_INTERFACE_NAME = "AlltrackBridge";
    private static final String FB_JAVASCRIPT_INTERFACE_NAME_PREFIX = "fbmq_";

    private WebView webView;
    private Application application;
    private boolean isInitialized = false;
    private boolean shouldDeferredDeeplinkBeLaunched = true;
    private FacebookSDKJSInterface facebookSDKJSInterface = null;

    AlltrackBridgeInstance() {}

    AlltrackBridgeInstance(Application application, WebView webView) {
        this.application = application;
        setWebView(webView);
    }

    // Automatically subscribe to Android lifecycle callbacks to properly handle session tracking.
    // This requires user to have minimal supported API level set to 14.
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static final class AlltrackLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityResumed(Activity activity) {
            Alltrack.onResume();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Alltrack.onPause();
        }

        @Override
        public void onActivityStopped(Activity activity) {}

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

        @Override
        public void onActivityDestroyed(Activity activity) {}

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

        @Override
        public void onActivityStarted(Activity activity) {}
    }

    private boolean isInitialized() {
        if (webView == null) {
            AlltrackBridgeUtil.getLogger().error("Webview missing. Call AlltrackBridge.setWebView before");
            return false;
        }
        if (application == null) {
            AlltrackBridgeUtil.getLogger().error("Application context missing. Call AlltrackBridge.setApplicationContext before");
            return false;
        }
        return true;
    }

    public void registerFacebookSDKJSInterface() {
        // Configure the web view to add fb pixel interface
        String fbApplicationId = FacebookSDKJSInterface.getApplicationId(application.getApplicationContext());
        AlltrackFactory.getLogger().info("AlltrackBridgeInstance fbApplicationId: %s", fbApplicationId);

        if (fbApplicationId == null) {
            return;
        }

        this.facebookSDKJSInterface = new FacebookSDKJSInterface();

        // Add FB pixel to JS interface.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            this.webView.addJavascriptInterface(facebookSDKJSInterface,
                                                FB_JAVASCRIPT_INTERFACE_NAME_PREFIX
                                                + fbApplicationId
                                               );
        }
    }

    @JavascriptInterface
    public void onCreate(String alltrackConfigString) {
        // Initialise SDK only if it's not already initialised.
        if (isInitialized) {
            AlltrackBridgeUtil.getLogger().warn("Alltrack bridge is already initialized. Ignoring further attempts");
            return;
        }
        if (!isInitialized()) {
            return;
        }

        try {
            AlltrackBridgeUtil.getLogger().verbose("Web bridge onCreate alltrackConfigString: " + alltrackConfigString);

            JSONObject jsonAlltrackConfig = new JSONObject(alltrackConfigString);
            Object appTokenField = jsonAlltrackConfig.get("appToken");
            Object environmentField = jsonAlltrackConfig.get("environment");
            Object allowSuppressLogLevelField = jsonAlltrackConfig.get("allowSuppressLogLevel");
            Object eventBufferingEnabledField = jsonAlltrackConfig.get("eventBufferingEnabled");
            Object sendInBackgroundField = jsonAlltrackConfig.get("sendInBackground");
            Object logLevelField = jsonAlltrackConfig.get("logLevel");
            Object sdkPrefixField = jsonAlltrackConfig.get("sdkPrefix");
            Object processNameField = jsonAlltrackConfig.get("processName");
            Object defaultTrackerField = jsonAlltrackConfig.get("defaultTracker");
            Object externalDeviceIdField = jsonAlltrackConfig.get("externalDeviceId");
            Object attributionCallbackNameField = jsonAlltrackConfig.get("attributionCallbackName");
            Object deviceKnownField = jsonAlltrackConfig.get("deviceKnown");
            Object needsCostField = jsonAlltrackConfig.get("needsCost");
            Object eventSuccessCallbackNameField = jsonAlltrackConfig.get("eventSuccessCallbackName");
            Object eventFailureCallbackNameField = jsonAlltrackConfig.get("eventFailureCallbackName");
            Object sessionSuccessCallbackNameField = jsonAlltrackConfig.get("sessionSuccessCallbackName");
            Object sessionFailureCallbackNameField = jsonAlltrackConfig.get("sessionFailureCallbackName");
            Object openDeferredDeeplinkField = jsonAlltrackConfig.get("openDeferredDeeplink");
            Object deferredDeeplinkCallbackNameField = jsonAlltrackConfig.get("deferredDeeplinkCallbackName");
            Object delayStartField = jsonAlltrackConfig.get("delayStart");
            Object userAgentField = jsonAlltrackConfig.get("userAgent");
            Object secretIdField = jsonAlltrackConfig.get("secretId");
            Object info1Field = jsonAlltrackConfig.get("info1");
            Object info2Field = jsonAlltrackConfig.get("info2");
            Object info3Field = jsonAlltrackConfig.get("info3");
            Object info4Field = jsonAlltrackConfig.get("info4");
            Object fbPixelDefaultEventTokenField = jsonAlltrackConfig.get("fbPixelDefaultEventToken");
            Object fbPixelMappingField = jsonAlltrackConfig.get("fbPixelMapping");
            Object urlStrategyField = jsonAlltrackConfig.get("urlStrategy");
            Object preinstallTrackingEnabledField = jsonAlltrackConfig.get("preinstallTrackingEnabled");
            Object preinstallFilePathField = jsonAlltrackConfig.get("preinstallFilePath");
            Object playStoreKidsAppEnabledField = jsonAlltrackConfig.get("playStoreKidsAppEnabled");
            Object coppaCompliantEnabledField = jsonAlltrackConfig.get("coppaCompliantEnabled");

            String appToken = AlltrackBridgeUtil.fieldToString(appTokenField);
            String environment = AlltrackBridgeUtil.fieldToString(environmentField);
            Boolean allowSuppressLogLevel = AlltrackBridgeUtil.fieldToBoolean(allowSuppressLogLevelField);

            AlltrackConfig alltrackConfig;
            if (allowSuppressLogLevel == null) {
                alltrackConfig = new AlltrackConfig(application.getApplicationContext(), appToken, environment);
            } else {
                alltrackConfig = new AlltrackConfig(application.getApplicationContext(), appToken, environment, allowSuppressLogLevel.booleanValue());
            }

            if (!alltrackConfig.isValid()) {
                return;
            }

            // Event buffering
            Boolean eventBufferingEnabled = AlltrackBridgeUtil.fieldToBoolean(eventBufferingEnabledField);
            if (eventBufferingEnabled != null) {
                alltrackConfig.setEventBufferingEnabled(eventBufferingEnabled);
            }

            // Send in the background
            Boolean sendInBackground = AlltrackBridgeUtil.fieldToBoolean(sendInBackgroundField);
            if (sendInBackground != null) {
                alltrackConfig.setSendInBackground(sendInBackground);
            }

            // Log level
            String logLevelString = AlltrackBridgeUtil.fieldToString(logLevelField);
            if (logLevelString != null) {
                if (logLevelString.equalsIgnoreCase(LOG_LEVEL_VERBOSE)) {
                    alltrackConfig.setLogLevel(LogLevel.VERBOSE);
                } else if (logLevelString.equalsIgnoreCase(LOG_LEVEL_DEBUG)) {
                    alltrackConfig.setLogLevel(LogLevel.DEBUG);
                } else if (logLevelString.equalsIgnoreCase(LOG_LEVEL_INFO)) {
                    alltrackConfig.setLogLevel(LogLevel.INFO);
                } else if (logLevelString.equalsIgnoreCase(LOG_LEVEL_WARN)) {
                    alltrackConfig.setLogLevel(LogLevel.WARN);
                } else if (logLevelString.equalsIgnoreCase(LOG_LEVEL_ERROR)) {
                    alltrackConfig.setLogLevel(LogLevel.ERROR);
                } else if (logLevelString.equalsIgnoreCase(LOG_LEVEL_ASSERT)) {
                    alltrackConfig.setLogLevel(LogLevel.ASSERT);
                } else if (logLevelString.equalsIgnoreCase(LOG_LEVEL_SUPPRESS)) {
                    alltrackConfig.setLogLevel(LogLevel.SUPRESS);
                }
            }

            // SDK prefix
            String sdkPrefix = AlltrackBridgeUtil.fieldToString(sdkPrefixField);
            if (sdkPrefix != null) {
                alltrackConfig.setSdkPrefix(sdkPrefix);
            }

            // Main process name
            String processName = AlltrackBridgeUtil.fieldToString(processNameField);
            if (processName != null) {
                alltrackConfig.setProcessName(processName);
            }

            // Default tracker
            String defaultTracker = AlltrackBridgeUtil.fieldToString(defaultTrackerField);
            if (defaultTracker != null) {
                alltrackConfig.setDefaultTracker(defaultTracker);
            }

            // External device ID
            String externalDeviceId = AlltrackBridgeUtil.fieldToString(externalDeviceIdField);
            if (externalDeviceId != null) {
                alltrackConfig.setExternalDeviceId(externalDeviceId);
            }

            // Attribution callback name
            final String attributionCallbackName = AlltrackBridgeUtil.fieldToString(attributionCallbackNameField);
            if (attributionCallbackName != null) {
                alltrackConfig.setOnAttributionChangedListener(new OnAttributionChangedListener() {
                    @Override
                    public void onAttributionChanged(AlltrackAttribution attribution) {
                        AlltrackBridgeUtil.execAttributionCallbackCommand(webView, attributionCallbackName, attribution);
                    }
                });
            }

            // Is device known
            Boolean deviceKnown = AlltrackBridgeUtil.fieldToBoolean(deviceKnownField);
            if (deviceKnown != null) {
                alltrackConfig.setDeviceKnown(deviceKnown);
            }

            // Needs cost
            Boolean needsCost = AlltrackBridgeUtil.fieldToBoolean(needsCostField);
            if (needsCost != null) {
                alltrackConfig.setNeedsCost(needsCost);
            }

            // Event success callback
            final String eventSuccessCallbackName = AlltrackBridgeUtil.fieldToString(eventSuccessCallbackNameField);
            if (eventSuccessCallbackName != null) {
                alltrackConfig.setOnEventTrackingSucceededListener(new OnEventTrackingSucceededListener() {
                    public void onFinishedEventTrackingSucceeded(AlltrackEventSuccess eventSuccessResponseData) {
                        AlltrackBridgeUtil.execEventSuccessCallbackCommand(webView, eventSuccessCallbackName, eventSuccessResponseData);
                    }
                });
            }

            // Event failure callback
            final String eventFailureCallbackName = AlltrackBridgeUtil.fieldToString(eventFailureCallbackNameField);
            if (eventFailureCallbackName != null) {
                alltrackConfig.setOnEventTrackingFailedListener(new OnEventTrackingFailedListener() {
                    public void onFinishedEventTrackingFailed(AlltrackEventFailure eventFailureResponseData) {
                        AlltrackBridgeUtil.execEventFailureCallbackCommand(webView, eventFailureCallbackName, eventFailureResponseData);
                    }
                });
            }

            // Session success callback
            final String sessionSuccessCallbackName = AlltrackBridgeUtil.fieldToString(sessionSuccessCallbackNameField);
            if (sessionSuccessCallbackName != null) {
                alltrackConfig.setOnSessionTrackingSucceededListener(new OnSessionTrackingSucceededListener() {
                    @Override
                    public void onFinishedSessionTrackingSucceeded(AlltrackSessionSuccess sessionSuccessResponseData) {
                        AlltrackBridgeUtil.execSessionSuccessCallbackCommand(webView, sessionSuccessCallbackName, sessionSuccessResponseData);
                    }
                });
            }

            // Session failure callback
            final String sessionFailureCallbackName = AlltrackBridgeUtil.fieldToString(sessionFailureCallbackNameField);
            if (sessionFailureCallbackName != null) {
                alltrackConfig.setOnSessionTrackingFailedListener(new OnSessionTrackingFailedListener() {
                    @Override
                    public void onFinishedSessionTrackingFailed(AlltrackSessionFailure failureResponseData) {
                        AlltrackBridgeUtil.execSessionFailureCallbackCommand(webView, sessionFailureCallbackName, failureResponseData);
                    }
                });
            }

            // Should deferred deep link be opened?
            Boolean openDeferredDeeplink = AlltrackBridgeUtil.fieldToBoolean(openDeferredDeeplinkField);
            if (openDeferredDeeplink != null) {
                shouldDeferredDeeplinkBeLaunched = openDeferredDeeplink;
            }

            // Deferred deeplink callback
            final String deferredDeeplinkCallbackName = AlltrackBridgeUtil.fieldToString(deferredDeeplinkCallbackNameField);
            if (deferredDeeplinkCallbackName != null) {
                alltrackConfig.setOnDeeplinkResponseListener(new OnDeeplinkResponseListener() {
                    @Override
                    public boolean launchReceivedDeeplink(Uri deeplink) {
                        AlltrackBridgeUtil.execSingleValueCallback(webView, deferredDeeplinkCallbackName, deeplink.toString());
                        return shouldDeferredDeeplinkBeLaunched;
                    }
                });
            }

            // Delay start
            Double delayStart = AlltrackBridgeUtil.fieldToDouble(delayStartField);
            if (delayStart != null) {
                alltrackConfig.setDelayStart(delayStart);
            }

            // User agent
            String userAgent = AlltrackBridgeUtil.fieldToString(userAgentField);
            if (userAgent != null) {
                alltrackConfig.setUserAgent(userAgent);
            }

            // App secret
            Long secretId = AlltrackBridgeUtil.fieldToLong(secretIdField);
            Long info1 = AlltrackBridgeUtil.fieldToLong(info1Field);
            Long info2 = AlltrackBridgeUtil.fieldToLong(info2Field);
            Long info3 = AlltrackBridgeUtil.fieldToLong(info3Field);
            Long info4 = AlltrackBridgeUtil.fieldToLong(info4Field);
            if (secretId != null && info1 != null && info2 != null && info3 != null && info4 != null) {
                alltrackConfig.setAppSecret(secretId, info1, info2, info3, info4);
            }

            // Check Pixel Default Event Token
            String fbPixelDefaultEventToken = AlltrackBridgeUtil.fieldToString(fbPixelDefaultEventTokenField);
            if (fbPixelDefaultEventToken != null && this.facebookSDKJSInterface != null) {
                this.facebookSDKJSInterface.setDefaultEventToken(fbPixelDefaultEventToken);
            }

            // Add Pixel mappings
            try {
                String[] fbPixelMapping = AlltrackBridgeUtil.jsonArrayToArray((JSONArray)fbPixelMappingField);
                if (fbPixelMapping != null && this.facebookSDKJSInterface != null) {
                    for (int i = 0; i < fbPixelMapping.length; i += 2) {
                        String key = fbPixelMapping[i];
                        String value = fbPixelMapping[i+1];
                        this.facebookSDKJSInterface.addFbPixelEventTokenMapping(key, value);
                    }
                }
            } catch (Exception e) {
                AlltrackFactory.getLogger().error("AlltrackBridgeInstance.configureFbPixel: %s", e.getMessage());
            }

            // Set url strategy
            String urlStrategy = AlltrackBridgeUtil.fieldToString(urlStrategyField);
            if (urlStrategy != null) {
                alltrackConfig.setUrlStrategy(urlStrategy);
            }

            // Preinstall tracking
            Boolean preinstallTrackingEnabled = AlltrackBridgeUtil.fieldToBoolean(preinstallTrackingEnabledField);
            if (preinstallTrackingEnabled != null) {
                alltrackConfig.setPreinstallTrackingEnabled(preinstallTrackingEnabled);
            }

            // Preinstall secondary file path
            String preinstallFilePath = AlltrackBridgeUtil.fieldToString(preinstallFilePathField);
            if (preinstallFilePath != null) {
                alltrackConfig.setPreinstallFilePath(preinstallFilePath);
            }

            // PlayStore Kids app
            Boolean playStoreKidsAppEnabled = AlltrackBridgeUtil.fieldToBoolean(playStoreKidsAppEnabledField);
            if (playStoreKidsAppEnabled != null) {
                alltrackConfig.setPlayStoreKidsAppEnabled(playStoreKidsAppEnabled);
            }

            // Coppa compliant
            Boolean coppaCompliantEnabled = AlltrackBridgeUtil.fieldToBoolean(coppaCompliantEnabledField);
            if (coppaCompliantEnabled != null) {
                alltrackConfig.setCoppaCompliantEnabled(coppaCompliantEnabled);
            }

            // Manually call onResume() because web view initialisation will happen a bit delayed.
            // With this delay, it will miss lifecycle callback onResume() initial firing.
            Alltrack.onCreate(alltrackConfig);
            Alltrack.onResume();

            isInitialized = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                application.registerActivityLifecycleCallbacks(new AlltrackLifecycleCallbacks());
            }
        } catch (Exception e) {
            AlltrackFactory.getLogger().error("AlltrackBridgeInstance onCreate: %s", e.getMessage());
        }
    }

    @JavascriptInterface
    public void trackEvent(String alltrackEventString) {
        if (!isInitialized()) {
            return;
        }

        try {
            JSONObject jsonAlltrackEvent = new JSONObject(alltrackEventString);

            Object eventTokenField = jsonAlltrackEvent.get("eventToken");
            Object revenueField = jsonAlltrackEvent.get("revenue");
            Object currencyField = jsonAlltrackEvent.get("currency");
            Object callbackParametersField = jsonAlltrackEvent.get("callbackParameters");
            Object partnerParametersField = jsonAlltrackEvent.get("partnerParameters");
            Object orderIdField = jsonAlltrackEvent.get("orderId");
            Object callbackIdField = jsonAlltrackEvent.get("callbackId");

            String eventToken = AlltrackBridgeUtil.fieldToString(eventTokenField);
            AlltrackEvent alltrackEvent = new AlltrackEvent(eventToken);

            if (!alltrackEvent.isValid()) {
                return;
            }

            // Revenue
            Double revenue = AlltrackBridgeUtil.fieldToDouble(revenueField);
            String currency = AlltrackBridgeUtil.fieldToString(currencyField);
            if (revenue != null && currency != null) {
                alltrackEvent.setRevenue(revenue, currency);
            }

            // Callback parameters
            String[] callbackParameters = AlltrackBridgeUtil.jsonArrayToArray((JSONArray)callbackParametersField);
            if (callbackParameters != null) {
                for (int i = 0; i < callbackParameters.length; i += 2) {
                    String key = callbackParameters[i];
                    String value = callbackParameters[i+1];
                    alltrackEvent.addCallbackParameter(key, value);
                }
            }

            // Partner parameters
            String[] partnerParameters = AlltrackBridgeUtil.jsonArrayToArray((JSONArray)partnerParametersField);
            if (partnerParameters != null) {
                for (int i = 0; i < partnerParameters.length; i += 2) {
                    String key = partnerParameters[i];
                    String value = partnerParameters[i+1];
                    alltrackEvent.addPartnerParameter(key, value);
                }
            }

            // Revenue deduplication
            String orderId = AlltrackBridgeUtil.fieldToString(orderIdField);
            if (orderId != null) {
                alltrackEvent.setOrderId(orderId);
            }

            // Callback id
            String callbackId = AlltrackBridgeUtil.fieldToString(callbackIdField);
            if (callbackId != null) {
                alltrackEvent.setCallbackId(callbackId);
            }

            // Track event
            Alltrack.trackEvent(alltrackEvent);
        } catch (Exception e) {
            AlltrackFactory.getLogger().error("AlltrackBridgeInstance trackEvent: %s", e.getMessage());
        }
    }

    @JavascriptInterface
    public void trackAdRevenue(final String source, final String payload) {
        try {
            // payload JSON string is URL encoded
            String decodedPayload = URLDecoder.decode(payload, "UTF-8");
            JSONObject jsonPayload = new JSONObject(decodedPayload);
            Alltrack.trackAdRevenue(source, jsonPayload);
        } catch (JSONException je) {
            AlltrackFactory.getLogger().debug("Ad revenue payload does not seem to be a valid JSON string");
        } catch (UnsupportedEncodingException ue) {
            AlltrackFactory.getLogger().debug("Unable to URL decode given JSON string");
        }
    }

    @JavascriptInterface
    public void onResume() {
        if (!isInitialized()) {
            return;
        }
        Alltrack.onResume();
    }

    @JavascriptInterface
    public void onPause() {
        if (!isInitialized()) {
            return;
        }
        Alltrack.onPause();
    }

    @JavascriptInterface
    public void setEnabled(String isEnabledString) {
        if (!isInitialized()) {
            return;
        }
        Boolean isEnabled = AlltrackBridgeUtil.fieldToBoolean(isEnabledString);
        if (isEnabled != null) {
            Alltrack.setEnabled(isEnabled);
        }
    }

    @JavascriptInterface
    public void isEnabled(String callback) {
        if (!isInitialized()) {
            return;
        }
        boolean isEnabled = Alltrack.isEnabled();
        AlltrackBridgeUtil.execSingleValueCallback(webView, callback, String.valueOf(isEnabled));
    }

    @JavascriptInterface
    public boolean isEnabled() {
        if (!isInitialized()) {
            return false;
        }
        return Alltrack.isEnabled();
    }

    @JavascriptInterface
    public void appWillOpenUrl(String deeplinkString) {
        if (!isInitialized()) {
            return;
        }
        Uri deeplink = null;
        if (deeplinkString != null) {
            deeplink = Uri.parse(deeplinkString);
        }
        Alltrack.appWillOpenUrl(deeplink, application.getApplicationContext());
    }

    @JavascriptInterface
    public void setReferrer(String referrer) {
        if (!isInitialized()) {
            return;
        }
        Alltrack.setReferrer(referrer, application.getApplicationContext());
    }

    @JavascriptInterface
    public void setOfflineMode(String isOfflineString) {
        if (!isInitialized()) {
            return;
        }
        Boolean isOffline = AlltrackBridgeUtil.fieldToBoolean(isOfflineString);
        if (isOffline != null) {
            Alltrack.setOfflineMode(isOffline);
        }
    }

    @JavascriptInterface
    public void sendFirstPackages() {
        if (!isInitialized()) {
            return;
        }
        Alltrack.sendFirstPackages();
    }

    @JavascriptInterface
    public void addSessionCallbackParameter(String key, String value) {
        if (!isInitialized()) {
            return;
        }
        Alltrack.addSessionCallbackParameter(key, value);
    }

    @JavascriptInterface
    public void addSessionPartnerParameter(String key, String value) {
        if (!isInitialized()) {
            return;
        }
        Alltrack.addSessionPartnerParameter(key, value);
    }

    @JavascriptInterface
    public void removeSessionCallbackParameter(String key) {
        if (!isInitialized()) {
            return;
        }
        Alltrack.removeSessionCallbackParameter(key);
    }

    @JavascriptInterface
    public void removeSessionPartnerParameter(String key) {
        if (!isInitialized()) {
            return;
        }
        Alltrack.removeSessionPartnerParameter(key);
    }

    @JavascriptInterface
    public void resetSessionCallbackParameters() {
        if (!isInitialized()) {
            return;
        }
        Alltrack.resetSessionCallbackParameters();
    }

    @JavascriptInterface
    public void resetSessionPartnerParameters() {
        if (!isInitialized()) {
            return;
        }
        Alltrack.resetSessionPartnerParameters();
    }

    @JavascriptInterface
    public void setPushToken(String pushToken) {
        if (!isInitialized()) {
            return;
        }

        Alltrack.setPushToken(pushToken, application.getApplicationContext());
    }

    @JavascriptInterface
    public void gdprForgetMe() {
        if (!isInitialized()) {
            return;
        }
        Alltrack.gdprForgetMe(application.getApplicationContext());
    }

    @JavascriptInterface
    public void disableThirdPartySharing() {
        if (!isInitialized()) {
            return;
        }
        Alltrack.disableThirdPartySharing(application.getApplicationContext());
    }

    @JavascriptInterface
    public void trackThirdPartySharing(String alltrackThirdPartySharingString) {
        if (!isInitialized()) {
            return;
        }

        try {
            JSONObject jsonAlltrackThirdPartySharing = new JSONObject(alltrackThirdPartySharingString);

            Object isEnabledField =
                    jsonAlltrackThirdPartySharing.get("isEnabled");
            Object granularOptionsField = jsonAlltrackThirdPartySharing.get("granularOptions");
            Object partnerSharingSettingsField = jsonAlltrackThirdPartySharing.get("partnerSharingSettings");

            Boolean isEnabled = AlltrackBridgeUtil.fieldToBoolean(isEnabledField);

            AlltrackThirdPartySharing alltrackThirdPartySharing =
                    new AlltrackThirdPartySharing(isEnabled);

            // Granular options
            String[] granularOptions =
                    AlltrackBridgeUtil.jsonArrayToArray((JSONArray)granularOptionsField);
            if (granularOptions != null) {
                for (int i = 0; i < granularOptions.length; i += 3) {
                    String partnerName = granularOptions[i];
                    String key = granularOptions[i + 1];
                    String value = granularOptions[i + 2];
                    alltrackThirdPartySharing.addGranularOption(partnerName, key, value);
                }
            }

            // Partner sharing settings
            String[] partnerSharingSettings =
                    AlltrackBridgeUtil.jsonArrayToArray((JSONArray)partnerSharingSettingsField);
            if (partnerSharingSettings != null) {
                for (int i = 0; i < partnerSharingSettings.length; i += 3) {
                    String partnerName = partnerSharingSettings[i];
                    String key = partnerSharingSettings[i + 1];
                    Boolean value = AlltrackBridgeUtil.fieldToBoolean(partnerSharingSettings[i + 2]);
                    if (value != null) {
                        alltrackThirdPartySharing.addPartnerSharingSetting(partnerName, key, value);
                    } else {
                        AlltrackFactory.getLogger().error("Cannot add partner sharing setting with non boolean value");
                    }
                }
            }

            // Track ThirdPartySharing
            Alltrack.trackThirdPartySharing(alltrackThirdPartySharing);
        } catch (Exception e) {
            AlltrackFactory.getLogger().error(
                    "AlltrackBridgeInstance trackThirdPartySharing: %s", e.getMessage());
        }
    }

    @JavascriptInterface
    public void trackMeasurementConsent(String consentMeasurementString) {
        if (!isInitialized()) {
            return;
        }
        Boolean consentMeasurement = AlltrackBridgeUtil.fieldToBoolean(consentMeasurementString);
        if (consentMeasurement != null) {
            Alltrack.trackMeasurementConsent(consentMeasurement);
        }
    }

    @JavascriptInterface
    public void getGoogleAdId(final String callback) {
        if (!isInitialized()) {
            return;
        }
        Alltrack.getGoogleAdId(application.getApplicationContext(), new OnDeviceIdsRead() {
            @Override
            public void onGoogleAdIdRead(String googleAdId) {
                AlltrackBridgeUtil.execSingleValueCallback(webView, callback, googleAdId);
            }
        });
    }

    @JavascriptInterface
    public String getAmazonAdId() {
        if (!isInitialized()) {
            return null;
        }
        return Alltrack.getAmazonAdId(application.getApplicationContext());
    }

    @JavascriptInterface
    public String getAdid() {
        if (!isInitialized()) {
            return null;
        }
        return Alltrack.getAdid();
    }

    @JavascriptInterface
    public void getAttribution(final String callback) {
        if (!isInitialized()) {
            return;
        }
        AlltrackAttribution attribution = Alltrack.getAttribution();
        AlltrackBridgeUtil.execAttributionCallbackCommand(webView, callback, attribution);
    }

    @JavascriptInterface
    public String getSdkVersion() {
        return Alltrack.getSdkVersion();
    }

    @JavascriptInterface
    public void fbPixelEvent(String pixelId, String event_name, String jsonString) {
        this.facebookSDKJSInterface.sendEvent(pixelId, event_name, jsonString);
    }

    @JavascriptInterface
    public void teardown() {
        isInitialized = false;
        shouldDeferredDeeplinkBeLaunched = true;
    }

    public void setWebView(WebView webView) {
        this.webView = webView;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webView.addJavascriptInterface(this, JAVASCRIPT_INTERFACE_NAME);
        }
    }

    public void setApplicationContext(Application application) {
        this.application = application;
    }

    public void unregister() {
        if (!isInitialized()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            this.webView.removeJavascriptInterface(JAVASCRIPT_INTERFACE_NAME);
        }

        unregisterFacebookSDKJSInterface();

        application = null;
        webView = null;
        isInitialized = false;
    }

    public void unregisterFacebookSDKJSInterface() {
        if (!isInitialized()) {
            return;
        }

        if (this.facebookSDKJSInterface == null) {
            return;
        }

        String fbApplicationId = FacebookSDKJSInterface.getApplicationId(application.getApplicationContext());
        if (fbApplicationId == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            this.webView.removeJavascriptInterface(FB_JAVASCRIPT_INTERFACE_NAME_PREFIX + fbApplicationId);
        }

        this.facebookSDKJSInterface = null;
    }
}
