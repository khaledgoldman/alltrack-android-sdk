package com.alltrack.sdk;

import org.json.JSONObject;

public class AlltrackEventSuccess {
    public String adid;
    public String message;
    public String timestamp;
    public String eventToken;
    public String callbackId;
    public JSONObject jsonResponse;

    @Override
    public String toString() {
        return Util.formatString("Event Success msg:%s time:%s adid:%s event:%s cid:%s json:%s",
                message,
                timestamp,
                adid,
                eventToken,
                callbackId,
                jsonResponse);
    }
}
