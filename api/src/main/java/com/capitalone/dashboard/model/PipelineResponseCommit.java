package com.capitalone.dashboard.model;

import java.util.HashMap;
import java.util.Map;

public class PipelineResponseCommit extends SCM {

    public PipelineResponseCommit(SCM scm) {
    	super(scm);
    }

    Map<String, Long> processedTimestamps = new HashMap<>();

    public void addNewPipelineProcessedTimestamp(PipelineStage stage, Long timestamp) {
    	addNewPipelineProcessedTimestamp(stage.getName(), timestamp);
    }
    
    public void addNewPipelineProcessedTimestamp(String stageName, Long timestamp) {
        getProcessedTimestamps().put(stageName, timestamp);
    }

    public Map<String, Long> getProcessedTimestamps() {
        return processedTimestamps;
    }

    public void setProcessedTimestamps(Map<String, Long> processedTimestamps) {
        this.processedTimestamps = processedTimestamps;
    }
}
