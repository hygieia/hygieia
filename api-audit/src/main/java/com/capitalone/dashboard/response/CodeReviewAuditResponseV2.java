package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitRequest;

import java.util.ArrayList;
import java.util.List;

public class CodeReviewAuditResponseV2 extends AuditReviewResponse {

    public static class PullRequestAudit extends AuditReviewResponse{
        GitRequest pullRequest;

        public GitRequest getPullRequest() {
            return pullRequest;
        }

        public void setPullRequest(GitRequest pullRequest) {
            this.pullRequest = pullRequest;
        }
    }
    protected String scmUrl;
    protected String scmBranch;
    private List<Commit> directCommits = new ArrayList<>();
    private List<PullRequestAudit> pullRequests = new ArrayList<>();


    public List<PullRequestAudit> getPullRequests() {
        return pullRequests;
    }

    public void setPullRequests(List<PullRequestAudit> pullRequests) {
        this.pullRequests = pullRequests;
    }

    public List<Commit> getDirectCommits() {
        return directCommits;
    }

    public void setDirectCommits(List<Commit> directCommits) {
        this.directCommits = directCommits;
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

    public void addPullRequest (PullRequestAudit pull) {
        pullRequests.add(pull);
    }

    public void addDirectCommit (Commit commit) {
        directCommits.add(commit);
    }

}