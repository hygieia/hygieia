package com.capitalone.dashboard.model;

public class PipelineCommit extends SCM{

    public PipelineCommit() {
    }

    public PipelineCommit(long timestamp) {
        this.timestamp = timestamp;
    }

    public PipelineCommit(String scmUrl, String scmBranch, String scmRevisionNumber, String scmCommitLog, String scmAuthor, long scmCommitTimestamp, long numberOfChanges, long timestamp) {
        super(scmUrl, scmBranch, scmRevisionNumber, scmCommitLog, scmAuthor, scmCommitTimestamp, numberOfChanges);
        this.timestamp = timestamp;
    }

    public PipelineCommit(SCM scm, long timestamp){
        super(scm.scmUrl, scm.scmBranch, scm.scmRevisionNumber, scm.scmCommitLog, scm.scmAuthor, scm.scmCommitTimestamp, scm.numberOfChanges);
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
