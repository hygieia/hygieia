package com.capitalone.dashboard.model.pullrequest;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequestRepo implements Serializable {
    private static final long serialVersionUID = 3332497048882405022L;
    String name;

    public PullRequestRepo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
