package com.capitalone.dashboard.core.json;

import com.atlassian.jira.rest.client.internal.json.JsonArrayParser;
import com.capitalone.dashboard.api.domain.TestExecution;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import java.util.ArrayList;

/**
 * This class will parse the JSON for a Test Array
 */
public class TestArrayJsonParser implements JsonArrayParser<Iterable<TestExecution.Test>> {
    private final static TestJsonParser T_PARSER =new TestJsonParser();

    public Iterable<TestExecution.Test> parse(JSONArray jsonArray) throws JSONException {
        ArrayList<TestExecution.Test> tests=new ArrayList<TestExecution.Test>();
        for (int i = 0; i < jsonArray.length(); i++){
            tests.add(T_PARSER.parse(jsonArray.getJSONObject(i)));
        }
    return tests;
    }
}
