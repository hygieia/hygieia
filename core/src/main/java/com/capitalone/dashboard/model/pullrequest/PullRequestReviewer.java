package com.capitalone.dashboard.model.pullrequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequestReviewer implements Serializable {
    private static final long serialVersionUID = -6366771839796813159L;
    private Long reviewerId;
    private String reviewerDisplayedName;
    private boolean approved;
    private String status;

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getReviewerDisplayedName() {
        return reviewerDisplayedName;
    }

    public void setReviewerDisplayedName(String reviewerDisplayedName) {
        this.reviewerDisplayedName = reviewerDisplayedName;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
