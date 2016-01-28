package com.capitalone.dashboard.model;

public class PipelineCommit {
    private SCM commit;
    private long processedTimestamp;

    public PipelineCommit(){
    }

    public PipelineCommit(SCM commit) {
        this.commit = commit;
    }

    public PipelineCommit(SCM commit, long processedTimestamp) {
        this.commit = commit;
        this.processedTimestamp = processedTimestamp;
    }

    public SCM getCommit() {
        return commit;
    }

    public void setCommit(SCM commit) {
        this.commit = commit;
    }

    public long getProcessedTimestamp() {
        return processedTimestamp;
    }

    public void setProcessedTimestamp(long processedTimestamp) {
        this.processedTimestamp = processedTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PipelineCommit that = (PipelineCommit) o;

        return commit.scmRevisionNumber.equals(that.commit.scmRevisionNumber);
    }

    @Override
    public int hashCode() {
        return commit.hashCode();
    }
}
