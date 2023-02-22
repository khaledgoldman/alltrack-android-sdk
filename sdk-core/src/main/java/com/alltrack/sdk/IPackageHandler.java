package com.alltrack.sdk;

import android.content.Context;

import com.alltrack.sdk.network.IActivityPackageSender;

public interface IPackageHandler {
    void init(IActivityHandler activityHandler,
              Context context,
              boolean startsSending,
              IActivityPackageSender packageHandlerActivityPackageSender);

    void addPackage(ActivityPackage activityPackage);

    void sendFirstPackage();

    void pauseSending();

    void resumeSending();

    void updatePackages(SessionParameters sessionParameters);

    void flush();

    void teardown();
}
