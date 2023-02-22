

package com.alltrack.sdk;

import com.alltrack.sdk.network.IActivityPackageSender;

public interface IAttributionHandler {
    void init(IActivityHandler activityHandler,
              boolean startsSending,
              IActivityPackageSender attributionHandlerActivityPackageSender);
    void checkSessionResponse(SessionResponseData sessionResponseData);
    void checkSdkClickResponse(SdkClickResponseData sdkClickResponseData);
    void pauseSending();
    void resumeSending();
    void getAttribution();
    void teardown();
}
