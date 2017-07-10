package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.AuditStatus;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitRequest;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class PeerReviewResponse {
    private GitRequest pullRequest;
    private List<Commit> commits;
    private List<Commit> mergeCommits;
    private List<Commit> directCommits;
    private Set<AuditStatus> auditStatuses = EnumSet.noneOf(AuditStatus.class);

    public GitRequest getPullRequest() {
        return pullRequest;
    }

    public void setPullRequest(GitRequest pullRequest) {
        this.pullRequest = pullRequest;
    }

    public List<Commit> getCommits() {
        return commits;
    }

    public void setCommits(List<Commit> commits) {
        this.commits = commits;
    }

    public List<Commit> getMergeCommits() {
        return mergeCommits;
    }

    public void setMergeCommits(List<Commit> mergeCommits) {
        this.mergeCommits = mergeCommits;
    }

    public List<Commit> getDirectCommits() {
        return directCommits;
    }

    public void setDirectCommits(List<Commit> directCommits) {
        this.directCommits = directCommits;
    }

    public void addAuditStatus(AuditStatus status) {
        auditStatuses.add(status);
    }

    public Set<AuditStatus> getAuditStatuses() {
        return auditStatuses;
    }
}