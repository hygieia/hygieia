package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitRequest;

import java.util.List;

public class PeerReviewResponse extends AuditReviewResponse {
    private GitRequest pullRequest;
    private List<Commit> commits;
    private List<Commit> mergeCommits;
    private List<Commit> directCommits;

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
}