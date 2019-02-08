package com.capitalone.dashboard.core.json.gen;

import com.atlassian.jira.rest.client.internal.json.gen.JsonGenerator;
import com.capitalone.dashboard.api.domain.TestExecution;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * This class will generate a JSON Object for a Test Execution
 */
public class TestExecJsonGenerator implements JsonGenerator<TestExecution> {
    public JSONObject generate(TestExecution testExecution) throws JSONException {
        return null;
    }
}
