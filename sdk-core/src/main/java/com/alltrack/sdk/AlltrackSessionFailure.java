package com.alltrack.sdk;

import org.json.JSONObject;

public class AlltrackSessionFailure {
    public boolean willRetry;
    public String adid;
    public String message;
    public String timestamp;
    public JSONObject jsonResponse;

    @Override
    public String toString() {
        return Util.formatString("Session Failure msg:%s time:%s adid:%s retry:%b json:%s",
                message,
                timestamp,
                adid,
                willRetry,
                jsonResponse);
    }
}
