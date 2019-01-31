package com.capitalone.dashboard.core.json;

import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.capitalone.dashboard.api.domain.TestRun;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * This class will parse the JSON for a Status
 */
public class StatusJsonParser implements JsonObjectParser<TestRun.Status> {
    public TestRun.Status parse(JSONObject jsonObject) throws JSONException {
        throw new IllegalArgumentException("NOT IMPLEMENTED YET");
    }
}
