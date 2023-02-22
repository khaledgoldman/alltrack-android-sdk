package com.alltrack.sdk.xiaomi;

import android.content.Context;

import com.alltrack.sdk.ILogger;
import com.alltrack.sdk.ReferrerDetails;
import com.miui.referrer.api.GetAppsReferrerDetails;

public class Util {
   public synchronized static ReferrerDetails getXiaomiInstallReferrerDetails(Context context, ILogger logger) {
      if (!AlltrackXiaomiReferrer.shouldReadXiaomiReferrer) {
         return null;
      }

      GetAppsReferrerDetails getAppsReferrerDetails = XiaomiReferrerClient.getReferrer(context, logger, 3000);
      if (getAppsReferrerDetails == null) {
         return null;
      }

      return new ReferrerDetails(getAppsReferrerDetails.getInstallReferrer(),
                                 getAppsReferrerDetails.getReferrerClickTimestampSeconds(),
                                 getAppsReferrerDetails.getInstallBeginTimestampSeconds(),
                                 getAppsReferrerDetails.getReferrerClickTimestampServerSeconds(),
                                 getAppsReferrerDetails.getInstallBeginTimestampServerSeconds(),
                                 getAppsReferrerDetails.getInstallVersion(), null);
   }
}
