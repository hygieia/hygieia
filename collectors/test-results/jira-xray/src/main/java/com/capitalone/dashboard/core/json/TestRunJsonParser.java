package com.capitalone.dashboard.core.json;

import com.atlassian.jira.rest.client.internal.json.GenericJsonArrayParser;
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil;
import com.capitalone.dashboard.api.domain.Comment;
import com.capitalone.dashboard.api.domain.TestRun;
import com.capitalone.dashboard.api.domain.TestStep;
import com.capitalone.dashboard.core.json.util.XrayJiraDateFormatter;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.text.ParseException;
import java.util.Date;

/**
 * This class will parse the JSON for a Test Run
 */
public class TestRunJsonParser implements JsonObjectParser<TestRun> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunJsonParser.class);

    private final static DefectJsonParser DEFECT_PARSER =new DefectJsonParser();
    private final static TestStepJsonParser TEST_STEP_PARSER =new TestStepJsonParser();

    public final static String KEY_ID="id";
    public final static String KEY_STATUS="status";
    public final static String KEY_EXECBY="executedBy";
    public final static String KEY_ASSIGNEE="assignee";
    public final static String KEY_STARTEDON="startedOn";
    public final static String KEY_FINISHEDON="finishedOn";
    public final static String KEY_EXAMPLES="examples";
    public final static String KEY_COMMENT="comment";
    public final static String KEY_TESTSTEPS="steps";
    // TODO: CUMCUMBER TESTS
    // public final static String KEY_SCENARIO="scenario";
    // public final static String KEY_SCENARIO_OUTLINE="scenarioOutline";


    public TestRunJsonParser(){
    }

    public TestRun parse(JSONObject jsonObject) throws JSONException {
        jsonObject.put("self",""); // TODO: ADD URI.
        URI selfUri = JsonParseUtil.getSelfUri(jsonObject);
        String key =" THERE IS NO KEY FOR TEST RUN AT X-RAY DIRECT REST API"; // TODO: GET THE ISSUE KEY
        Long id = Long.valueOf(jsonObject.getLong(KEY_ID));
        TestRun.Status status=getStatus(jsonObject);
        GenericJsonArrayParser arrayParser=new GenericJsonArrayParser(DEFECT_PARSER);
        Iterable<TestStep> testSteps=null;

        Date startedOn = null;
        Date finishedOn = null;
        String executedBy = null;
        String assignee = null;

        try {
            if (!jsonObject.isNull(KEY_STARTEDON)) {
                startedOn = new XrayJiraDateFormatter().parse(jsonObject.getString(KEY_STARTEDON));
            }
            if (!jsonObject.isNull(KEY_FINISHEDON)) {
                finishedOn= new XrayJiraDateFormatter().parse(jsonObject.getString(KEY_FINISHEDON));
            }
            if (!jsonObject.isNull(KEY_EXECBY)) {
                executedBy = jsonObject.getString(KEY_EXECBY);
            }
            if (!jsonObject.isNull(KEY_ASSIGNEE)) {
                assignee = jsonObject.getString(KEY_ASSIGNEE);
            }
            if (!jsonObject.isNull(KEY_TESTSTEPS)) {
                arrayParser=new GenericJsonArrayParser(TEST_STEP_PARSER);
                testSteps=arrayParser.parse(jsonObject.getJSONArray(KEY_TESTSTEPS));
            }
        } catch (ParseException e) {
            LOGGER.error("Unable to Parse JSON: " + e);
            throw new JSONException(e.getMessage());
        }

        TestRun res=new TestRun(selfUri,key,id,status,startedOn,finishedOn,assignee,executedBy,testSteps);
        return res;
    }

    //TODO: ADD SUPPORT FOR CUSTOM STATUSES.
    private TestRun.Status getStatus(JSONObject jsonObject) throws JSONException {
        if(jsonObject.get(KEY_STATUS).equals("TODO")){
            return TestRun.Status.TODO;
        }
        if(jsonObject.get(KEY_STATUS).equals("EXECUTING")){
            return TestRun.Status.EXECUTING;
        }
        if(jsonObject.get(KEY_STATUS).equals("ABORTED")){
            return TestRun.Status.ABORTED;
        }
        if(jsonObject.get(KEY_STATUS).equals("FAIL")){
            return TestRun.Status.FAIL;
        }
        if(jsonObject.get(KEY_STATUS).equals("PASS")){
            return TestRun.Status.PASS;
        }
        return null;
    }

    private Comment parseComment(JSONObject jsonObject){
        try {
            return new Comment(jsonObject.getString(KEY_COMMENT), jsonObject.getString(KEY_COMMENT));
        }catch(JSONException jE){
            return new Comment("","");
        }
    }

}