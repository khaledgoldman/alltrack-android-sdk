package com.alltrack.sdk.vivo;

import android.content.Context;

import com.alltrack.sdk.ILogger;
import com.alltrack.sdk.ReferrerDetails;

public class Util {
   public synchronized static ReferrerDetails getVivoInstallReferrerDetails(Context context, ILogger logger) {
      if (!AlltrackVivoReferrer.shouldReadVivoReferrer) {
         return null;
      }

      return VivoReferrerClient.getReferrer(context, logger);
   }
}
