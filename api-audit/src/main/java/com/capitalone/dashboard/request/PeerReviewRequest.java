package com.capitalone.dashboard.request;

import javax.validation.constraints.NotNull;

public class PeerReviewRequest extends AuditReviewRequest {
    @NotNull
    private String repo;
    @NotNull
    private String branch;

    private String scmName;

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getScmName() {
        return scmName;
    }

    public void setScmName(String scmName) {
        this.scmName = scmName;
    }
}
