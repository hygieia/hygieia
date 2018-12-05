package com.capitalone.dashboard.core.json.gen;

import com.atlassian.jira.rest.client.internal.json.gen.JsonGenerator;
import com.capitalone.dashboard.api.domain.Defect;
import com.capitalone.dashboard.api.domain.Evidence;
import com.capitalone.dashboard.api.domain.TestRun;
import com.capitalone.dashboard.api.domain.TestStep;
import com.google.common.collect.Iterables;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;
import java.util.Optional;

/**
 * This class will generate a JSON Object for a Test Run
 */
public class TestRunJsonGenerator implements JsonGenerator<TestRun> {
    public final static String KEY_ID="id";
    public final static String KEY_EXECBY="executedBy";
    public final static String KEY_STARTEDON="startedOn";
    public final static String KEY_ASSIGNEE="assignee";
    public final static String KEY_FINISHEDON="finishedON";
    // UPDATABLE FIELDS
    public final static String KEY_COMMENT="comment";
    public final static String KEY_STATUS="status";
    public final static String KEY_DEFECTS="defects";
    public final static String KEY_EVIDENCES="evidences";
    public final static String KEY_EXAMPLES="examples";
    public final static String KEY_TESTSTEPS="steps";
    // CUMCUMBER TESTS
    public final static String KEY_SCENARIO="scenario";
    public final static String KEY_SCENARIO_OUTLINE="scenarioOutline";


    private final static RendereableItemJsonGenerator COMMENTS_GENERATOR =new RendereableItemJsonGenerator();
    private final static TestStepJsonGenerator TEST_STEPS_GENERATOR =new TestStepJsonGenerator();
    private final static DefectJSONGenerator DEFECTS_GENERATOR =new DefectJSONGenerator();
    private final static EvidenceJsonGenerator EVIDENCES_GENERATOR =new EvidenceJsonGenerator();


    public JSONObject generate(TestRun testRun) throws JSONException {
        JSONObject ex=new JSONObject();
        // INMUTABLE FIELDS
        ex.put(KEY_ID,testRun.getOldVersion().getId());
        ex.put(KEY_EXECBY,testRun.getOldVersion().getExecutedBy());
        ex.put(KEY_STARTEDON,testRun.getOldVersion().getStartedOn());
        ex.put(KEY_ASSIGNEE,testRun.getOldVersion().getAssignee());
        ex.put(KEY_FINISHEDON,testRun.getOldVersion().getFinishedOn());
        // UPDATABLE FIELDS
        ex.put(KEY_COMMENT, COMMENTS_GENERATOR.generate(testRun.getComment()).get("raw"));
        ex.put(KEY_STATUS,testRun.getStatus().name());
        ex.put(KEY_DEFECTS,generateDefects(testRun));
        ex.put(KEY_EVIDENCES,generateEvidences(testRun));
        if(testRun.getSteps()!=null) {
            ex.put(KEY_TESTSTEPS,generateTestSteps(testRun));
        }
        // EXAMPLES IS CUCUMBER


        return ex;
    }

    private JSONArray generateEvidences(TestRun testRun) throws JSONException {
        ArrayList<JSONObject> evidences=new ArrayList<JSONObject>();
        for(Evidence ev: testRun.getEvidences()){
            evidences.add(EVIDENCES_GENERATOR.generate(ev));
        }
        return new JSONArray(evidences);
    }

    private Object generateDefects (TestRun testRun) throws JSONException{
        if(testRun.getVersion()!=0) {
            return generateDefectsUpdates(testRun);
        }
        else {
            return generateDefectsArray(testRun);
        }
    }

    private JSONArray generateDefectsArray(TestRun testRun) throws JSONException {
        ArrayList<JSONObject> defects=new ArrayList<JSONObject>();
        for(Defect def: testRun.getDefects()){
            defects.add(DEFECTS_GENERATOR.generate(def));
        }
        return new JSONArray(defects);
    }


    //TODO: EXTRACT LOGIC AND CLEAN CODE, IMPLEMENT COMMON CLASSES AND INTERFACES
    @SuppressWarnings("PMD.NPathComplexity")
    private JSONObject generateDefectsUpdates(TestRun testRun) throws JSONException {
        ArrayList<String> removes=new ArrayList<String>();
        ArrayList<String> adds=new ArrayList<String>();

        Iterable<Defect> all;

        Optional<Iterable<Defect>> defectsOpt = Optional.ofNullable(testRun.getOldVersion().getDefects());
        if( testRun.getVersion()!=0 && defectsOpt.isPresent()) {
            all= Iterables.concat(testRun.getOldVersion().getDefects(),testRun.getDefects());
        }
        else {
            all=testRun.getDefects();
        }

        if (testRun.getVersion() != 0) {
            ArrayList<Defect> oldDef = new ArrayList<Defect>();
            ArrayList<Defect> newDef = new ArrayList<Defect>();

            if(defectsOpt.isPresent()) {
                Iterables.addAll(oldDef, testRun.getOldVersion().getDefects());
            }
            Iterables.addAll(newDef, testRun.getDefects());
            for (Defect def : all) {
                if (oldDef.contains(def) && !newDef.contains(def)) {
                    removes.add(def.getKey());
                }
                if (!oldDef.contains(def) && newDef.contains(def)) {
                    adds.add(def.getKey());
                }
            }
        }

        JSONObject defects = new JSONObject();
        if(!adds.isEmpty()) {
            defects.put("add", new JSONArray(adds));
        }
        if(!removes.isEmpty()) {
            defects.put("remove", new JSONArray(removes));
        }
        return defects;
    }


    private JSONArray generateTestSteps(TestRun testRun) throws JSONException {
        ArrayList<JSONObject> testStepsArray=new ArrayList<JSONObject>();
        for(TestStep testStep: testRun.getSteps()){
                testStepsArray.add(TEST_STEPS_GENERATOR.generate(testStep));
        }

    return new JSONArray(testStepsArray);}





}
