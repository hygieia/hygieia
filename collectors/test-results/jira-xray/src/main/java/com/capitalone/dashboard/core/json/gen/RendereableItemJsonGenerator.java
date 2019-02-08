package com.capitalone.dashboard.core.json.gen;

import com.atlassian.jira.rest.client.internal.json.gen.JsonGenerator;

import com.capitalone.dashboard.core.json.util.RendereableItem;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * This class will generate a JSON Object for any Renderable Item
 */
public class RendereableItemJsonGenerator  implements JsonGenerator<RendereableItem> {

    private final static String KEY_RAW="raw";
    private final static String KEY_RENDERED="rendered";
    public JSONObject generate(RendereableItem rendereableItem) throws JSONException {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(KEY_RAW,rendereableItem.getRaw());
        jsonObject.put(KEY_RENDERED,rendereableItem.getRendered());

    return jsonObject;}
}
