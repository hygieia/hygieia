package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.CodeAction;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.status.CodeReviewAuditStatus;

import java.util.List;

public class CodeReviewAuditResponse extends AuditReviewResponse<CodeReviewAuditStatus> {
    private GitRequest pullRequest;
    private List<Commit> commits;
    private List<CodeAction> codeActions;
    private List<Commit> directCommits;
    private String scmUrl;
    private String scmBranch;

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

    public List<CodeAction> getCodeActions() {
        return codeActions;
    }

    public void setCodeActions(List<CodeAction> codeActions) {
        this.codeActions = codeActions;
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
}