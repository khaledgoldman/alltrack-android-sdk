var Alltrack = {
    onCreate: function (alltrackConfig) {
        if (alltrackConfig && !alltrackConfig.getSdkPrefix()) {
            alltrackConfig.setSdkPrefix(this.getSdkPrefix());
        }
        this.alltrackConfig = alltrackConfig;
        if (AlltrackBridge) {
            AlltrackBridge.onCreate(JSON.stringify(alltrackConfig));
        }
    },

    getConfig: function () {
        return this.alltrackConfig;
    },

    trackEvent: function (alltrackEvent) {
        if (AlltrackBridge) {
            AlltrackBridge.trackEvent(JSON.stringify(alltrackEvent));
        }
    },

    trackAdRevenue: function(source, payload) {
        if (AlltrackBridge) {
            AlltrackBridge.trackAdRevenue(source, payload);
        }
    },

    onResume: function () {
        if (AlltrackBridge) {
            AlltrackBridge.onResume();
        }
    },

    onPause: function () {
        if (AlltrackBridge) {
            AlltrackBridge.onPause();
        }
    },

    setEnabled: function (enabled) {
        if (AlltrackBridge) {
            AlltrackBridge.setEnabled(enabled);
        }
    },

    isEnabled: function (callback) {
        if (!AlltrackBridge) {
            return undefined;
        }
        // supports legacy return with callback
        if (arguments.length === 1) {
            // with manual string call
            if (typeof callback === 'string' || callback instanceof String) {
                this.isEnabledCallbackName = callback;
            } else {
                // or save callback and call later
                this.isEnabledCallbackName = 'Alltrack.alltrack_isEnabledCallback';
                this.isEnabledCallbackFunction = callback;
            }
            AlltrackBridge.isEnabled(this.isEnabledCallbackName);
        } else {
            return AlltrackBridge.isEnabled();
        }
    },

    alltrack_isEnabledCallback: function (isEnabled) {
        if (AlltrackBridge && this.isEnabledCallbackFunction) {
            this.isEnabledCallbackFunction(isEnabled);
        }
    },

    appWillOpenUrl: function (url) {
        if (AlltrackBridge) {
            AlltrackBridge.appWillOpenUrl(url);
        }
    },

    setReferrer: function (referrer) {
        if (AlltrackBridge) {
            AlltrackBridge.setReferrer(referrer);
        }
    },

    setOfflineMode: function(isOffline) {
        if (AlltrackBridge) {
            AlltrackBridge.setOfflineMode(isOffline);
        }
    },

    sendFirstPackages: function() {
        if (AlltrackBridge) {
            AlltrackBridge.sendFirstPackages();
        }
    },

    addSessionCallbackParameter: function(key, value) {
        if (AlltrackBridge) {
            AlltrackBridge.addSessionCallbackParameter(key, value);
        }
    },

    addSessionPartnerParameter: function(key, value) {
        if (AlltrackBridge) {
            AlltrackBridge.addSessionPartnerParameter(key, value);
        }
    },

    removeSessionCallbackParameter: function(key) {
        if (AlltrackBridge) {
            AlltrackBridge.removeSessionCallbackParameter(key);
        }
    },

    removeSessionPartnerParameter: function(key) {
        if (AlltrackBridge) {
            AlltrackBridge.removeSessionPartnerParameter(key);
        }
    },

    resetSessionCallbackParameters: function() {
        if (AlltrackBridge) {
            AlltrackBridge.resetSessionCallbackParameters();
        }
    },

    resetSessionPartnerParameters: function() {
        if (AlltrackBridge) {
            AlltrackBridge.resetSessionPartnerParameters();
        }
    },

    setPushToken: function(token) {
        if (AlltrackBridge) {
            AlltrackBridge.setPushToken(token);
        }
    },

    gdprForgetMe: function() {
        if (AlltrackBridge) {
            AlltrackBridge.gdprForgetMe();
        }
    },

    disableThirdPartySharing: function() {
        if (AlltrackBridge) {
            AlltrackBridge.disableThirdPartySharing();
        }
    },

    trackThirdPartySharing: function(alltrackThirdPartySharing) {
        if (AlltrackBridge) {
            AlltrackBridge.trackThirdPartySharing(JSON.stringify(alltrackThirdPartySharing));
        }
    },

    trackMeasurementConsent: function(consentMeasurement) {
        if (AlltrackBridge) {
            AlltrackBridge.trackMeasurementConsent(consentMeasurement);
        }
    },

    getGoogleAdId: function (callback) {
        if (AlltrackBridge) {
            if (typeof callback === 'string' || callback instanceof String) {
                this.getGoogleAdIdCallbackName = callback;
            } else {
                this.getGoogleAdIdCallbackName = 'Alltrack.alltrack_getGoogleAdIdCallback';
                this.getGoogleAdIdCallbackFunction = callback;
            }
            AlltrackBridge.getGoogleAdId(this.getGoogleAdIdCallbackName);
        }
    },

    alltrack_getGoogleAdIdCallback: function (googleAdId) {
        if (AlltrackBridge && this.getGoogleAdIdCallbackFunction) {
            this.getGoogleAdIdCallbackFunction(googleAdId);
        }
    },

    getAmazonAdId: function (callback) {
        if (AlltrackBridge) {
            return AlltrackBridge.getAmazonAdId();
        } else {
            return undefined;
        }
    },

    getAdid: function () {
        if (AlltrackBridge) {
            return AlltrackBridge.getAdid();
        } else {
            return undefined;
        }
    },

    getAttribution: function (callback) {
        if (AlltrackBridge) {
            AlltrackBridge.getAttribution(callback);
        }
    },

    getSdkVersion: function () {
        if (AlltrackBridge) {
             return this.getSdkPrefix() + '@' + AlltrackBridge.getSdkVersion();
        } else {
            return undefined;
        }
    },

    getSdkPrefix: function () {
        if (this.alltrackConfig) {
            return this.alltrackConfig.getSdkPrefix();
        } else {
            return 'web-bridge0.0.1';
        }
    },

    teardown: function() {
        if (AlltrackBridge) {
            AlltrackBridge.teardown();
        }
        this.alltrackConfig = undefined;
        this.isEnabledCallbackName = undefined;
        this.isEnabledCallbackFunction = undefined;
        this.getGoogleAdIdCallbackName = undefined;
        this.getGoogleAdIdCallbackFunction = undefined;
    },
};
