package com.alltrack.sdk.webbridge;

import android.webkit.WebView;
import android.app.Application;

/**
 * Created by uerceg on 10/06/16.
 */
public class AlltrackBridge {
    private static AlltrackBridgeInstance defaultInstance;

    // New builder gets dependencies
    public static synchronized AlltrackBridgeInstance registerAndGetInstance(Application application, WebView webView) {
        if (defaultInstance == null) {
            defaultInstance = new AlltrackBridgeInstance(application, webView);
        }
        return defaultInstance;
    }

    public static synchronized AlltrackBridgeInstance getDefaultInstance() {
        if (defaultInstance == null) {
            defaultInstance = new AlltrackBridgeInstance();
        }
        return defaultInstance;
    }

    public static void setWebView(WebView webView) {
        AlltrackBridge.getDefaultInstance().setWebView(webView);
    }

    public static void setApplicationContext(Application application) {
        AlltrackBridge.getDefaultInstance().setApplicationContext(application);
    }

    public static synchronized void unregister() {
        if (defaultInstance != null) {
            defaultInstance.unregister();
        }
        defaultInstance = null;
    }
}
