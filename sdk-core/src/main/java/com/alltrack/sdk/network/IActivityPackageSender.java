package com.alltrack.sdk.network;

import com.alltrack.sdk.ActivityPackage;
import com.alltrack.sdk.ResponseData;

import java.util.Map;

public interface IActivityPackageSender {
    interface ResponseDataCallbackSubscriber {
        void onResponseDataCallback(ResponseData responseData);
    }

    void sendActivityPackage(ActivityPackage activityPackage,
                             Map<String, String> sendingParameters,
                             ResponseDataCallbackSubscriber responseCallback);

    ResponseData sendActivityPackageSync(ActivityPackage activityPackage,
                             Map<String, String> sendingParameters);
}
