package com.capitalone.dashboard.core.json;

import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.capitalone.dashboard.core.json.util.RendereableItem;
import com.capitalone.dashboard.core.json.util.RendereableItemImpl;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * This class will parse the JSON for any Renderable Item
 */
public class RendereableItemJsonParser implements JsonObjectParser<RendereableItem> {
    private static final String KEY_RAW="raw";
    private static final String KEY_RENDERED="rendered";

    public RendereableItem parse(JSONObject jsonObject) throws JSONException {
        return new RendereableItemImpl(jsonObject.optString(KEY_RAW),jsonObject.optString(KEY_RENDERED));
    }
}
