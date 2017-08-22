package com.capitalone.dashboard.request;

import javax.validation.constraints.NotNull;

public class JobReviewRequest extends AuditReviewRequest {
    @NotNull
    private String jobUrl;
    @NotNull
    private String jobName;

    public String getJobUrl() {
        return jobUrl;
    }

    public void setJobUrl(String jobUrl) {
        this.jobUrl = jobUrl;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
