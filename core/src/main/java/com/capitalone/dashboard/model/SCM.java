package com.capitalone.dashboard.model;

import java.util.List;

/**
 * Base class to represent the details of a change in a source code management
 * system.
 */
public class SCM {
    protected String scmUrl;
    protected String scmBranch; // For SCM that don't have branch in the url
	protected String scmRevisionNumber;
    protected String scmCommitLog;
    protected String scmAuthor;
    protected List<String> scmParentRevisionNumbers;
    protected long scmCommitTimestamp;
    protected long numberOfChanges;
    protected CommitType type;

    public SCM(){

    }
    
    public SCM(SCM scm) {
        this.scmUrl = scm.scmUrl;
        this.scmBranch = scm.scmBranch;
        this.scmRevisionNumber = scm.scmRevisionNumber;
        this.scmCommitLog = scm.scmCommitLog;
        this.scmAuthor = scm.scmAuthor;
        this.scmParentRevisionNumbers = scm.scmParentRevisionNumbers;
        this.scmCommitTimestamp = scm.scmCommitTimestamp;
        this.numberOfChanges = scm.numberOfChanges;
        this.type = scm.type;
    }

    public SCM(String scmUrl, String scmBranch, String scmRevisionNumber, String scmCommitLog, String scmAuthor, List<String> scmParentRevisionNumbers, long scmCommitTimestamp, long numberOfChanges, CommitType type) {
        this.scmUrl = scmUrl;
        this.scmBranch = scmBranch;
        this.scmRevisionNumber = scmRevisionNumber;
        this.scmCommitLog = scmCommitLog;
        this.scmAuthor = scmAuthor;
        this.scmParentRevisionNumbers = scmParentRevisionNumbers;
        this.scmCommitTimestamp = scmCommitTimestamp;
        this.numberOfChanges = numberOfChanges;
        this.type = type;
    }

    public String getScmUrl() {
        return scmUrl;
    }

    public void setScmUrl(String scmUrl) {
        this.scmUrl = scmUrl;
    }

    public String getScmBranch() {
		return scmBranch;
	}

	public void setScmBranch(String scmBranch) {
		this.scmBranch = scmBranch;
	}

    public String getScmRevisionNumber() {
        return scmRevisionNumber;
    }

    public void setScmRevisionNumber(String scmRevisionNumber) {
        this.scmRevisionNumber = scmRevisionNumber;
    }

    public String getScmCommitLog() {
        return scmCommitLog;
    }

    public void setScmCommitLog(String scmCommitLog) {
        this.scmCommitLog = scmCommitLog;
    }

    public String getScmAuthor() {
        return scmAuthor;
    }

    public void setScmAuthor(String scmAuthor) {
        this.scmAuthor = scmAuthor;
    }
    
    // can return null
    public List<String> getScmParentRevisionNumbers() {
    	return scmParentRevisionNumbers;
    }
    
    public void setScmParentRevisionNumbers(List<String> scmParentRevisionNumbers) {
    	this.scmParentRevisionNumbers = scmParentRevisionNumbers;
    }

    public long getScmCommitTimestamp() {
        return scmCommitTimestamp;
    }

    public void setScmCommitTimestamp(long scmCommitTimestamp) {
        this.scmCommitTimestamp = scmCommitTimestamp;
    }

    public long getNumberOfChanges() {
        return numberOfChanges;
    }

    public void setNumberOfChanges(long numberOfChanges) {
        this.numberOfChanges = numberOfChanges;
    }

    public CommitType getType() {
        return type;
    }

    public void setType(CommitType type) {
        this.type = type;
    }
}
