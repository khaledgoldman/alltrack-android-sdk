function AlltrackConfig(appToken, environment, legacy) {
    this.allowSuppressLogLevel = null;

    if (arguments.length === 2) {
        // new format does not require bridge as first parameter
        this.appToken = appToken;
        this.environment = environment;
    } else if (arguments.length === 3) {
        // new format with allowSuppressLogLevel
        if (typeof(legacy) == typeof(true)) {
            this.appToken = appToken;
            this.environment = environment;
            this.allowSuppressLogLevel = legacy;
        } else {
            // old format with first argument being the bridge instance
            this.bridge = appToken;
            this.appToken = environment;
            this.environment = legacy;
        }
    }

    this.eventBufferingEnabled = null;
    this.sendInBackground = null;
    this.logLevel = null;
    this.sdkPrefix = null;
    this.processName = null;
    this.defaultTracker = null;
    this.externalDeviceId = null;
    this.attributionCallbackName = null;
    this.attributionCallbackFunction = null;
    this.deviceKnown = null;
    this.needsCost = null;
    this.eventSuccessCallbackName = null;
    this.eventSuccessCallbackFunction = null;
    this.eventFailureCallbackName = null;
    this.eventFailureCallbackFunction = null;
    this.sessionSuccessCallbackName = null;
    this.sessionSuccessCallbackFunction = null;
    this.sessionFailureCallbackName = null;
    this.sessionFailureCallbackFunction = null;
    this.openDeferredDeeplink = null;
    this.deferredDeeplinkCallbackName = null;
    this.deferredDeeplinkCallbackFunction = null;
    this.delayStart = null;
    this.userAgent = null;
    this.secretId = null;
    this.info1 = null;
    this.info2 = null;
    this.info3 = null;
    this.info4 = null;
    this.fbPixelDefaultEventToken = null;
    this.fbPixelMapping = [];
    this.urlStrategy = null;
    this.preinstallTrackingEnabled = null;
    this.preinstallFilePath = null;
    this.playStoreKidsAppEnabled = null;
    this.coppaCompliantEnabled = null;
}

AlltrackConfig.EnvironmentSandbox = 'sandbox';
AlltrackConfig.EnvironmentProduction = 'production';

AlltrackConfig.UrlStrategyIndia = "url_strategy_india";
AlltrackConfig.UrlStrategyChina = "url_strategy_china";
AlltrackConfig.UrlStrategyCn = "url_strategy_cn";
AlltrackConfig.DataResidencyEU = "data_residency_eu";
AlltrackConfig.DataResidencyTR = "data_residency_tr";
AlltrackConfig.DataResidencyUS = "data_residency_us";

AlltrackConfig.LogLevelVerbose = 'VERBOSE',
AlltrackConfig.LogLevelDebug = 'DEBUG',
AlltrackConfig.LogLevelInfo = 'INFO',
AlltrackConfig.LogLevelWarn = 'WARN',
AlltrackConfig.LogLevelError = 'ERROR',
AlltrackConfig.LogLevelAssert = 'ASSERT',
AlltrackConfig.LogLevelSuppress = 'SUPPRESS',

AlltrackConfig.prototype.getBridge = function() {
    return this.bridge;
};

AlltrackConfig.prototype.setEventBufferingEnabled = function(isEnabled) {
    this.eventBufferingEnabled = isEnabled;
};

AlltrackConfig.prototype.setSendInBackground = function(isEnabled) {
    this.sendInBackground = isEnabled;
};

AlltrackConfig.prototype.setLogLevel = function(logLevel) {
    this.logLevel = logLevel;
};

AlltrackConfig.prototype.getSdkPrefix = function() {
    return this.sdkPrefix;
};

AlltrackConfig.prototype.setSdkPrefix = function(sdkPrefix) {
    this.sdkPrefix = sdkPrefix;
};

AlltrackConfig.prototype.setProcessName = function(processName) {
    this.processName = processName;
};

AlltrackConfig.prototype.setDefaultTracker = function(defaultTracker) {
    this.defaultTracker = defaultTracker;
};

AlltrackConfig.prototype.setExternalDeviceId = function(externalDeviceId) {
    this.externalDeviceId = externalDeviceId;
};

AlltrackConfig.prototype.setAttributionCallback = function(callback) {
    if (typeof callback === 'string' || callback instanceof String) {
        this.attributionCallbackName = callback;
    } else {
        this.attributionCallbackName = 'Alltrack.getConfig().alltrack_attributionCallback';
        this.attributionCallbackFunction = callback;
    }
};

AlltrackConfig.prototype.alltrack_attributionCallback = function(attribution) {
    if (this.attributionCallbackFunction) {
        this.attributionCallbackFunction(attribution);
    }
};

AlltrackConfig.prototype.setDeviceKnown = function(deviceKnown) {
    this.deviceKnown = deviceKnown;
};

AlltrackConfig.prototype.setNeedsCost = function(needsCost) {
    this.needsCost = needsCost;
};

AlltrackConfig.prototype.setEventSuccessCallback = function(callback) {
    if (typeof callback === 'string' || callback instanceof String) {
        this.eventSuccessCallbackName = callback;
    } else {
        this.eventSuccessCallbackName = 'Alltrack.getConfig().alltrack_eventSuccessCallback';
        this.eventSuccessCallbackFunction = callback;
    }
};

AlltrackConfig.prototype.alltrack_eventSuccessCallback = function(eventSuccess) {
    if (this.eventSuccessCallbackFunction) {
        this.eventSuccessCallbackFunction(eventSuccess);
    }
};

AlltrackConfig.prototype.setEventFailureCallback = function(callback) {
    if (typeof callback === 'string' || callback instanceof String) {
        this.eventFailureCallbackName = callback;
    } else {
        this.eventFailureCallbackName = 'Alltrack.getConfig().alltrack_eventFailureCallback';
        this.eventFailureCallbackFunction = callback;
    }
};

AlltrackConfig.prototype.alltrack_eventFailureCallback = function(eventFailure) {
    if (this.eventFailureCallbackFunction) {
        this.eventFailureCallbackFunction(eventFailure);
    }
};

AlltrackConfig.prototype.setSessionSuccessCallback = function(callback) {
    if (typeof callback === 'string' || callback instanceof String) {
        this.sessionSuccessCallbackName = callback;
    } else {
        this.sessionSuccessCallbackName = 'Alltrack.getConfig().alltrack_sessionSuccessCallback';
        this.sessionSuccessCallbackFunction = callback;
    }
};

AlltrackConfig.prototype.alltrack_sessionSuccessCallback = function(sessionSuccess) {
    if (this.sessionSuccessCallbackFunction) {
        this.sessionSuccessCallbackFunction(sessionSuccess);
    }
};

AlltrackConfig.prototype.setSessionFailureCallback = function(callback) {
    if (typeof callback === 'string' || callback instanceof String) {
        this.sessionFailureCallbackName = callback;
    } else {
        this.sessionFailureCallbackName = 'Alltrack.getConfig().alltrack_sessionFailureCallback';
        this.sessionFailureCallbackFunction = callback;
    }
};

AlltrackConfig.prototype.alltrack_sessionFailureCallback = function(sessionFailure) {
    if (this.sessionFailureCallbackFunction) {
        this.sessionFailureCallbackFunction(sessionFailure);
    }
};

AlltrackConfig.prototype.setOpenDeferredDeeplink = function(shouldOpen) {
    this.openDeferredDeeplink = shouldOpen;
};

AlltrackConfig.prototype.setDeferredDeeplinkCallback = function(callback) {
    if (typeof callback === 'string' || callback instanceof String) {
        this.deferredDeeplinkCallbackName = callback;
    } else {
        this.deferredDeeplinkCallbackName = 'Alltrack.getConfig().alltrack_deferredDeeplinkCallback';
        this.deferredDeeplinkCallbackFunction = callback;
    }
};

AlltrackConfig.prototype.alltrack_deferredDeeplinkCallback = function(deeplink) {
    if (this.deferredDeeplinkCallbackFunction) {
        this.deferredDeeplinkCallbackFunction(deeplink);
    }
};

AlltrackConfig.prototype.setDelayStart = function(delayStart) {
    this.delayStart = delayStart;
};

AlltrackConfig.prototype.setUserAgent = function(userAgent) {
    this.userAgent = userAgent;
};

AlltrackConfig.prototype.setAppSecret = function(secretId, info1, info2, info3, info4) {
    this.secretId = secretId;
    this.info1 = info1;
    this.info2 = info2;
    this.info3 = info3;
    this.info4 = info4;
};

AlltrackConfig.prototype.setReadMobileEquipmentIdentity = function(readMobileEquipmentIdentity) {};

AlltrackConfig.prototype.setFbPixelDefaultEventToken = function(fbPixelDefaultEventToken) {
    this.fbPixelDefaultEventToken = fbPixelDefaultEventToken;
};

AlltrackConfig.prototype.addFbPixelMapping = function(fbEventNameKey, altEventTokenValue) {
    this.fbPixelMapping.push(fbEventNameKey);
    this.fbPixelMapping.push(altEventTokenValue);
};

AlltrackConfig.prototype.setUrlStrategy = function(urlStrategy) {
    this.urlStrategy = urlStrategy;
};

AlltrackConfig.prototype.setPreinstallTrackingEnabled = function(preinstallTrackingEnabled) {
    this.preinstallTrackingEnabled = preinstallTrackingEnabled;
};

AlltrackConfig.prototype.setPreinstallFilePath = function(preinstallFilePath) {
    this.preinstallFilePath = preinstallFilePath;
};

AlltrackConfig.prototype.setPlayStoreKidsAppEnabled = function(isEnabled) {
    this.playStoreKidsAppEnabled = isEnabled;
};

AlltrackConfig.prototype.setCoppaCompliantEnabled = function(isEnabled) {
    this.coppaCompliantEnabled = isEnabled;
};
