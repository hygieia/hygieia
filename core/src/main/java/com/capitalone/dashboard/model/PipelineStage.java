package com.capitalone.dashboard.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Class representing any stage of a {@link Pipeline}
 */
public class PipelineStage {
    private String name;
    private Set<SCM> commits = new HashSet<>();
    private long lastCommitTimestamp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<SCM> getCommits() {
        return commits;
    }

    public void setCommits(Set<SCM> commits) {
        this.commits = commits;
    }

    public long getLastCommitTimestamp() {
        return lastCommitTimestamp;
    }

    public void setLastCommitTimestamp(long lastCommitTimestamp) {
        this.lastCommitTimestamp = lastCommitTimestamp;
    }
}
