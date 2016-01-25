package com.capitalone.dashboard.model;

import java.util.HashMap;
import java.util.Map;

public class PipelineCommit {
    Commit commit;
    Map<PipelineStageType, Long> processedTimestamps = new HashMap<>();

    public PipelineCommit() {
    }

    public PipelineCommit(Commit commit, Map<PipelineStageType, Long> processedTimestamps) {
        this.commit = commit;
        this.processedTimestamps = processedTimestamps;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    public Map<PipelineStageType, Long> getProcessedTimestamps() {
        return processedTimestamps;
    }

    public void setProcessedTimestamps(Map<PipelineStageType, Long> processedTimestamps) {
        this.processedTimestamps = processedTimestamps;
    }

    public void addNewPipelineProcessedTimestamp(PipelineStageType pipelineStageType, Long timestamp){
        getProcessedTimestamps().put(pipelineStageType, timestamp);
    }
}
