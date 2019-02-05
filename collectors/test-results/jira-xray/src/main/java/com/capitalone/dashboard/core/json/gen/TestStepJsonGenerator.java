package com.capitalone.dashboard.core.json.gen;

import com.atlassian.jira.rest.client.internal.json.gen.JsonGenerator;
import com.capitalone.dashboard.api.domain.Defect;
import com.capitalone.dashboard.api.domain.Evidence;
import com.capitalone.dashboard.api.domain.TestStep;
import com.google.common.collect.Iterables;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;

/**
 * This class will generate a JSON Object for a Test Step
 */
public class TestStepJsonGenerator  implements JsonGenerator<TestStep> {

    private final static RendereableItemJsonGenerator RENDEREABLE_GENERATOR =new RendereableItemJsonGenerator();
    private final static DefectJSONGenerator DEFECTS_GENERATOR =new DefectJSONGenerator();

    public final static String KEY_ID="id";
    public final static String KEY_INDEX="index";
    public final static String KEY_STEP="step";
    public final static String KEY_DATA="data";
    public final static String KEY_RESULT="result";
    // UPDATABLE FIELDS
    public final static String KEY_ATTACHMENTS="attachments";
    public final static String KEY_EVIDENCES="evidences";
    public final static String KEY_DEFECTS="defects";
    public final static String KEY_STATUS="status";
    public final static String KEY_COMMENT="comment";

    public JSONObject generate(TestStep testStep) throws JSONException {
        JSONObject ex=new JSONObject();
        ex.put(KEY_ID,testStep.getId());
        ex.put(KEY_INDEX,testStep.getIndex());
        ex.put(KEY_STEP, RENDEREABLE_GENERATOR.generate(testStep.getStep()));
        ex.put(KEY_DATA, RENDEREABLE_GENERATOR.generate(testStep.getData()));
        ex.put(KEY_RESULT, RENDEREABLE_GENERATOR.generate(testStep.getResult()));
        ex.put(KEY_STATUS,testStep.getStatus().name());
       // ex.put(KEY_COMMENT, RENDEREABLE_GENERATOR.generate(testStep.getComment()));

        if(testStep.getAttachments()!=null) {
            ex.put(KEY_ATTACHMENTS,generateAttachments(testStep));
        }
        if(testStep.getEvidences()!=null) {
            ex.put(KEY_EVIDENCES,generateEvidences(testStep));
        }
        if(testStep.getDefects()!=null) {
            ex.put(KEY_DEFECTS,generateDefects(testStep));
        }

    return ex;
    }


    protected JSONObject generateAttachments(TestStep testStep) throws JSONException {
        ArrayList<Evidence> removes=new ArrayList<Evidence>();
        ArrayList<Evidence> adds=new ArrayList<Evidence>();
        Iterable<Evidence> all;

        if( testStep.getVersion()!=0) {
            all= Iterables.concat(testStep.getOldVersion().getAttachments(),testStep.getAttachments());
        }
        else {
            all=testStep.getAttachments();
        }

        if(testStep.getVersion()!=0){
            ArrayList<Evidence> oldEv=new ArrayList<Evidence>();
            ArrayList<Evidence> newEv=new ArrayList<Evidence>();
            Iterables.addAll(oldEv,testStep.getOldVersion().getAttachments());
            Iterables.addAll(newEv,testStep.getAttachments());
            for(Evidence ev: all){
                if(!oldEv.contains(ev) && !newEv.contains(ev)){
                    removes.add(ev);
                }
                if(!oldEv.contains(ev) && newEv.contains(ev)){
                    adds.add(ev);
                }
            }
        }

        JSONObject attachments=new JSONObject();
        if(!adds.isEmpty()) {
            attachments.put("add",new JSONArray(adds));
        }
        if(!removes.isEmpty()) {
            attachments.put("remove",new JSONArray(removes));
        }

        return attachments;
    }

    protected JSONObject generateEvidences(TestStep testStep) throws JSONException {
        ArrayList<Evidence> removes=new ArrayList<Evidence>();
        ArrayList<Evidence> adds=new ArrayList<Evidence>();

        Iterable<Evidence> all;

        if( testStep.getVersion()!=0) {
            all= Iterables.concat(testStep.getOldVersion().getEvidences(),testStep.getEvidences());
        }
        else {
            all=testStep.getEvidences();
        }

        if(testStep.getVersion()!=0){
            ArrayList<Evidence> oldEv=new ArrayList<Evidence>();
            ArrayList<Evidence> newEv=new ArrayList<Evidence>();
            Iterables.addAll(oldEv,testStep.getOldVersion().getAttachments());
            Iterables.addAll(newEv,testStep.getAttachments());
            for(Evidence ev: all){
                if(!oldEv.contains(ev) && !newEv.contains(ev)){
                    removes.add(ev);
                }
                if(!oldEv.contains(ev) && newEv.contains(ev)){
                    adds.add(ev);
                }
            }

        }

        JSONObject evidences=new JSONObject();
        if(!adds.isEmpty()) {
            evidences.put("add",new JSONArray(adds));
        }
        if(!removes.isEmpty()) {
            evidences.put("remove",new JSONArray(removes));
        }
        return evidences;
    }

    protected Object generateDefects (TestStep testStep) throws JSONException{
        if(testStep.getVersion()!=0) {
            return generateDefectsUpdates(testStep);
        }
        else {
            return generateDefectsArray(testStep);
        }
    }

    protected JSONArray generateDefectsArray(TestStep testStep) throws JSONException {
        ArrayList<JSONObject> defects=new ArrayList<JSONObject>();
        for(Defect def: testStep.getDefects()){
            defects.add(DEFECTS_GENERATOR.generate(def));
        }
        return new JSONArray(defects);
    }

    //TODO: EXTRACT LOGIC AND CLEAN CODE, IMPLEMENT COMMON CLASSES AND INTERFACES
    protected JSONObject generateDefectsUpdates(TestStep testStep) throws JSONException {
        ArrayList<Defect> removes=new ArrayList<Defect>();
        ArrayList<Defect> adds=new ArrayList<Defect>();

        Iterable<Defect> all;

        if( testStep.getVersion()!=0) {
            all= Iterables.concat(testStep.getOldVersion().getDefects(),testStep.getDefects());
        }
        else {
            all=testStep.getDefects();
        }

        if (testStep.getVersion() != 0) {
            ArrayList<Defect> oldDef = new ArrayList<Defect>();
            ArrayList<Defect> newDef = new ArrayList<Defect>();
            Iterables.addAll(oldDef, testStep.getOldVersion().getDefects());
            Iterables.addAll(newDef, testStep.getDefects());
            for (Defect def : all) {
                if (!oldDef.contains(def) && !newDef.contains(def)) {
                    removes.add(def);
                }
                if (!oldDef.contains(def) && newDef.contains(def)) {
                    adds.add(def);
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

}
