package com.capitalone.dashboard.util;

import com.capitalone.dashboard.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by jkc on 2/8/16.
 */
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
