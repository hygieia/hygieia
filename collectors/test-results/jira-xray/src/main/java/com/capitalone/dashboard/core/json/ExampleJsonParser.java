package com.capitalone.dashboard.core.json;

import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil;
import com.capitalone.dashboard.api.domain.Example;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * This class will parse the JSON for an Example
 */
public class ExampleJsonParser implements JsonObjectParser<Example> {
    private final static String KEY_ID="id";
    private final static String KEY_VALUES="values";

    public Example parse(JSONObject jsonObject) throws JSONException {
        jsonObject.put("self",""); // TODO: ADD URI.
        URI selfUri = JsonParseUtil.getSelfUri(jsonObject);
        String key =" THERE IS NO KEY FOR TEST RUN AT X-RAY DIRECT REST API"; // TODO: GET THE ISSUE KEY
        return new Example(selfUri,key,jsonObject.getLong(KEY_ID)
                ,jsonObject.getInt("rank")
                ,getValues(jsonObject)
                ,getStatus());
    }

    private Example.Status getStatus(){
        return null;
    }

    private List<Object> getValues(JSONObject jsonObject) throws JSONException {
        JSONArray jsonValues=jsonObject.getJSONArray(KEY_VALUES);
        ArrayList<Object> values=new ArrayList<Object>();
        for (int i = 0; i < jsonValues.length(); i++){
                values.add(jsonValues.getString(i));
        }
   return values;
    }
}
