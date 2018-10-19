package com.capitalone.dashboard.client.core.json;

import com.atlassian.jira.rest.client.internal.json.GenericJsonArrayParser;
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil;
import com.capitalone.dashboard.client.api.domain.Comment;
import com.capitalone.dashboard.client.api.domain.TestStep;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;

/**
 * This class will parse the JSON for a Test Step
 */
public class TestStepJsonParser implements JsonObjectParser<TestStep> {

    private static final RendereableItemJsonParser RENDEREABLE_JSON_PARSER =new RendereableItemJsonParser();


    private final static String KEY_ID="id";
    private final static String KEY_INDEX="index";
    private final static String KEY_STEP="step";
    private final static String KEY_DATA="data";
    private final static String KEY_RESULT="result";
    private final static String KEY_ATTACHMENTS="attachments";
    private final static String KEY_EVIDENCES="evidences";
    private final static String KEY_DEFECTS="defects";
    private final static String KEY_STATUS="status";
    private final static String KEY_COMMENT="comment";


    public TestStep parse(JSONObject jsonObject) throws JSONException {
        jsonObject.put("self",""); // TODO: ADD URI.
        URI selfUri = JsonParseUtil.getSelfUri(jsonObject);
        String key =" THERE IS NO KEY FOR TEST RUN AT X-RAY DIRECT REST API"; // TODO: GET THE ISSUE KEY
        GenericJsonArrayParser evidencesParser=new GenericJsonArrayParser(new EvidenceJsonParser());
        GenericJsonArrayParser defectsParser=new GenericJsonArrayParser(new DefectJsonParser());

        return new TestStep(selfUri,key
                ,Long.parseLong(jsonObject.getString(KEY_ID))
                ,Integer.parseInt(jsonObject.getString(KEY_INDEX))
                , RENDEREABLE_JSON_PARSER.parse(jsonObject.getJSONObject(KEY_STEP))
                , RENDEREABLE_JSON_PARSER.parse(jsonObject.optJSONObject(KEY_DATA))
                , RENDEREABLE_JSON_PARSER.parse(jsonObject.getJSONObject(KEY_RESULT))
                ,evidencesParser.parse(jsonObject.getJSONArray(KEY_ATTACHMENTS))
                ,parseStatus(jsonObject)
                , new Comment(RENDEREABLE_JSON_PARSER.parse(jsonObject.getJSONObject(KEY_COMMENT)))
                ,defectsParser.parse(jsonObject.getJSONArray(KEY_DEFECTS))
                ,evidencesParser.parse(jsonObject.getJSONArray(KEY_EVIDENCES))
        );
    }

    private TestStep.Status parseStatus(JSONObject jsonObject) throws JSONException {
        if(jsonObject.getString(KEY_STATUS).equals(TestStep.Status.PASS.name()))
            return TestStep.Status.PASS;
        if(jsonObject.getString(KEY_STATUS).equals(TestStep.Status.ABORTED.name()))
            return TestStep.Status.ABORTED;
        if(jsonObject.getString(KEY_STATUS).equals(TestStep.Status.EXECUTING.name()))
            return TestStep.Status.EXECUTING;
        if(jsonObject.getString(KEY_STATUS).equals(TestStep.Status.FAIL.name()))
            return TestStep.Status.FAIL;
        if(jsonObject.getString(KEY_STATUS).equals(TestStep.Status.TODO.name()))
            return TestStep.Status.TODO;
        return null;
    }


}