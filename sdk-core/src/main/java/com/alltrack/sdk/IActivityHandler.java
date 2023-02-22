package com.alltrack.sdk;

import android.content.Context;
import android.net.Uri;

import org.json.JSONObject;

/**
 * Created by pfms on 15/12/14.
 */
public interface IActivityHandler {
    void init(AlltrackConfig config);

    void onResume();

    void onPause();

    void trackEvent(AlltrackEvent event);

    void finishedTrackingActivity(ResponseData responseData);

    void setEnabled(boolean enabled);

    boolean isEnabled();

    void readOpenUrl(Uri url, long clickTime);

    boolean updateAttributionI(AlltrackAttribution attribution);

    void launchEventResponseTasks(EventResponseData eventResponseData);

    void launchSessionResponseTasks(SessionResponseData sessionResponseData);

    void launchSdkClickResponseTasks(SdkClickResponseData sdkClickResponseData);

    void launchAttributionResponseTasks(AttributionResponseData attributionResponseData);

    void sendReftagReferrer();

    void sendPreinstallReferrer();

    void sendInstallReferrer(ReferrerDetails referrerDetails, String referrerApi);

    void setOfflineMode(boolean enabled);

    void setAskingAttribution(boolean askingAttribution);

    void sendFirstPackages();

    void addSessionCallbackParameter(String key, String value);

    void addSessionPartnerParameter(String key, String value);

    void removeSessionCallbackParameter(String key);

    void removeSessionPartnerParameter(String key);

    void resetSessionCallbackParameters();

    void resetSessionPartnerParameters();

    void teardown();

    void setPushToken(String token, boolean preSaved);

    void gdprForgetMe();

    void disableThirdPartySharing();

    void trackThirdPartySharing(AlltrackThirdPartySharing alltrackThirdPartySharing);

    void trackMeasurementConsent(boolean consentMeasurement);

    void trackAdRevenue(String source, JSONObject adRevenueJson);

    void trackAdRevenue(AlltrackAdRevenue alltrackAdRevenue);

    void trackPlayStoreSubscription(AlltrackPlayStoreSubscription subscription);

    void gotOptOutResponse();

    Context getContext();

    String getAdid();

    AlltrackAttribution getAttribution();

    AlltrackConfig getAlltrackConfig();

    DeviceInfo getDeviceInfo();

    ActivityState getActivityState();

    SessionParameters getSessionParameters();
}
