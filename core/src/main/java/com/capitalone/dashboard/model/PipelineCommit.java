package com.capitalone.dashboard.model;

public class PipelineCommit extends SCM{

    public PipelineCommit() {
    }

    public PipelineCommit(long timestamp) {
        this.timestamp = timestamp;
    }

    public PipelineCommit(SCM scm, long timestamp){
        super(scm);
        this.timestamp = timestamp;
    }
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof PipelineCommit){
            PipelineCommit toCompareTo = (PipelineCommit) o;
            return this.scmRevisionNumber.equals(toCompareTo.scmRevisionNumber);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (int) (timestamp ^ (timestamp >>> 32));
    }
}
