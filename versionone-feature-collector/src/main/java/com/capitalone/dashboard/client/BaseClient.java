package com.capitalone.dashboard.client;

import com.capitalone.dashboard.util.ClientUtil;
import org.json.simple.JSONObject;

public class BaseClient {



    protected String getJSONDateString(JSONObject obj, String field) {
        return ClientUtil.toCanonicalDate(getJSONString(obj, field));
    }

    protected String getJSONString(JSONObject obj, String field) {
        return ClientUtil.sanitizeResponse((String) obj.get(field));
    }
}
