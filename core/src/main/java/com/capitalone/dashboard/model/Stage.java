package com.capitalone.dashboard.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by jkc on 1/28/16.
 */
public class Stage {
    /** A collection that includes all commits ever to come into the stage */
    private Set<PipelineCommit> commits = new HashSet<>();

    public Set<PipelineCommit> getCommits() {
        return commits;
    }

    public void setCommits(Set<PipelineCommit> commits) {
        this.commits = commits;
    }

}
