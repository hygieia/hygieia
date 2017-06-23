package com.capitalone.dashboard.audit.request;

import javax.validation.constraints.NotNull;

public class PeerReviewRequest {
    @NotNull
    private long beginDate;
    @NotNull
    private long endDate;
    @NotNull
    private String repo;
    @NotNull
    private String branch;

    public long getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(long beginDate) {
        this.beginDate = beginDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

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
}
