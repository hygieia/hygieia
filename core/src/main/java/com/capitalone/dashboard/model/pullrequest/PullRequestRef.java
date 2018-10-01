package com.capitalone.dashboard.model.pullrequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequestRef implements Serializable {
    private static final long serialVersionUID = -8406211822625859945L;
    private String displayId; // displayName of the incoming pr's branch
    private PullRequestRepo repository;

    public PullRequestRef() {
    }

    public String getDisplayId() {
        return displayId;
    }

    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }

    public PullRequestRepo getRepository() {
        return repository;
    }

    public void setRepository(PullRequestRepo repository) {
        this.repository = repository;
    }
}
