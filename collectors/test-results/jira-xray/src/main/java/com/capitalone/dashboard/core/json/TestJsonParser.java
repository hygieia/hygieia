package com.capitalone.dashboard.core.json;

import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil;
import com.capitalone.dashboard.api.domain.TestExecution;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;

/**
 * This class will parse the JSON for a Test
 */
public class TestJsonParser implements JsonObjectParser<TestExecution.Test> {

        public TestExecution.Test parse(JSONObject json) throws JSONException {
            json.put("self","");
            URI selfUri = JsonParseUtil.getSelfUri(json);
            String key = json.getString("key");
            Long id = Long.valueOf(json.getLong("id"));
            return new TestExecution.Test(selfUri, key, id);
        }

}
