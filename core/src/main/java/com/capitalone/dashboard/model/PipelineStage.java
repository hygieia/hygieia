package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing any stage of a {@link Pipeline}
 */
public class PipelineStage {
    private String name;
    private List<PipelineCommit> commits = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PipelineCommit> getCommits() {
        return commits;
    }

    public void setCommits(List<PipelineCommit> commits) {
        this.commits = commits;
    }
}
