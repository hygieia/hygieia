package com.capitalone.dashboard.core.json.gen;

import com.atlassian.jira.rest.client.internal.json.gen.JsonGenerator;
import com.capitalone.dashboard.api.domain.TestExecution;
import com.google.common.collect.Iterables;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Generate JSON object payload for the server rest implementation.
 */
public class TestExecUpdateJsonGenerator implements JsonGenerator<TestExecution> {
    private final static String KEY_ADD="add";
    private final static String KEY_REMOVE="remove";

    public JSONObject generate(TestExecution testExecution) throws JSONException {
        ArrayList<String> adds=new ArrayList<String>();
        ArrayList<String> removes=new ArrayList<String>();

        if(testExecution.getVersion()!=0){
            Collection<TestExecution.Test> allTests=new ArrayList<TestExecution.Test>();

            if(testExecution.getTests()!=null){
                if(testExecution.getOldVersion().getTests()!=null) {
                    Iterables.addAll(allTests, Iterables.concat(testExecution.getTests(),testExecution.getOldVersion().getTests()));
                }
                else {
                    Iterables.addAll(allTests,testExecution.getTests());
                }
            }

            for(TestExecution.Test t: allTests){
                if(testExecution.getOldVersion().getTests()!=null){
                if(Iterables.contains(testExecution.getOldVersion().getTests(),t) && !Iterables.contains(testExecution.getTests(),t))
                {
                    removes.add(t.getKey());

                }
                if(!Iterables.contains(testExecution.getOldVersion().getTests(),t) && Iterables.contains(testExecution.getOldVersion().getTests(),t))
                {
                    adds.add(t.getKey());
                }
                }else{
                    adds.add(t.getKey());
                }
            }
        }
        JSONObject ex=new JSONObject();
        if(!adds.isEmpty()){
            ex.put(KEY_ADD,new JSONArray(adds));
        }
        if(!removes.isEmpty()){
            ex.put(KEY_REMOVE,new JSONArray(removes));
        }
        return ex;
    }
}
