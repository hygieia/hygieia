package com.capitalone.dashboard.model.pullrequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequestProperties implements Serializable {

    private static final long serialVersionUID = -6701721371836651410L;

    private MergeResult mergeResult;
    private int commentCount;

    public PullRequestProperties() {
    }

    public MergeResult getMergeResult() {
        return mergeResult;
    }

    public void setMergeResult(MergeResult mergeResult) {
        this.mergeResult = mergeResult;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
