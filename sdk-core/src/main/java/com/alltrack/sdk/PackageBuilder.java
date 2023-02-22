

package com.alltrack.sdk;

import java.util.Map;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONObject;

import android.text.TextUtils;

public class PackageBuilder {
    private static ILogger logger = AlltrackFactory.getLogger();
    private long createdAt;
    private DeviceInfo deviceInfo;
    private AlltrackConfig alltrackConfig;
    private ActivityStateCopy activityStateCopy;
    private SessionParameters sessionParameters;

    long clickTimeInSeconds = -1;
    long clickTimeInMilliseconds = -1;
    long installBeginTimeInSeconds = -1;
    long clickTimeServerInSeconds = -1;
    long installBeginTimeServerInSeconds = -1;
    String reftag;
    String deeplink;
    String referrer;
    String installVersion;
    String rawReferrer;
    String referrerApi;
    String preinstallPayload;
    String preinstallLocation;
    Boolean googlePlayInstant;
    AlltrackAttribution attribution;
    Map<String, String> extraParameters;

    private class ActivityStateCopy {
        int eventCount = -1;
        int sessionCount = -1;
        int subsessionCount = -1;
        long timeSpent = -1;
        long lastInterval = -1;
        long sessionLength = -1;
        String uuid = null;
        String pushToken = null;

        ActivityStateCopy(ActivityState activityState) {
            if (activityState == null) {
                return;
            }
            this.eventCount = activityState.eventCount;
            this.sessionCount = activityState.sessionCount;
            this.subsessionCount = activityState.subsessionCount;
            this.timeSpent = activityState.timeSpent;
            this.lastInterval = activityState.lastInterval;
            this.sessionLength = activityState.sessionLength;
            this.uuid = activityState.uuid;
            this.pushToken = activityState.pushToken;
        }
    }

    PackageBuilder(AlltrackConfig alltrackConfig,
                   DeviceInfo deviceInfo,
                   ActivityState activityState,
                   SessionParameters sessionParameters,
                   long createdAt) {
        this.createdAt = createdAt;
        this.deviceInfo = deviceInfo;
        this.alltrackConfig = alltrackConfig;
        this.activityStateCopy = new ActivityStateCopy(activityState);
        this.sessionParameters = sessionParameters;
    }

    ActivityPackage buildSessionPackage(boolean isInDelay) {
        Map<String, String> parameters = getSessionParameters(isInDelay);
        ActivityPackage sessionPackage = getDefaultActivityPackage(ActivityKind.SESSION);
        sessionPackage.setPath("/session");
        sessionPackage.setSuffix("");

        AlltrackSigner.sign(parameters, ActivityKind.SESSION.toString(),
                sessionPackage.getClientSdk(), alltrackConfig.context, alltrackConfig.logger);

        sessionPackage.setParameters(parameters);
        return sessionPackage;
    }

    ActivityPackage buildEventPackage(AlltrackEvent event, boolean isInDelay) {
        Map<String, String> parameters = getEventParameters(event, isInDelay);
        ActivityPackage eventPackage = getDefaultActivityPackage(ActivityKind.EVENT);
        eventPackage.setPath("/event");
        eventPackage.setSuffix(getEventSuffix(event));

        AlltrackSigner.sign(parameters, ActivityKind.EVENT.toString(),
                eventPackage.getClientSdk(), alltrackConfig.context, alltrackConfig.logger);

        eventPackage.setParameters(parameters);

        if (isInDelay) {
            eventPackage.setCallbackParameters(event.callbackParameters);
            eventPackage.setPartnerParameters(event.partnerParameters);
        }

        return eventPackage;
    }

    ActivityPackage buildInfoPackage(String source) {
        Map<String, String> parameters = getInfoParameters(source);
        ActivityPackage infoPackage = getDefaultActivityPackage(ActivityKind.INFO);
        infoPackage.setPath("/sdk_info");
        infoPackage.setSuffix("");

        AlltrackSigner.sign(parameters, ActivityKind.INFO.toString(),
                infoPackage.getClientSdk(), alltrackConfig.context, alltrackConfig.logger);

        infoPackage.setParameters(parameters);
        return infoPackage;
    }

    ActivityPackage buildClickPackage(String source) {
        Map<String, String> parameters = getClickParameters(source);
        ActivityPackage clickPackage = getDefaultActivityPackage(ActivityKind.CLICK);
        clickPackage.setPath("/sdk_click");
        clickPackage.setSuffix("");
        clickPackage.setClickTimeInMilliseconds(clickTimeInMilliseconds);
        clickPackage.setClickTimeInSeconds(clickTimeInSeconds);
        clickPackage.setInstallBeginTimeInSeconds(installBeginTimeInSeconds);
        clickPackage.setClickTimeServerInSeconds(clickTimeServerInSeconds);
        clickPackage.setInstallBeginTimeServerInSeconds(installBeginTimeServerInSeconds);
        clickPackage.setInstallVersion(installVersion);
        clickPackage.setGooglePlayInstant(googlePlayInstant);

        AlltrackSigner.sign(parameters, ActivityKind.CLICK.toString(),
                clickPackage.getClientSdk(), alltrackConfig.context, alltrackConfig.logger);

        clickPackage.setParameters(parameters);
        return clickPackage;
    }

    ActivityPackage buildAttributionPackage(String initiatedByDescription) {
        Map<String, String> parameters = getAttributionParameters(initiatedByDescription);
        ActivityPackage attributionPackage = getDefaultActivityPackage(ActivityKind.ATTRIBUTION);
        attributionPackage.setPath("attribution"); // does not contain '/' because of Uri.Builder.appendPath
        attributionPackage.setSuffix("");

        AlltrackSigner.sign(parameters, ActivityKind.ATTRIBUTION.toString(),
                attributionPackage.getClientSdk(), alltrackConfig.context, alltrackConfig.logger);

        attributionPackage.setParameters(parameters);
        return attributionPackage;
    }

    ActivityPackage buildGdprPackage() {
        Map<String, String> parameters = getGdprParameters();
        ActivityPackage gdprPackage = getDefaultActivityPackage(ActivityKind.GDPR);
        gdprPackage.setPath("/gdpr_forget_device");
        gdprPackage.setSuffix("");

        AlltrackSigner.sign(parameters, ActivityKind.GDPR.toString(),
                gdprPackage.getClientSdk(), alltrackConfig.context, alltrackConfig.logger);

        gdprPackage.setParameters(parameters);
        return gdprPackage;
    }

    ActivityPackage buildDisableThirdPartySharingPackage() {
        Map<String, String> parameters = getDisableThirdPartySharingParameters();
        ActivityPackage activityPackage = getDefaultActivityPackage(ActivityKind.DISABLE_THIRD_PARTY_SHARING);
        activityPackage.setPath("/disable_third_party_sharing");
        activityPackage.setSuffix("");

        AlltrackSigner.sign(parameters, ActivityKind.DISABLE_THIRD_PARTY_SHARING.toString(),
                activityPackage.getClientSdk(), alltrackConfig.context, alltrackConfig.logger);

        activityPackage.setParameters(parameters);
        return activityPackage;
    }

    ActivityPackage buildThirdPartySharingPackage(
            final AlltrackThirdPartySharing alltrackThirdPartySharing)
    {
        Map<String, String> parameters = getThirdPartySharingParameters(alltrackThirdPartySharing);
        ActivityPackage activityPackage = getDefaultActivityPackage(ActivityKind.THIRD_PARTY_SHARING);
        activityPackage.setPath("/third_party_sharing");
        activityPackage.setSuffix("");

        AlltrackSigner.sign(parameters, ActivityKind.THIRD_PARTY_SHARING.toString(),
                activityPackage.getClientSdk(), alltrackConfig.context, alltrackConfig.logger);

        activityPackage.setParameters(parameters);
        return activityPackage;
    }

    ActivityPackage buildMeasurementConsentPackage(final boolean consentMeasurement) {
        Map<String, String> parameters = getMeasurementConsentParameters(consentMeasurement);
        ActivityPackage activityPackage =
                getDefaultActivityPackage(ActivityKind.MEASUREMENT_CONSENT);
        activityPackage.setPath("/measurement_consent");
        activityPackage.setSuffix("");

        AlltrackSigner.sign(parameters, ActivityKind.MEASUREMENT_CONSENT.toString(),
                activityPackage.getClientSdk(), alltrackConfig.context, alltrackConfig.logger);

        activityPackage.setParameters(parameters);
        return activityPackage;
    }

    ActivityPackage buildAdRevenuePackage(String source, JSONObject adRevenueJson) {
        Map<String, String> parameters = getAdRevenueParameters(source, adRevenueJson);
        ActivityPackage adRevenuePackage = getDefaultActivityPackage(ActivityKind.AD_REVENUE);
        adRevenuePackage.setPath("/ad_revenue");
        adRevenuePackage.setSuffix("");

        AlltrackSigner.sign(parameters, ActivityKind.AD_REVENUE.toString(),
                adRevenuePackage.getClientSdk(), alltrackConfig.context, alltrackConfig.logger);

        adRevenuePackage.setParameters(parameters);
        return adRevenuePackage;
    }

    ActivityPackage buildAdRevenuePackage(AlltrackAdRevenue alltrackAdRevenue, boolean isInDelay) {
        Map<String, String> parameters = getAdRevenueParameters(alltrackAdRevenue, isInDelay);
        ActivityPackage adRevenuePackage = getDefaultActivityPackage(ActivityKind.AD_REVENUE);
        adRevenuePackage.setPath("/ad_revenue");
        adRevenuePackage.setSuffix("");

        AlltrackSigner.sign(parameters, ActivityKind.AD_REVENUE.toString(),
                          adRevenuePackage.getClientSdk(), alltrackConfig.context, alltrackConfig.logger);

        adRevenuePackage.setParameters(parameters);

        if (isInDelay) {
            adRevenuePackage.setCallbackParameters(alltrackAdRevenue.callbackParameters);
            adRevenuePackage.setPartnerParameters(alltrackAdRevenue.partnerParameters);
        }

        return adRevenuePackage;
    }

    ActivityPackage buildSubscriptionPackage(AlltrackPlayStoreSubscription subscription, boolean isInDelay) {
        Map<String, String> parameters = getSubscriptionParameters(subscription, isInDelay);
        ActivityPackage subscriptionPackage = getDefaultActivityPackage(ActivityKind.SUBSCRIPTION);
        subscriptionPackage.setPath("/v2/purchase");
        subscriptionPackage.setSuffix("");

        AlltrackSigner.sign(parameters, ActivityKind.SUBSCRIPTION.toString(),
                subscriptionPackage.getClientSdk(), alltrackConfig.context, alltrackConfig.logger);

        subscriptionPackage.setParameters(parameters);
        return subscriptionPackage;
    }

    private Map<String, String> getSessionParameters(boolean isInDelay) {
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> imeiParameters = Util.getImeiParameters(alltrackConfig, logger);

        // Check if plugin is used and if yes, add read parameters.
        if (imeiParameters != null) {
            parameters.putAll(imeiParameters);
        }

        // Check if oaid plugin is used and if yes, add the parameter
        Map<String, String> oaidParameters = Util.getOaidParameters(alltrackConfig, logger);
        if (oaidParameters != null) {
            parameters.putAll(oaidParameters);
        }

        // Callback and partner parameters.
        if (!isInDelay) {
            PackageBuilder.addMapJson(parameters, "callback_params", this.sessionParameters.callbackParameters);
            PackageBuilder.addMapJson(parameters, "partner_params", this.sessionParameters.partnerParameters);
        }

        // Device identifiers.
        deviceInfo.reloadPlayIds(alltrackConfig);
        PackageBuilder.addString(parameters, "android_uuid", activityStateCopy.uuid);
        PackageBuilder.addString(parameters, "gps_adid", deviceInfo.playAdId);
        PackageBuilder.addLong(parameters, "gps_adid_attempt", deviceInfo.playAdIdAttempt);
        PackageBuilder.addString(parameters, "gps_adid_src", deviceInfo.playAdIdSource);
        PackageBuilder.addBoolean(parameters, "tracking_enabled", deviceInfo.isTrackingEnabled);
        PackageBuilder.addString(parameters, "fire_adid", Util.getFireAdvertisingId(alltrackConfig));
        PackageBuilder.addBoolean(parameters, "fire_tracking_enabled", Util.getFireTrackingEnabled(alltrackConfig));

        if (!containsPlayIds(parameters) && !containsFireIds(parameters)) {
            logger.warn("Google Advertising ID or Fire Advertising ID not detected, " +
                    "fallback to non Google Play and Fire identifiers will take place");
            deviceInfo.reloadNonPlayIds(alltrackConfig);
            PackageBuilder.addString(parameters, "android_id", deviceInfo.androidId);
        }

        // Rest of the parameters.
        PackageBuilder.addString(parameters, "api_level", deviceInfo.apiLevel);
        PackageBuilder.addString(parameters, "app_secret", alltrackConfig.appSecret);
        PackageBuilder.addString(parameters, "app_token", alltrackConfig.appToken);
        PackageBuilder.addString(parameters, "app_version", deviceInfo.appVersion);
        PackageBuilder.addBoolean(parameters, "attribution_deeplink", true);
        PackageBuilder.addLong(parameters, "connectivity_type", Util.getConnectivityType(alltrackConfig.context));
        PackageBuilder.addString(parameters, "country", deviceInfo.country);
        PackageBuilder.addString(parameters, "cpu_type", deviceInfo.abi);
        PackageBuilder.addDateInMilliseconds(parameters, "created_at", createdAt);
        PackageBuilder.addString(parameters, "default_tracker", alltrackConfig.defaultTracker);
        PackageBuilder.addBoolean(parameters, "device_known", alltrackConfig.deviceKnown);
        PackageBuilder.addBoolean(parameters, "needs_cost", alltrackConfig.needsCost);
        PackageBuilder.addString(parameters, "device_manufacturer", deviceInfo.deviceManufacturer);
        PackageBuilder.addString(parameters, "device_name", deviceInfo.deviceName);
        PackageBuilder.addString(parameters, "device_type", deviceInfo.deviceType);
        PackageBuilder.addLong(parameters, "ui_mode", deviceInfo.uiMode);
        PackageBuilder.addString(parameters, "display_height", deviceInfo.displayHeight);
        PackageBuilder.addString(parameters, "display_width", deviceInfo.displayWidth);
        PackageBuilder.addString(parameters, "environment", alltrackConfig.environment);
        PackageBuilder.addBoolean(parameters, "event_buffering_enabled", alltrackConfig.eventBufferingEnabled);
        PackageBuilder.addString(parameters, "external_device_id", alltrackConfig.externalDeviceId);
        PackageBuilder.addString(parameters, "fb_id", deviceInfo.fbAttributionId);
        PackageBuilder.addString(parameters, "hardware_name", deviceInfo.hardwareName);
        PackageBuilder.addString(parameters, "installed_at", deviceInfo.appInstallTime);
        PackageBuilder.addString(parameters, "language", deviceInfo.language);
        PackageBuilder.addDuration(parameters, "last_interval", activityStateCopy.lastInterval);
        PackageBuilder.addString(parameters, "mcc", Util.getMcc(alltrackConfig.context));
        PackageBuilder.addString(parameters, "mnc", Util.getMnc(alltrackConfig.context));
        PackageBuilder.addBoolean(parameters, "needs_response_details", true);
        PackageBuilder.addString(parameters, "os_build", deviceInfo.buildName);
        PackageBuilder.addString(parameters, "os_name", deviceInfo.osName);
        PackageBuilder.addString(parameters, "os_version", deviceInfo.osVersion);
        PackageBuilder.addString(parameters, "package_name", deviceInfo.packageName);
        PackageBuilder.addString(parameters, "push_token", activityStateCopy.pushToken);
        PackageBuilder.addString(parameters, "screen_density", deviceInfo.screenDensity);
        PackageBuilder.addString(parameters, "screen_format", deviceInfo.screenFormat);
        PackageBuilder.addString(parameters, "screen_size", deviceInfo.screenSize);
        PackageBuilder.addString(parameters, "secret_id", alltrackConfig.secretId);
        PackageBuilder.addLong(parameters, "session_count", activityStateCopy.sessionCount);
        PackageBuilder.addDuration(parameters, "session_length", activityStateCopy.sessionLength);
        PackageBuilder.addLong(parameters, "subsession_count", activityStateCopy.subsessionCount);
        PackageBuilder.addDuration(parameters, "time_spent", activityStateCopy.timeSpent);
        PackageBuilder.addString(parameters, "updated_at", deviceInfo.appUpdateTime);

        injectFeatureFlagsWithParameters(parameters);

        checkDeviceIds(parameters);
        return parameters;
    }

    public Map<String, String> getEventParameters(AlltrackEvent event, boolean isInDelay) {
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> imeiParameters = Util.getImeiParameters(alltrackConfig, logger);

        // Check if plugin is used and if yes, add read parameters.
        if (imeiParameters != null) {
            parameters.putAll(imeiParameters);
        }

        // Check if oaid plugin is used and if yes, add the parameter
        Map<String, String> oaidParameters = Util.getOaidParameters(alltrackConfig, logger);
        if (oaidParameters != null) {
            parameters.putAll(oaidParameters);
        }

        // Callback and partner parameters.
        if (!isInDelay) {
            PackageBuilder.addMapJson(parameters, "callback_params", Util.mergeParameters(this.sessionParameters.callbackParameters, event.callbackParameters, "Callback"));
            PackageBuilder.addMapJson(parameters, "partner_params", Util.mergeParameters(this.sessionParameters.partnerParameters, event.partnerParameters, "Partner"));
        }

        // Device identifiers.
        deviceInfo.reloadPlayIds(alltrackConfig);
        PackageBuilder.addString(parameters, "android_uuid", activityStateCopy.uuid);
        PackageBuilder.addString(parameters, "gps_adid", deviceInfo.playAdId);
        PackageBuilder.addLong(parameters, "gps_adid_attempt", deviceInfo.playAdIdAttempt);
        PackageBuilder.addString(parameters, "gps_adid_src", deviceInfo.playAdIdSource);
        PackageBuilder.addBoolean(parameters, "tracking_enabled", deviceInfo.isTrackingEnabled);
        PackageBuilder.addString(parameters, "fire_adid", Util.getFireAdvertisingId(alltrackConfig));
        PackageBuilder.addBoolean(parameters, "fire_tracking_enabled", Util.getFireTrackingEnabled(alltrackConfig));

        if (!containsPlayIds(parameters) && !containsFireIds(parameters)) {
            logger.warn("Google Advertising ID or Fire Advertising ID not detected, " +
                    "fallback to non Google Play and Fire identifiers will take place");
            deviceInfo.reloadNonPlayIds(alltrackConfig);
            PackageBuilder.addString(parameters, "android_id", deviceInfo.androidId);
        }

        // Rest of the parameters.
        PackageBuilder.addString(parameters, "api_level", deviceInfo.apiLevel);
        PackageBuilder.addString(parameters, "app_secret", alltrackConfig.appSecret);
        PackageBuilder.addString(parameters, "app_token", alltrackConfig.appToken);
        PackageBuilder.addString(parameters, "app_version", deviceInfo.appVersion);
        PackageBuilder.addBoolean(parameters, "attribution_deeplink", true);
        PackageBuilder.addLong(parameters, "connectivity_type", Util.getConnectivityType(alltrackConfig.context));
        PackageBuilder.addString(parameters, "country", deviceInfo.country);
        PackageBuilder.addString(parameters, "cpu_type", deviceInfo.abi);
        PackageBuilder.addDateInMilliseconds(parameters, "created_at", createdAt);
        PackageBuilder.addString(parameters, "currency", event.currency);
        PackageBuilder.addBoolean(parameters, "device_known", alltrackConfig.deviceKnown);
        PackageBuilder.addBoolean(parameters, "needs_cost", alltrackConfig.needsCost);
        PackageBuilder.addString(parameters, "device_manufacturer", deviceInfo.deviceManufacturer);
        PackageBuilder.addString(parameters, "device_name", deviceInfo.deviceName);
        PackageBuilder.addString(parameters, "device_type", deviceInfo.deviceType);
        PackageBuilder.addLong(parameters, "ui_mode", deviceInfo.uiMode);
        PackageBuilder.addString(parameters, "display_height", deviceInfo.displayHeight);
        PackageBuilder.addString(parameters, "display_width", deviceInfo.displayWidth);
        PackageBuilder.addString(parameters, "environment", alltrackConfig.environment);
        PackageBuilder.addString(parameters, "event_callback_id", event.callbackId);
        PackageBuilder.addLong(parameters, "event_count", activityStateCopy.eventCount);
        PackageBuilder.addBoolean(parameters, "event_buffering_enabled", alltrackConfig.eventBufferingEnabled);
        PackageBuilder.addString(parameters, "event_token", event.eventToken);
        PackageBuilder.addString(parameters, "external_device_id", alltrackConfig.externalDeviceId);
        PackageBuilder.addString(parameters, "fb_id", deviceInfo.fbAttributionId);
        PackageBuilder.addString(parameters, "hardware_name", deviceInfo.hardwareName);
        PackageBuilder.addString(parameters, "language", deviceInfo.language);
        PackageBuilder.addString(parameters, "mcc", Util.getMcc(alltrackConfig.context));
        PackageBuilder.addString(parameters, "mnc", Util.getMnc(alltrackConfig.context));
        PackageBuilder.addBoolean(parameters, "needs_response_details", true);
        PackageBuilder.addString(parameters, "os_build", deviceInfo.buildName);
        PackageBuilder.addString(parameters, "os_name", deviceInfo.osName);
        PackageBuilder.addString(parameters, "os_version", deviceInfo.osVersion);
        PackageBuilder.addString(parameters, "package_name", deviceInfo.packageName);
        PackageBuilder.addString(parameters, "push_token", activityStateCopy.pushToken);
        PackageBuilder.addDouble(parameters, "revenue", event.revenue);
        PackageBuilder.addString(parameters, "deduplication_id", event.orderId);
        PackageBuilder.addString(parameters, "screen_density", deviceInfo.screenDensity);
        PackageBuilder.addString(parameters, "screen_format", deviceInfo.screenFormat);
        PackageBuilder.addString(parameters, "screen_size", deviceInfo.screenSize);
        PackageBuilder.addString(parameters, "secret_id", alltrackConfig.secretId);
        PackageBuilder.addLong(parameters, "session_count", activityStateCopy.sessionCount);
        PackageBuilder.addDuration(parameters, "session_length", activityStateCopy.sessionLength);
        PackageBuilder.addLong(parameters, "subsession_count", activityStateCopy.subsessionCount);
        PackageBuilder.addDuration(parameters, "time_spent", activityStateCopy.timeSpent);

        injectFeatureFlagsWithParameters(parameters);

        checkDeviceIds(parameters);
        return parameters;
    }

    private Map<String, String> getInfoParameters(String source) {
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> imeiParameters = Util.getImeiParameters(alltrackConfig, logger);

        // Check if plugin is used and if yes, add read parameters.
        if (imeiParameters != null) {
            parameters.putAll(imeiParameters);
        }

        // Check if oaid plugin is used and if yes, add the parameter
        Map<String, String> oaidParameters = Util.getOaidParameters(alltrackConfig, logger);
        if (oaidParameters != null) {
            parameters.putAll(oaidParameters);
        }

        // Device identifiers.
        deviceInfo.reloadPlayIds(alltrackConfig);
        PackageBuilder.addString(parameters, "android_uuid", activityStateCopy.uuid);
        PackageBuilder.addString(parameters, "gps_adid", deviceInfo.playAdId);
        PackageBuilder.addLong(parameters, "gps_adid_attempt", deviceInfo.playAdIdAttempt);
        PackageBuilder.addString(parameters, "gps_adid_src", deviceInfo.playAdIdSource);
        PackageBuilder.addBoolean(parameters, "tracking_enabled", deviceInfo.isTrackingEnabled);
        PackageBuilder.addString(parameters, "fire_adid", Util.getFireAdvertisingId(alltrackConfig));
        PackageBuilder.addBoolean(parameters, "fire_tracking_enabled", Util.getFireTrackingEnabled(alltrackConfig));

        if (!containsPlayIds(parameters) && !containsFireIds(parameters)) {
            logger.warn("Google Advertising ID or Fire Advertising ID not detected, " +
                    "fallback to non Google Play and Fire identifiers will take place");
            deviceInfo.reloadNonPlayIds(alltrackConfig);
            PackageBuilder.addString(parameters, "android_id", deviceInfo.androidId);
        }

        // Rest of the parameters.
        PackageBuilder.addString(parameters, "app_secret", alltrackConfig.appSecret);
        PackageBuilder.addString(parameters, "app_token", alltrackConfig.appToken);
        PackageBuilder.addBoolean(parameters, "attribution_deeplink", true);
        PackageBuilder.addDateInMilliseconds(parameters, "created_at", createdAt);
        PackageBuilder.addBoolean(parameters, "device_known", alltrackConfig.deviceKnown);
        PackageBuilder.addBoolean(parameters, "needs_cost", alltrackConfig.needsCost);
        PackageBuilder.addString(parameters, "environment", alltrackConfig.environment);
        PackageBuilder.addBoolean(parameters, "event_buffering_enabled", alltrackConfig.eventBufferingEnabled);
        PackageBuilder.addString(parameters, "external_device_id", alltrackConfig.externalDeviceId);
        PackageBuilder.addBoolean(parameters, "needs_response_details", true);
        PackageBuilder.addString(parameters, "push_token", activityStateCopy.pushToken);
        PackageBuilder.addString(parameters, "secret_id", alltrackConfig.secretId);
        PackageBuilder.addString(parameters, "source", source);

        injectFeatureFlagsWithParameters(parameters);

        checkDeviceIds(parameters);
        return parameters;
    }

    private Map<String, String> getClickParameters(String source) {
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> imeiParameters = Util.getImeiParameters(alltrackConfig, logger);

        // Check if plugin is used and if yes, add read parameters.
        if (imeiParameters != null) {
            parameters.putAll(imeiParameters);
        }

        // Check if oaid plugin is used and if yes, add the parameter
        Map<String, String> oaidParameters = Util.getOaidParameters(alltrackConfig, logger);
        if (oaidParameters != null) {
            parameters.putAll(oaidParameters);
        }

        // Device identifiers.
        deviceInfo.reloadPlayIds(alltrackConfig);
        PackageBuilder.addString(parameters, "android_uuid", activityStateCopy.uuid);
        PackageBuilder.addString(parameters, "gps_adid", deviceInfo.playAdId);
        PackageBuilder.addLong(parameters, "gps_adid_attempt", deviceInfo.playAdIdAttempt);
        PackageBuilder.addString(parameters, "gps_adid_src", deviceInfo.playAdIdSource);
        PackageBuilder.addBoolean(parameters, "tracking_enabled", deviceInfo.isTrackingEnabled);
        PackageBuilder.addString(parameters, "fire_adid", Util.getFireAdvertisingId(alltrackConfig));
        PackageBuilder.addBoolean(parameters, "fire_tracking_enabled", Util.getFireTrackingEnabled(alltrackConfig));

        if (!containsPlayIds(parameters) && !containsFireIds(parameters)) {
            logger.warn("Google Advertising ID or Fire Advertising ID not detected, " +
                    "fallback to non Google Play and Fire identifiers will take place");
            deviceInfo.reloadNonPlayIds(alltrackConfig);
            PackageBuilder.addString(parameters, "android_id", deviceInfo.androidId);
        }

        // Attribution parameters.
        if (attribution != null) {
            PackageBuilder.addString(parameters, "tracker", attribution.trackerName);
            PackageBuilder.addString(parameters, "campaign", attribution.campaign);
            PackageBuilder.addString(parameters, "adgroup", attribution.adgroup);
            PackageBuilder.addString(parameters, "creative", attribution.creative);
        }

        // Rest of the parameters.
        PackageBuilder.addString(parameters, "api_level", deviceInfo.apiLevel);
        PackageBuilder.addString(parameters, "app_secret", alltrackConfig.appSecret);
        PackageBuilder.addString(parameters, "app_token", alltrackConfig.appToken);
        PackageBuilder.addString(parameters, "app_version", deviceInfo.appVersion);
        PackageBuilder.addBoolean(parameters, "attribution_deeplink", true);
        PackageBuilder.addMapJson(parameters, "callback_params", this.sessionParameters.callbackParameters);
        PackageBuilder.addDateInMilliseconds(parameters, "click_time", clickTimeInMilliseconds);
        PackageBuilder.addDateInSeconds(parameters, "click_time", clickTimeInSeconds);
        PackageBuilder.addDateInSeconds(parameters, "click_time_server", clickTimeServerInSeconds);
        PackageBuilder.addLong(parameters, "connectivity_type", Util.getConnectivityType(alltrackConfig.context));
        PackageBuilder.addString(parameters, "country", deviceInfo.country);
        PackageBuilder.addString(parameters, "cpu_type", deviceInfo.abi);
        PackageBuilder.addDateInMilliseconds(parameters, "created_at", createdAt);
        PackageBuilder.addString(parameters, "deeplink", deeplink);
        PackageBuilder.addBoolean(parameters, "device_known", alltrackConfig.deviceKnown);
        PackageBuilder.addBoolean(parameters, "needs_cost", alltrackConfig.needsCost);
        PackageBuilder.addString(parameters, "device_manufacturer", deviceInfo.deviceManufacturer);
        PackageBuilder.addString(parameters, "device_name", deviceInfo.deviceName);
        PackageBuilder.addString(parameters, "device_type", deviceInfo.deviceType);
        PackageBuilder.addLong(parameters, "ui_mode", deviceInfo.uiMode);
        PackageBuilder.addString(parameters, "display_height", deviceInfo.displayHeight);
        PackageBuilder.addString(parameters, "display_width", deviceInfo.displayWidth);
        PackageBuilder.addString(parameters, "environment", alltrackConfig.environment);
        PackageBuilder.addBoolean(parameters, "event_buffering_enabled", alltrackConfig.eventBufferingEnabled);
        PackageBuilder.addString(parameters, "external_device_id", alltrackConfig.externalDeviceId);
        PackageBuilder.addString(parameters, "fb_id", deviceInfo.fbAttributionId);
        PackageBuilder.addBoolean(parameters, "google_play_instant", googlePlayInstant);
        PackageBuilder.addString(parameters, "hardware_name", deviceInfo.hardwareName);
        PackageBuilder.addDateInSeconds(parameters, "install_begin_time", installBeginTimeInSeconds);
        PackageBuilder.addDateInSeconds(parameters, "install_begin_time_server", installBeginTimeServerInSeconds);
        PackageBuilder.addString(parameters, "install_version", installVersion);
        PackageBuilder.addString(parameters, "installed_at", deviceInfo.appInstallTime);
        PackageBuilder.addString(parameters, "language", deviceInfo.language);
        PackageBuilder.addDuration(parameters, "last_interval", activityStateCopy.lastInterval);
        PackageBuilder.addString(parameters, "mcc", Util.getMcc(alltrackConfig.context));
        PackageBuilder.addString(parameters, "mnc", Util.getMnc(alltrackConfig.context));
        PackageBuilder.addBoolean(parameters, "needs_response_details", true);
        PackageBuilder.addString(parameters, "os_build", deviceInfo.buildName);
        PackageBuilder.addString(parameters, "os_name", deviceInfo.osName);
        PackageBuilder.addString(parameters, "os_version", deviceInfo.osVersion);
        PackageBuilder.addString(parameters, "package_name", deviceInfo.packageName);
        PackageBuilder.addMapJson(parameters, "params", extraParameters);
        PackageBuilder.addMapJson(parameters, "partner_params", this.sessionParameters.partnerParameters);
        PackageBuilder.addString(parameters, "push_token", activityStateCopy.pushToken);
        PackageBuilder.addString(parameters, "raw_referrer", rawReferrer);
        PackageBuilder.addString(parameters, "referrer", referrer);
        PackageBuilder.addString(parameters, "referrer_api", referrerApi);
        PackageBuilder.addString(parameters, "reftag", reftag);
        PackageBuilder.addString(parameters, "screen_density", deviceInfo.screenDensity);
        PackageBuilder.addString(parameters, "screen_format", deviceInfo.screenFormat);
        PackageBuilder.addString(parameters, "screen_size", deviceInfo.screenSize);
        PackageBuilder.addString(parameters, "secret_id", alltrackConfig.secretId);
        PackageBuilder.addLong(parameters, "session_count", activityStateCopy.sessionCount);
        PackageBuilder.addDuration(parameters, "session_length", activityStateCopy.sessionLength);
        PackageBuilder.addString(parameters, "source", source);
        PackageBuilder.addLong(parameters, "subsession_count", activityStateCopy.subsessionCount);
        PackageBuilder.addDuration(parameters, "time_spent", activityStateCopy.timeSpent);
        PackageBuilder.addString(parameters, "updated_at", deviceInfo.appUpdateTime);
        PackageBuilder.addString(parameters, "payload", preinstallPayload);
        PackageBuilder.addString(parameters, "found_location", preinstallLocation);

        injectFeatureFlagsWithParameters(parameters);

        checkDeviceIds(parameters);
        return parameters;
    }

    private Map<String, String> getAttributionParameters(String initiatedBy) {
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> imeiParameters = Util.getImeiParameters(alltrackConfig, logger);

        // Check if plugin is used and if yes, add read parameters.
        if (imeiParameters != null) {
            parameters.putAll(imeiParameters);
        }

        // Check if oaid plugin is used and if yes, add the parameter
        Map<String, String> oaidParameters = Util.getOaidParameters(alltrackConfig, logger);
        if (oaidParameters != null) {
            parameters.putAll(oaidParameters);
        }

        // Device identifiers.
        deviceInfo.reloadPlayIds(alltrackConfig);
        PackageBuilder.addString(parameters, "android_uuid", activityStateCopy.uuid);
        PackageBuilder.addString(parameters, "gps_adid", deviceInfo.playAdId);
        PackageBuilder.addLong(parameters, "gps_adid_attempt", deviceInfo.playAdIdAttempt);
        PackageBuilder.addString(parameters, "gps_adid_src", deviceInfo.playAdIdSource);
        PackageBuilder.addBoolean(parameters, "tracking_enabled", deviceInfo.isTrackingEnabled);
        PackageBuilder.addString(parameters, "fire_adid", Util.getFireAdvertisingId(alltrackConfig));
        PackageBuilder.addBoolean(parameters, "fire_tracking_enabled", Util.getFireTrackingEnabled(alltrackConfig));

        if (!containsPlayIds(parameters) && !containsFireIds(parameters)) {
            logger.warn("Google Advertising ID or Fire Advertising ID not detected, " +
                    "fallback to non Google Play and Fire identifiers will take place");
            deviceInfo.reloadNonPlayIds(alltrackConfig);
            PackageBuilder.addString(parameters, "android_id", deviceInfo.androidId);
        }

        // Rest of the parameters.
        PackageBuilder.addString(parameters, "api_level", deviceInfo.apiLevel);
        PackageBuilder.addString(parameters, "app_secret", alltrackConfig.appSecret);
        PackageBuilder.addString(parameters, "app_token", alltrackConfig.appToken);
        PackageBuilder.addString(parameters, "app_version", deviceInfo.appVersion);
        PackageBuilder.addBoolean(parameters, "attribution_deeplink", true);
        PackageBuilder.addDateInMilliseconds(parameters, "created_at", createdAt);
        PackageBuilder.addBoolean(parameters, "device_known", alltrackConfig.deviceKnown);
        PackageBuilder.addBoolean(parameters, "needs_cost", alltrackConfig.needsCost);
        PackageBuilder.addString(parameters, "device_name", deviceInfo.deviceName);
        PackageBuilder.addString(parameters, "device_type", deviceInfo.deviceType);
        PackageBuilder.addLong(parameters, "ui_mode", deviceInfo.uiMode);
        PackageBuilder.addString(parameters, "environment", alltrackConfig.environment);
        PackageBuilder.addBoolean(parameters, "event_buffering_enabled", alltrackConfig.eventBufferingEnabled);
        PackageBuilder.addString(parameters, "external_device_id", alltrackConfig.externalDeviceId);
        PackageBuilder.addString(parameters, "initiated_by", initiatedBy);
        PackageBuilder.addBoolean(parameters, "needs_response_details", true);
        PackageBuilder.addString(parameters, "os_name", deviceInfo.osName);
        PackageBuilder.addString(parameters, "os_version", deviceInfo.osVersion);
        PackageBuilder.addString(parameters, "package_name", deviceInfo.packageName);
        PackageBuilder.addString(parameters, "push_token", activityStateCopy.pushToken);
        PackageBuilder.addString(parameters, "secret_id", alltrackConfig.secretId);

        injectFeatureFlagsWithParameters(parameters);

        checkDeviceIds(parameters);
        return parameters;
    }

    private Map<String, String> getGdprParameters() {
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> imeiParameters = Util.getImeiParameters(alltrackConfig, logger);

        // Check if plugin is used and if yes, add read parameters.
        if (imeiParameters != null) {
            parameters.putAll(imeiParameters);
        }

        // Check if oaid plugin is used and if yes, add the parameter
        Map<String, String> oaidParameters = Util.getOaidParameters(alltrackConfig, logger);
        if (oaidParameters != null) {
            parameters.putAll(oaidParameters);
        }

        // Device identifiers.
        deviceInfo.reloadPlayIds(alltrackConfig);
        PackageBuilder.addString(parameters, "android_uuid", activityStateCopy.uuid);
        PackageBuilder.addString(parameters, "gps_adid", deviceInfo.playAdId);
        PackageBuilder.addLong(parameters, "gps_adid_attempt", deviceInfo.playAdIdAttempt);
        PackageBuilder.addString(parameters, "gps_adid_src", deviceInfo.playAdIdSource);
        PackageBuilder.addBoolean(parameters, "tracking_enabled", deviceInfo.isTrackingEnabled);
        PackageBuilder.addString(parameters, "fire_adid", Util.getFireAdvertisingId(alltrackConfig));
        PackageBuilder.addBoolean(parameters, "fire_tracking_enabled", Util.getFireTrackingEnabled(alltrackConfig));

        if (!containsPlayIds(parameters) && !containsFireIds(parameters)) {
            logger.warn("Google Advertising ID or Fire Advertising ID not detected, " +
                    "fallback to non Google Play and Fire identifiers will take place");
            deviceInfo.reloadNonPlayIds(alltrackConfig);
            PackageBuilder.addString(parameters, "android_id", deviceInfo.androidId);
        }

        // Rest of the parameters.
        PackageBuilder.addString(parameters, "api_level", deviceInfo.apiLevel);
        PackageBuilder.addString(parameters, "app_secret", alltrackConfig.appSecret);
        PackageBuilder.addString(parameters, "app_token", alltrackConfig.appToken);
        PackageBuilder.addString(parameters, "app_version", deviceInfo.appVersion);
        PackageBuilder.addBoolean(parameters, "attribution_deeplink", true);
        PackageBuilder.addDateInMilliseconds(parameters, "created_at", createdAt);
        PackageBuilder.addBoolean(parameters, "device_known", alltrackConfig.deviceKnown);
        PackageBuilder.addBoolean(parameters, "needs_cost", alltrackConfig.needsCost);
        PackageBuilder.addString(parameters, "device_name", deviceInfo.deviceName);
        PackageBuilder.addString(parameters, "device_type", deviceInfo.deviceType);
        PackageBuilder.addLong(parameters, "ui_mode", deviceInfo.uiMode);
        PackageBuilder.addString(parameters, "environment", alltrackConfig.environment);
        PackageBuilder.addBoolean(parameters, "event_buffering_enabled", alltrackConfig.eventBufferingEnabled);
        PackageBuilder.addString(parameters, "external_device_id", alltrackConfig.externalDeviceId);
        PackageBuilder.addBoolean(parameters, "needs_response_details", true);
        PackageBuilder.addString(parameters, "os_name", deviceInfo.osName);
        PackageBuilder.addString(parameters, "os_version", deviceInfo.osVersion);
        PackageBuilder.addString(parameters, "package_name", deviceInfo.packageName);
        PackageBuilder.addString(parameters, "push_token", activityStateCopy.pushToken);
        PackageBuilder.addString(parameters, "secret_id", alltrackConfig.secretId);

        injectFeatureFlagsWithParameters(parameters);

        checkDeviceIds(parameters);
        return parameters;
    }

    private Map<String, String> getDisableThirdPartySharingParameters() {
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> imeiParameters = Util.getImeiParameters(alltrackConfig, logger);

        // Check if plugin is used and if yes, add read parameters.
        if (imeiParameters != null) {
            parameters.putAll(imeiParameters);
        }

        // Check if oaid plugin is used and if yes, add the parameter
        Map<String, String> oaidParameters = Util.getOaidParameters(alltrackConfig, logger);
        if (oaidParameters != null) {
            parameters.putAll(oaidParameters);
        }

        // Device identifiers.
        deviceInfo.reloadPlayIds(alltrackConfig);
        PackageBuilder.addString(parameters, "android_uuid", activityStateCopy.uuid);
        PackageBuilder.addString(parameters, "gps_adid", deviceInfo.playAdId);
        PackageBuilder.addLong(parameters, "gps_adid_attempt", deviceInfo.playAdIdAttempt);
        PackageBuilder.addString(parameters, "gps_adid_src", deviceInfo.playAdIdSource);
        PackageBuilder.addBoolean(parameters, "tracking_enabled", deviceInfo.isTrackingEnabled);
        PackageBuilder.addString(parameters, "fire_adid", Util.getFireAdvertisingId(alltrackConfig));
        PackageBuilder.addBoolean(parameters, "fire_tracking_enabled", Util.getFireTrackingEnabled(alltrackConfig));

        if (!containsPlayIds(parameters) && !containsFireIds(parameters)) {
            logger.warn("Google Advertising ID or Fire Advertising ID not detected, " +
                    "fallback to non Google Play and Fire identifiers will take place");
            deviceInfo.reloadNonPlayIds(alltrackConfig);
            PackageBuilder.addString(parameters, "android_id", deviceInfo.androidId);
        }

        // Rest of the parameters.
        PackageBuilder.addString(parameters, "api_level", deviceInfo.apiLevel);
        PackageBuilder.addString(parameters, "app_secret", alltrackConfig.appSecret);
        PackageBuilder.addString(parameters, "app_token", alltrackConfig.appToken);
        PackageBuilder.addString(parameters, "app_version", deviceInfo.appVersion);
        PackageBuilder.addBoolean(parameters, "attribution_deeplink", true);
        PackageBuilder.addDateInMilliseconds(parameters, "created_at", createdAt);
        PackageBuilder.addBoolean(parameters, "device_known", alltrackConfig.deviceKnown);
        PackageBuilder.addBoolean(parameters, "needs_cost", alltrackConfig.needsCost);
        PackageBuilder.addString(parameters, "device_name", deviceInfo.deviceName);
        PackageBuilder.addString(parameters, "device_type", deviceInfo.deviceType);
        PackageBuilder.addLong(parameters, "ui_mode", deviceInfo.uiMode);
        PackageBuilder.addString(parameters, "environment", alltrackConfig.environment);
        PackageBuilder.addBoolean(parameters, "event_buffering_enabled", alltrackConfig.eventBufferingEnabled);
        PackageBuilder.addString(parameters, "external_device_id", alltrackConfig.externalDeviceId);
        PackageBuilder.addBoolean(parameters, "needs_response_details", true);
        PackageBuilder.addString(parameters, "os_name", deviceInfo.osName);
        PackageBuilder.addString(parameters, "os_version", deviceInfo.osVersion);
        PackageBuilder.addString(parameters, "package_name", deviceInfo.packageName);
        PackageBuilder.addString(parameters, "push_token", activityStateCopy.pushToken);
        PackageBuilder.addString(parameters, "secret_id", alltrackConfig.secretId);

        injectFeatureFlagsWithParameters(parameters);

        checkDeviceIds(parameters);
        return parameters;
    }

    private Map<String, String> getThirdPartySharingParameters
            (final AlltrackThirdPartySharing alltrackThirdPartySharing)
    {
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> imeiParameters = Util.getImeiParameters(alltrackConfig, logger);

        // Check if plugin is used and if yes, add read parameters.
        if (imeiParameters != null) {
            parameters.putAll(imeiParameters);
        }

        // Check if oaid plugin is used and if yes, add the parameter
        Map<String, String> oaidParameters = Util.getOaidParameters(alltrackConfig, logger);
        if (oaidParameters != null) {
            parameters.putAll(oaidParameters);
        }

        // Third Party Sharing
        if (alltrackThirdPartySharing.isEnabled != null) {
            PackageBuilder.addString(parameters, "sharing",
                    alltrackThirdPartySharing.isEnabled.booleanValue() ?
                            "enable" : "disable");
        }
        PackageBuilder.addMapJson(parameters, "granular_third_party_sharing_options",
                alltrackThirdPartySharing.granularOptions);

        PackageBuilder.addMapJson(parameters, "partner_sharing_settings",
                alltrackThirdPartySharing.partnerSharingSettings);

        // Device identifiers.
        deviceInfo.reloadPlayIds(alltrackConfig);
        PackageBuilder.addString(parameters, "android_uuid", activityStateCopy.uuid);
        PackageBuilder.addString(parameters, "gps_adid", deviceInfo.playAdId);
        PackageBuilder.addLong(parameters, "gps_adid_attempt", deviceInfo.playAdIdAttempt);
        PackageBuilder.addString(parameters, "gps_adid_src", deviceInfo.playAdIdSource);
        PackageBuilder.addBoolean(parameters, "tracking_enabled", deviceInfo.isTrackingEnabled);
        PackageBuilder.addString(parameters, "fire_adid", Util.getFireAdvertisingId(alltrackConfig));
        PackageBuilder.addBoolean(parameters, "fire_tracking_enabled", Util.getFireTrackingEnabled(alltrackConfig));

        if (!containsPlayIds(parameters) && !containsFireIds(parameters)) {
            logger.warn("Google Advertising ID or Fire Advertising ID not detected, " +
                    "fallback to non Google Play and Fire identifiers will take place");
            deviceInfo.reloadNonPlayIds(alltrackConfig);
            PackageBuilder.addString(parameters, "android_id", deviceInfo.androidId);
        }

        // Rest of the parameters.
        PackageBuilder.addString(parameters, "api_level", deviceInfo.apiLevel);
        PackageBuilder.addString(parameters, "app_secret", alltrackConfig.appSecret);
        PackageBuilder.addString(parameters, "app_token", alltrackConfig.appToken);
        PackageBuilder.addString(parameters, "app_version", deviceInfo.appVersion);
        PackageBuilder.addBoolean(parameters, "attribution_deeplink", true);
        PackageBuilder.addDateInMilliseconds(parameters, "created_at", createdAt);
        PackageBuilder.addBoolean(parameters, "device_known", alltrackConfig.deviceKnown);
        PackageBuilder.addString(parameters, "device_name", deviceInfo.deviceName);
        PackageBuilder.addString(parameters, "device_type", deviceInfo.deviceType);
        PackageBuilder.addLong(parameters, "ui_mode", deviceInfo.uiMode);
        PackageBuilder.addString(parameters, "environment", alltrackConfig.environment);
        PackageBuilder.addBoolean(parameters, "event_buffering_enabled", alltrackConfig.eventBufferingEnabled);
        PackageBuilder.addString(parameters, "external_device_id", alltrackConfig.externalDeviceId);
        PackageBuilder.addBoolean(parameters, "needs_response_details", true);
        PackageBuilder.addString(parameters, "os_name", deviceInfo.osName);
        PackageBuilder.addString(parameters, "os_version", deviceInfo.osVersion);
        PackageBuilder.addString(parameters, "package_name", deviceInfo.packageName);
        PackageBuilder.addString(parameters, "push_token", activityStateCopy.pushToken);
        PackageBuilder.addString(parameters, "secret_id", alltrackConfig.secretId);

        injectFeatureFlagsWithParameters(parameters);

        checkDeviceIds(parameters);
        return parameters;
    }

    private Map<String, String> getMeasurementConsentParameters(
            final boolean consentMeasurement)
    {
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> imeiParameters = Util.getImeiParameters(alltrackConfig, logger);

        // Check if plugin is used and if yes, add read parameters.
        if (imeiParameters != null) {
            parameters.putAll(imeiParameters);
        }

        // Check if oaid plugin is used and if yes, add the parameter
        Map<String, String> oaidParameters = Util.getOaidParameters(alltrackConfig, logger);
        if (oaidParameters != null) {
            parameters.putAll(oaidParameters);
        }

        // Measurement Consent
        PackageBuilder.addString(parameters, "measurement",
                consentMeasurement ? "enable" : "disable");

        // Device identifiers.
        deviceInfo.reloadPlayIds(alltrackConfig);
        PackageBuilder.addString(parameters, "android_uuid", activityStateCopy.uuid);
        PackageBuilder.addString(parameters, "gps_adid", deviceInfo.playAdId);
        PackageBuilder.addLong(parameters, "gps_adid_attempt", deviceInfo.playAdIdAttempt);
        PackageBuilder.addString(parameters, "gps_adid_src", deviceInfo.playAdIdSource);
        PackageBuilder.addBoolean(parameters, "tracking_enabled", deviceInfo.isTrackingEnabled);
        PackageBuilder.addString(parameters, "fire_adid", Util.getFireAdvertisingId(alltrackConfig));
        PackageBuilder.addBoolean(parameters, "fire_tracking_enabled", Util.getFireTrackingEnabled(alltrackConfig));

        if (!containsPlayIds(parameters) && !containsFireIds(parameters)) {
            logger.warn("Google Advertising ID or Fire Advertising ID not detected, " +
                    "fallback to non Google Play and Fire identifiers will take place");
            deviceInfo.reloadNonPlayIds(alltrackConfig);
            PackageBuilder.addString(parameters, "android_id", deviceInfo.androidId);
        }

        // Rest of the parameters.
        PackageBuilder.addString(parameters, "api_level", deviceInfo.apiLevel);
        PackageBuilder.addString(parameters, "app_secret", alltrackConfig.appSecret);
        PackageBuilder.addString(parameters, "app_token", alltrackConfig.appToken);
        PackageBuilder.addString(parameters, "app_version", deviceInfo.appVersion);
        PackageBuilder.addBoolean(parameters, "attribution_deeplink", true);
        PackageBuilder.addDateInMilliseconds(parameters, "created_at", createdAt);
        PackageBuilder.addBoolean(parameters, "device_known", alltrackConfig.deviceKnown);
        PackageBuilder.addString(parameters, "device_name", deviceInfo.deviceName);
        PackageBuilder.addString(parameters, "device_type", deviceInfo.deviceType);
        PackageBuilder.addLong(parameters, "ui_mode", deviceInfo.uiMode);
        PackageBuilder.addString(parameters, "environment", alltrackConfig.environment);
        PackageBuilder.addBoolean(parameters, "event_buffering_enabled", alltrackConfig.eventBufferingEnabled);
        PackageBuilder.addString(parameters, "external_device_id", alltrackConfig.externalDeviceId);
        PackageBuilder.addBoolean(parameters, "needs_response_details", true);
        PackageBuilder.addString(parameters, "os_name", deviceInfo.osName);
        PackageBuilder.addString(parameters, "os_version", deviceInfo.osVersion);
        PackageBuilder.addString(parameters, "package_name", deviceInfo.packageName);
        PackageBuilder.addString(parameters, "push_token", activityStateCopy.pushToken);
        PackageBuilder.addString(parameters, "secret_id", alltrackConfig.secretId);

        injectFeatureFlagsWithParameters(parameters);

        checkDeviceIds(parameters);
        return parameters;
    }

    private Map<String, String> getAdRevenueParameters(String source, JSONObject adRevenueJson) {
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> imeiParameters = Util.getImeiParameters(alltrackConfig, logger);

        // Check if plugin is used and if yes, add read parameters.
        if (imeiParameters != null) {
            parameters.putAll(imeiParameters);
        }

        // Check if oaid plugin is used and if yes, add the parameter
        Map<String, String> oaidParameters = Util.getOaidParameters(alltrackConfig, logger);
        if (oaidParameters != null) {
            parameters.putAll(oaidParameters);
        }

        // Device identifiers.
        deviceInfo.reloadPlayIds(alltrackConfig);
        PackageBuilder.addString(parameters, "android_uuid", activityStateCopy.uuid);
        PackageBuilder.addString(parameters, "gps_adid", deviceInfo.playAdId);
        PackageBuilder.addLong(parameters, "gps_adid_attempt", deviceInfo.playAdIdAttempt);
        PackageBuilder.addString(parameters, "gps_adid_src", deviceInfo.playAdIdSource);
        PackageBuilder.addBoolean(parameters, "tracking_enabled", deviceInfo.isTrackingEnabled);
        PackageBuilder.addString(parameters, "fire_adid", Util.getFireAdvertisingId(alltrackConfig));
        PackageBuilder.addBoolean(parameters, "fire_tracking_enabled", Util.getFireTrackingEnabled(alltrackConfig));

        if (!containsPlayIds(parameters) && !containsFireIds(parameters)) {
            logger.warn("Google Advertising ID or Fire Advertising ID not detected, " +
                    "fallback to non Google Play and Fire identifiers will take place");
            deviceInfo.reloadNonPlayIds(alltrackConfig);
            PackageBuilder.addString(parameters, "android_id", deviceInfo.androidId);
        }

        // Rest of the parameters.
        PackageBuilder.addString(parameters, "api_level", deviceInfo.apiLevel);
        PackageBuilder.addString(parameters, "app_secret", alltrackConfig.appSecret);
        PackageBuilder.addString(parameters, "app_token", alltrackConfig.appToken);
        PackageBuilder.addString(parameters, "app_version", deviceInfo.appVersion);
        PackageBuilder.addBoolean(parameters, "attribution_deeplink", true);
        PackageBuilder.addLong(parameters, "connectivity_type", Util.getConnectivityType(alltrackConfig.context));
        PackageBuilder.addString(parameters, "country", deviceInfo.country);
        PackageBuilder.addString(parameters, "cpu_type", deviceInfo.abi);
        PackageBuilder.addDateInMilliseconds(parameters, "created_at", createdAt);
        PackageBuilder.addString(parameters, "default_tracker", alltrackConfig.defaultTracker);
        PackageBuilder.addBoolean(parameters, "device_known", alltrackConfig.deviceKnown);
        PackageBuilder.addBoolean(parameters, "needs_cost", alltrackConfig.needsCost);
        PackageBuilder.addString(parameters, "device_manufacturer", deviceInfo.deviceManufacturer);
        PackageBuilder.addString(parameters, "device_name", deviceInfo.deviceName);
        PackageBuilder.addString(parameters, "device_type", deviceInfo.deviceType);
        PackageBuilder.addLong(parameters, "ui_mode", deviceInfo.uiMode);
        PackageBuilder.addString(parameters, "display_height", deviceInfo.displayHeight);
        PackageBuilder.addString(parameters, "display_width", deviceInfo.displayWidth);
        PackageBuilder.addString(parameters, "environment", alltrackConfig.environment);
        PackageBuilder.addBoolean(parameters, "event_buffering_enabled", alltrackConfig.eventBufferingEnabled);
        PackageBuilder.addString(parameters, "external_device_id", alltrackConfig.externalDeviceId);
        PackageBuilder.addString(parameters, "fb_id", deviceInfo.fbAttributionId);
        PackageBuilder.addString(parameters, "hardware_name", deviceInfo.hardwareName);
        PackageBuilder.addString(parameters, "installed_at", deviceInfo.appInstallTime);
        PackageBuilder.addString(parameters, "language", deviceInfo.language);
        PackageBuilder.addDuration(parameters, "last_interval", activityStateCopy.lastInterval);
        PackageBuilder.addString(parameters, "mcc", Util.getMcc(alltrackConfig.context));
        PackageBuilder.addString(parameters, "mnc", Util.getMnc(alltrackConfig.context));
        PackageBuilder.addBoolean(parameters, "needs_response_details", true);
        PackageBuilder.addString(parameters, "os_build", deviceInfo.buildName);
        PackageBuilder.addString(parameters, "os_name", deviceInfo.osName);
        PackageBuilder.addString(parameters, "os_version", deviceInfo.osVersion);
        PackageBuilder.addString(parameters, "package_name", deviceInfo.packageName);
        PackageBuilder.addString(parameters, "push_token", activityStateCopy.pushToken);
        PackageBuilder.addString(parameters, "screen_density", deviceInfo.screenDensity);
        PackageBuilder.addString(parameters, "screen_format", deviceInfo.screenFormat);
        PackageBuilder.addString(parameters, "screen_size", deviceInfo.screenSize);
        PackageBuilder.addString(parameters, "secret_id", alltrackConfig.secretId);
        PackageBuilder.addString(parameters, "source", source);
        PackageBuilder.addJsonObject(parameters, "payload", adRevenueJson);
        PackageBuilder.addLong(parameters, "session_count", activityStateCopy.sessionCount);
        PackageBuilder.addDuration(parameters, "session_length", activityStateCopy.sessionLength);
        PackageBuilder.addLong(parameters, "subsession_count", activityStateCopy.subsessionCount);
        PackageBuilder.addDuration(parameters, "time_spent", activityStateCopy.timeSpent);
        PackageBuilder.addString(parameters, "updated_at", deviceInfo.appUpdateTime);

        injectFeatureFlagsWithParameters(parameters);

        checkDeviceIds(parameters);
        return parameters;
    }

    private Map<String, String> getAdRevenueParameters(AlltrackAdRevenue alltrackAdRevenue, boolean isInDelay) {
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> imeiParameters = Util.getImeiParameters(alltrackConfig, logger);

        // Check if plugin is used and if yes, add read parameters.
        if (imeiParameters != null) {
            parameters.putAll(imeiParameters);
        }

        // Check if oaid plugin is used and if yes, add the parameter
        Map<String, String> oaidParameters = Util.getOaidParameters(alltrackConfig, logger);
        if (oaidParameters != null) {
            parameters.putAll(oaidParameters);
        }

        // Callback and partner parameters.
        if (!isInDelay) {
            PackageBuilder.addMapJson(parameters, "callback_params", Util.mergeParameters(this.sessionParameters.callbackParameters, alltrackAdRevenue.callbackParameters, "Callback"));
            PackageBuilder.addMapJson(parameters, "partner_params", Util.mergeParameters(this.sessionParameters.partnerParameters, alltrackAdRevenue.partnerParameters, "Partner"));
        }

        // Device identifiers.
        deviceInfo.reloadPlayIds(alltrackConfig);
        PackageBuilder.addString(parameters, "android_uuid", activityStateCopy.uuid);
        PackageBuilder.addString(parameters, "gps_adid", deviceInfo.playAdId);
        PackageBuilder.addLong(parameters, "gps_adid_attempt", deviceInfo.playAdIdAttempt);
        PackageBuilder.addString(parameters, "gps_adid_src", deviceInfo.playAdIdSource);
        PackageBuilder.addBoolean(parameters, "tracking_enabled", deviceInfo.isTrackingEnabled);
        PackageBuilder.addString(parameters, "fire_adid", Util.getFireAdvertisingId(alltrackConfig));
        PackageBuilder.addBoolean(parameters, "fire_tracking_enabled", Util.getFireTrackingEnabled(alltrackConfig));

        if (!containsPlayIds(parameters) && !containsFireIds(parameters)) {
            logger.warn("Google Advertising ID or Fire Advertising ID not detected, " +
                        "fallback to non Google Play and Fire identifiers will take place");
            deviceInfo.reloadNonPlayIds(alltrackConfig);
            PackageBuilder.addString(parameters, "android_id", deviceInfo.androidId);
        }

        // Rest of the parameters.
        PackageBuilder.addString(parameters, "api_level", deviceInfo.apiLevel);
        PackageBuilder.addString(parameters, "app_secret", alltrackConfig.appSecret);
        PackageBuilder.addString(parameters, "app_token", alltrackConfig.appToken);
        PackageBuilder.addString(parameters, "app_version", deviceInfo.appVersion);
        PackageBuilder.addBoolean(parameters, "attribution_deeplink", true);
        PackageBuilder.addLong(parameters, "connectivity_type", Util.getConnectivityType(alltrackConfig.context));
        PackageBuilder.addString(parameters, "country", deviceInfo.country);
        PackageBuilder.addString(parameters, "cpu_type", deviceInfo.abi);
        PackageBuilder.addDateInMilliseconds(parameters, "created_at", createdAt);
        PackageBuilder.addString(parameters, "default_tracker", alltrackConfig.defaultTracker);
        PackageBuilder.addBoolean(parameters, "device_known", alltrackConfig.deviceKnown);
        PackageBuilder.addBoolean(parameters, "needs_cost", alltrackConfig.needsCost);
        PackageBuilder.addString(parameters, "device_manufacturer", deviceInfo.deviceManufacturer);
        PackageBuilder.addString(parameters, "device_name", deviceInfo.deviceName);
        PackageBuilder.addString(parameters, "device_type", deviceInfo.deviceType);
        PackageBuilder.addLong(parameters, "ui_mode", deviceInfo.uiMode);
        PackageBuilder.addString(parameters, "display_height", deviceInfo.displayHeight);
        PackageBuilder.addString(parameters, "display_width", deviceInfo.displayWidth);
        PackageBuilder.addString(parameters, "environment", alltrackConfig.environment);
        PackageBuilder.addBoolean(parameters, "event_buffering_enabled", alltrackConfig.eventBufferingEnabled);
        PackageBuilder.addString(parameters, "external_device_id", alltrackConfig.externalDeviceId);
        PackageBuilder.addString(parameters, "fb_id", deviceInfo.fbAttributionId);
        PackageBuilder.addString(parameters, "hardware_name", deviceInfo.hardwareName);
        PackageBuilder.addString(parameters, "installed_at", deviceInfo.appInstallTime);
        PackageBuilder.addString(parameters, "language", deviceInfo.language);
        PackageBuilder.addDuration(parameters, "last_interval", activityStateCopy.lastInterval);
        PackageBuilder.addString(parameters, "mcc", Util.getMcc(alltrackConfig.context));
        PackageBuilder.addString(parameters, "mnc", Util.getMnc(alltrackConfig.context));
        PackageBuilder.addBoolean(parameters, "needs_response_details", true);
        PackageBuilder.addString(parameters, "os_build", deviceInfo.buildName);
        PackageBuilder.addString(parameters, "os_name", deviceInfo.osName);
        PackageBuilder.addString(parameters, "os_version", deviceInfo.osVersion);
        PackageBuilder.addString(parameters, "package_name", deviceInfo.packageName);
        PackageBuilder.addString(parameters, "push_token", activityStateCopy.pushToken);
        PackageBuilder.addString(parameters, "screen_density", deviceInfo.screenDensity);
        PackageBuilder.addString(parameters, "screen_format", deviceInfo.screenFormat);
        PackageBuilder.addString(parameters, "screen_size", deviceInfo.screenSize);
        PackageBuilder.addString(parameters, "secret_id", alltrackConfig.secretId);
        PackageBuilder.addString(parameters, "source", alltrackAdRevenue.source);
        PackageBuilder.addDoubleWithoutRounding(parameters, "revenue", alltrackAdRevenue.revenue);
        PackageBuilder.addString(parameters, "currency", alltrackAdRevenue.currency);
        PackageBuilder.addInteger(parameters, "ad_impressions_count", alltrackAdRevenue.adImpressionsCount);
        PackageBuilder.addString(parameters, "ad_revenue_network", alltrackAdRevenue.adRevenueNetwork);
        PackageBuilder.addString(parameters, "ad_revenue_unit", alltrackAdRevenue.adRevenueUnit);
        PackageBuilder.addString(parameters, "ad_revenue_placement", alltrackAdRevenue.adRevenuePlacement);
        PackageBuilder.addLong(parameters, "session_count", activityStateCopy.sessionCount);
        PackageBuilder.addDuration(parameters, "session_length", activityStateCopy.sessionLength);
        PackageBuilder.addLong(parameters, "subsession_count", activityStateCopy.subsessionCount);
        PackageBuilder.addDuration(parameters, "time_spent", activityStateCopy.timeSpent);
        PackageBuilder.addString(parameters, "updated_at", deviceInfo.appUpdateTime);

        injectFeatureFlagsWithParameters(parameters);

        checkDeviceIds(parameters);
        return parameters;
    }

    private Map<String, String> getSubscriptionParameters(AlltrackPlayStoreSubscription subscription, boolean isInDelay) {
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> imeiParameters = Util.getImeiParameters(alltrackConfig, logger);

        // Check if plugin is used and if yes, add read parameters.
        if (imeiParameters != null) {
            parameters.putAll(imeiParameters);
        }

        // Check if oaid plugin is used and if yes, add the parameter
        Map<String, String> oaidParameters = Util.getOaidParameters(alltrackConfig, logger);
        if (oaidParameters != null) {
            parameters.putAll(oaidParameters);
        }

        // Device identifiers.
        deviceInfo.reloadPlayIds(alltrackConfig);
        PackageBuilder.addString(parameters, "android_uuid", activityStateCopy.uuid);
        PackageBuilder.addString(parameters, "gps_adid", deviceInfo.playAdId);
        PackageBuilder.addLong(parameters, "gps_adid_attempt", deviceInfo.playAdIdAttempt);
        PackageBuilder.addString(parameters, "gps_adid_src", deviceInfo.playAdIdSource);
        PackageBuilder.addBoolean(parameters, "tracking_enabled", deviceInfo.isTrackingEnabled);
        PackageBuilder.addString(parameters, "fire_adid", Util.getFireAdvertisingId(alltrackConfig));
        PackageBuilder.addBoolean(parameters, "fire_tracking_enabled", Util.getFireTrackingEnabled(alltrackConfig));

        if (!containsPlayIds(parameters) && !containsFireIds(parameters)) {
            logger.warn("Google Advertising ID or Fire Advertising ID not detected, " +
                    "fallback to non Google Play and Fire identifiers will take place");
            deviceInfo.reloadNonPlayIds(alltrackConfig);
            PackageBuilder.addString(parameters, "android_id", deviceInfo.androidId);
        }

        // Callback and partner parameters.
        if (!isInDelay) {
            PackageBuilder.addMapJson(parameters, "callback_params", Util.mergeParameters(this.sessionParameters.callbackParameters, subscription.getCallbackParameters(), "Callback"));
            PackageBuilder.addMapJson(parameters, "partner_params", Util.mergeParameters(this.sessionParameters.partnerParameters, subscription.getPartnerParameters(), "Partner"));
        }

        // Rest of the parameters.
        PackageBuilder.addString(parameters, "api_level", deviceInfo.apiLevel);
        PackageBuilder.addString(parameters, "app_secret", alltrackConfig.appSecret);
        PackageBuilder.addString(parameters, "app_token", alltrackConfig.appToken);
        PackageBuilder.addString(parameters, "app_version", deviceInfo.appVersion);
        PackageBuilder.addBoolean(parameters, "attribution_deeplink", true);
        PackageBuilder.addLong(parameters, "connectivity_type", Util.getConnectivityType(alltrackConfig.context));
        PackageBuilder.addString(parameters, "country", deviceInfo.country);
        PackageBuilder.addString(parameters, "cpu_type", deviceInfo.abi);
        PackageBuilder.addDateInMilliseconds(parameters, "created_at", createdAt);
        PackageBuilder.addString(parameters, "default_tracker", alltrackConfig.defaultTracker);
        PackageBuilder.addBoolean(parameters, "device_known", alltrackConfig.deviceKnown);
        PackageBuilder.addBoolean(parameters, "needs_cost", alltrackConfig.needsCost);
        PackageBuilder.addString(parameters, "device_manufacturer", deviceInfo.deviceManufacturer);
        PackageBuilder.addString(parameters, "device_name", deviceInfo.deviceName);
        PackageBuilder.addString(parameters, "device_type", deviceInfo.deviceType);
        PackageBuilder.addLong(parameters, "ui_mode", deviceInfo.uiMode);
        PackageBuilder.addString(parameters, "display_height", deviceInfo.displayHeight);
        PackageBuilder.addString(parameters, "display_width", deviceInfo.displayWidth);
        PackageBuilder.addString(parameters, "environment", alltrackConfig.environment);
        PackageBuilder.addBoolean(parameters, "event_buffering_enabled", alltrackConfig.eventBufferingEnabled);
        PackageBuilder.addString(parameters, "external_device_id", alltrackConfig.externalDeviceId);
        PackageBuilder.addString(parameters, "fb_id", deviceInfo.fbAttributionId);
        PackageBuilder.addString(parameters, "hardware_name", deviceInfo.hardwareName);
        PackageBuilder.addString(parameters, "installed_at", deviceInfo.appInstallTime);
        PackageBuilder.addString(parameters, "language", deviceInfo.language);
        PackageBuilder.addDuration(parameters, "last_interval", activityStateCopy.lastInterval);
        PackageBuilder.addString(parameters, "mcc", Util.getMcc(alltrackConfig.context));
        PackageBuilder.addString(parameters, "mnc", Util.getMnc(alltrackConfig.context));
        PackageBuilder.addBoolean(parameters, "needs_response_details", true);
        PackageBuilder.addString(parameters, "os_build", deviceInfo.buildName);
        PackageBuilder.addString(parameters, "os_name", deviceInfo.osName);
        PackageBuilder.addString(parameters, "os_version", deviceInfo.osVersion);
        PackageBuilder.addString(parameters, "package_name", deviceInfo.packageName);
        PackageBuilder.addString(parameters, "push_token", activityStateCopy.pushToken);
        PackageBuilder.addString(parameters, "screen_density", deviceInfo.screenDensity);
        PackageBuilder.addString(parameters, "screen_format", deviceInfo.screenFormat);
        PackageBuilder.addString(parameters, "screen_size", deviceInfo.screenSize);
        PackageBuilder.addString(parameters, "secret_id", alltrackConfig.secretId);
        PackageBuilder.addLong(parameters, "session_count", activityStateCopy.sessionCount);
        PackageBuilder.addDuration(parameters, "session_length", activityStateCopy.sessionLength);
        PackageBuilder.addLong(parameters, "subsession_count", activityStateCopy.subsessionCount);
        PackageBuilder.addDuration(parameters, "time_spent", activityStateCopy.timeSpent);
        PackageBuilder.addString(parameters, "updated_at", deviceInfo.appUpdateTime);

        // subscription specific parameters
        PackageBuilder.addString(parameters, "billing_store", subscription.getBillingStore());
        PackageBuilder.addString(parameters, "currency", subscription.getCurrency());
        PackageBuilder.addString(parameters, "product_id", subscription.getSku());
        PackageBuilder.addString(parameters, "purchase_token", subscription.getPurchaseToken());
        PackageBuilder.addString(parameters, "receipt", subscription.getSignature());
        PackageBuilder.addLong(parameters, "revenue", subscription.getPrice());
        PackageBuilder.addDateInMilliseconds(parameters, "transaction_date", subscription.getPurchaseTime());
        PackageBuilder.addString(parameters, "transaction_id", subscription.getOrderId());

        injectFeatureFlagsWithParameters(parameters);

        checkDeviceIds(parameters);
        return parameters;
    }

    private ActivityPackage getDefaultActivityPackage(ActivityKind activityKind) {
        ActivityPackage activityPackage = new ActivityPackage(activityKind);
        activityPackage.setClientSdk(deviceInfo.clientSdk);
        return activityPackage;
    }

    private void injectFeatureFlagsWithParameters(Map<String, String> parameters) {
        if (alltrackConfig.coppaCompliantEnabled) {
            PackageBuilder.addLong(parameters, "ff_coppa", 1);
        }

        if (alltrackConfig.playStoreKidsAppEnabled) {
            PackageBuilder.addLong(parameters, "ff_play_store_kids_app", 1);
        }
    }

    public static void addString(Map<String, String> parameters, String key, String value) {
        if (TextUtils.isEmpty(value)) {
            return;
        }
        parameters.put(key, value);
    }

    public static void addBoolean(Map<String, String> parameters, String key, Boolean value) {
        if (value == null) {
            return;
        }
        int intValue = value ? 1 : 0;
        PackageBuilder.addLong(parameters, key, intValue);
    }

    static void addJsonObject(Map<String, String> parameters, String key, JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }

        PackageBuilder.addString(parameters, key, jsonObject.toString());
    }

    static void addMapJson(Map<String, String> parameters, String key, Map map) {
        if (map == null) {
            return;
        }
        if (map.size() == 0) {
            return;
        }

        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();
        PackageBuilder.addString(parameters, key, jsonString);
    }

    public static void addLong(Map<String, String> parameters, String key, long value) {
        if (value < 0) {
            return;
        }
        String valueString = Long.toString(value);
        PackageBuilder.addString(parameters, key, valueString);
    }

    private static void addDateInMilliseconds(Map<String, String> parameters, String key, long value) {
        if (value <= 0) {
            return;
        }
        Date date = new Date(value);
        PackageBuilder.addDate(parameters, key, date);
    }

    private static void addDateInSeconds(Map<String, String> parameters, String key, long value) {
        if (value <= 0) {
            return;
        }
        Date date = new Date(value * 1000);
        PackageBuilder.addDate(parameters, key, date);
    }

    private static void addDate(Map<String, String> parameters, String key, Date value) {
        if (value == null) {
            return;
        }
        String dateString = Util.dateFormatter.format(value);
        PackageBuilder.addString(parameters, key, dateString);
    }

    private static void addDuration(Map<String, String> parameters, String key, long durationInMilliSeconds) {
        if (durationInMilliSeconds < 0) {
            return;
        }
        long durationInSeconds = (durationInMilliSeconds + 500) / 1000;
        PackageBuilder.addLong(parameters, key, durationInSeconds);
    }

    private static void addDouble(Map<String, String> parameters, String key, Double value) {
        if (value == null) {
            return;
        }
        String doubleString = Util.formatString("%.5f", value);
        PackageBuilder.addString(parameters, key, doubleString);
    }

    private static void addDoubleWithoutRounding(Map<String, String> parameters, String key, Double value) {
        if (value == null) {
            return;
        }
        String doubleString = Double.toString(value);
        PackageBuilder.addString(parameters, key, doubleString);
    }

    private static void addInteger(Map<String, String> parameters, String key, Integer value) {
        if (value == null) {
            return;
        }
        String intString = Integer.toString(value);
        PackageBuilder.addString(parameters, key, intString);
    }

    private boolean containsPlayIds(Map<String, String> parameters) {
        if (parameters == null) {
            return false;
        }
        return parameters.containsKey("gps_adid");
    }

    private boolean containsFireIds(Map<String, String> parameters) {
        if (parameters == null) {
            return false;
        }
        return parameters.containsKey("fire_adid");
    }

    private void checkDeviceIds(Map<String, String> parameters) {
        if (parameters != null && !parameters.containsKey("android_id")
                && !parameters.containsKey("gps_adid")
                && !parameters.containsKey("fire_adid")
                && !parameters.containsKey("oaid")
                && !parameters.containsKey("imei")
                && !parameters.containsKey("meid")
                && !parameters.containsKey("device_id")
                && !parameters.containsKey("imeis")
                && !parameters.containsKey("meids")
                && !parameters.containsKey("device_ids")) {
            if (alltrackConfig.coppaCompliantEnabled) {
                logger.info("Missing Device IDs. COPPA enabled.");
            } else {
                logger.error("Missing Device IDs. Please check if Proguard is correctly set with Alltrack SDK");
            }
        }
    }

    private String getEventSuffix(AlltrackEvent event) {
        if (event.revenue == null) {
            return Util.formatString("'%s'", event.eventToken);
        } else {
            return Util.formatString("(%.5f %s, '%s')", event.revenue, event.currency, event.eventToken);
        }
    }
}
