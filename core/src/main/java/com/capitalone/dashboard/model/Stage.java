package com.capitalone.dashboard.model;

import java.util.HashSet;
import java.util.Set;


public class Stage {
    public Stage(){

    }

    public Stage(Set<PipelineCommit> commits){
        this.commits = commits;
    }
    /** A collection that includes all commits ever to come into the stage */
    private Set<PipelineCommit> commits = new HashSet<>();

    public Set<PipelineCommit> getCommits() {
        return commits;
    }

    public void setCommits(Set<PipelineCommit> commits) {
        this.commits = commits;
    }

    public void addCommit(PipelineCommit commit){
        this.commits.add(commit);
    }

}
