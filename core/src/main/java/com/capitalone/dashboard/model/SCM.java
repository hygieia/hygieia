package com.capitalone.dashboard.model;

import lombok.Data;

/**
 * Base class to represent the details of a change in a source code management
 * system.
 */
@Data
public class SCM {
    protected String scmUrl;
    protected String scmBranch; // For SCM that don't have branch in the url
	protected String scmRevisionNumber;
    protected String scmCommitLog;
    protected String scmAuthor;
    protected long scmCommitTimestamp;
    protected long numberOfChanges;

    public SCM(){

    }

    public SCM(String scmUrl, String scmBranch, String scmRevisionNumber, String scmCommitLog, String scmAuthor, long scmCommitTimestamp, long numberOfChanges) {
        this.scmUrl = scmUrl;
        this.scmBranch = scmBranch;
        this.scmRevisionNumber = scmRevisionNumber;
        this.scmCommitLog = scmCommitLog;
        this.scmAuthor = scmAuthor;
        this.scmCommitTimestamp = scmCommitTimestamp;
        this.numberOfChanges = numberOfChanges;
    }

}
