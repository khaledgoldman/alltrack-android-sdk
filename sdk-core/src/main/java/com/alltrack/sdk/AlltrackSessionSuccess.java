package com.alltrack.sdk;

import org.json.JSONObject;

public class AlltrackSessionSuccess {
    public String adid;
    public String message;
    public String timestamp;
    public JSONObject jsonResponse;

    @Override
    public String toString() {
        return Util.formatString("Session Success msg:%s time:%s adid:%s json:%s",
                message,
                timestamp,
                adid,
                jsonResponse);
    }
}
