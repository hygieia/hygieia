package com.capitalone.dashboard.util;

import com.capitalone.dashboard.model.PipelineCommit;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class PipelineUtils {

    private PipelineUtils(){

    }

    public static Map<String, PipelineCommit> commitSetToMap(Set<PipelineCommit> set){
        Map<String, PipelineCommit> returnMap = new HashMap<>();
        for(PipelineCommit commit : set){
            returnMap.put(commit.getScmRevisionNumber(), commit);
        }
        return returnMap;
    }

}
