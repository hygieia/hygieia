package com.capitalone.dashboard.client.core.json;

import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.capitalone.dashboard.client.api.domain.TestExecution;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * This class will parse the JSON for a Test Execution
 */
public class TestExecJsonParser implements JsonObjectParser<TestExecution> {
    public TestExecution parse(JSONObject jsonObject) throws JSONException {
        return null;
    }
}
