package com.capitalone.dashboard.model;

import java.util.HashMap;
import java.util.Map;

public class PipelineCommit {
    private SCM commit;
    Map<String, Long> processedTimestamps = new HashMap<>();

    public PipelineCommit(){
    }

    public PipelineCommit(SCM commit) {
        this.commit = commit;
    }

    public SCM getCommit() {
        return commit;
    }

    public void setCommit(SCM commit) {
        this.commit = commit;
    }

    public Map<String, Long> getProcessedTimestamps() {
        return processedTimestamps;
    }

    public void setProcessedTimestamps(Map<String, Long> processedTimestamps) {
        this.processedTimestamps = processedTimestamps;
    }

    public void addNewPipelineProcessedTimestamp(String pipelineStageType, Long timestamp){
        getProcessedTimestamps().put(pipelineStageType, timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof PipelineCommit){
            PipelineCommit toCompareTo = (PipelineCommit) o;
            return this.commit.scmRevisionNumber.equals(toCompareTo.commit.scmRevisionNumber);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return commit.hashCode();
    }
}
