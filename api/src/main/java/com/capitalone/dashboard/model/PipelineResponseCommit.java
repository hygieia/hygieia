package com.capitalone.dashboard.model;

import java.util.HashMap;
import java.util.Map;

public class PipelineResponseCommit extends SCM {

    public PipelineResponseCommit(SCM scm) {
        this.scmCommitLog = scm.scmCommitLog;
        this.scmUrl = scm.scmUrl;
        this.scmBranch = scm.scmBranch;
        this.scmRevisionNumber = scm.scmRevisionNumber;
        this.scmCommitLog = scm.scmCommitLog;
        this.scmAuthor = scm.scmAuthor;
        this.scmCommitTimestamp = scm.scmCommitTimestamp;
        this.numberOfChanges = scm.numberOfChanges;
    }

    Map<String, Long> processedTimestamps = new HashMap<>();

    public void addNewPipelineProcessedTimestamp(String pipelineStageType, Long timestamp) {
        getProcessedTimestamps().put(pipelineStageType, timestamp);
    }

    public Map<String, Long> getProcessedTimestamps() {
        return processedTimestamps;
    }

    public void setProcessedTimestamps(Map<String, Long> processedTimestamps) {
        this.processedTimestamps = processedTimestamps;
    }
}
