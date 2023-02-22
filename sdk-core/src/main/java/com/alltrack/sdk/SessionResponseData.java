package com.alltrack.sdk;

import org.json.JSONObject;

public class SessionResponseData extends ResponseData {
    private String sdkPlatform;

    public SessionResponseData(final ActivityPackage activityPackage) {
        this.sdkPlatform = Util.getSdkPrefixPlatform(activityPackage.getClientSdk());
    }

    public AlltrackSessionSuccess getSuccessResponseData() {
        if (!success) {
            return null;
        }

        AlltrackSessionSuccess successResponseData = new AlltrackSessionSuccess();
        if ("unity".equals(this.sdkPlatform)) {
            // Unity platform.
            successResponseData.message = message != null ? message : "";
            successResponseData.timestamp = timestamp != null ? timestamp : "";
            successResponseData.adid = adid != null ? adid : "";
            successResponseData.jsonResponse = jsonResponse != null ? jsonResponse : new JSONObject();
        } else {
            // Rest of all platforms.
            successResponseData.message = message;
            successResponseData.timestamp = timestamp;
            successResponseData.adid = adid;
            successResponseData.jsonResponse = jsonResponse;
        }

        return successResponseData;
    }

    public AlltrackSessionFailure getFailureResponseData() {
        if (success) {
            return null;
        }

        AlltrackSessionFailure failureResponseData = new AlltrackSessionFailure();
        if ("unity".equals(this.sdkPlatform)) {
            // Unity platform.
            failureResponseData.message = message != null ? message : "";
            failureResponseData.timestamp = timestamp != null ? timestamp : "";
            failureResponseData.adid = adid != null ? adid : "";
            failureResponseData.willRetry = willRetry;
            failureResponseData.jsonResponse = jsonResponse != null ? jsonResponse : new JSONObject();
        } else {
            // Rest of all platforms.
            failureResponseData.message = message;
            failureResponseData.timestamp = timestamp;
            failureResponseData.adid = adid;
            failureResponseData.willRetry = willRetry;
            failureResponseData.jsonResponse = jsonResponse;
        }

        return failureResponseData;
    }
}
