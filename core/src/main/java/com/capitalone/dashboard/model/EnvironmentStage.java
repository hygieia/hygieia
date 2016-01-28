package com.capitalone.dashboard.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Class representing any stage of a {@link Pipeline}
 */
public class EnvironmentStage {
    /** A collection that includes all commits ever to come into the stage */
    private Set<PipelineCommit> commits = new HashSet<>();

    /** {@link BinaryArtifact} The last artifact to be processed */
    private BinaryArtifact lastArtifact;

    public Set<PipelineCommit> getCommits() {
        return commits;
    }

    public void setCommits(Set<PipelineCommit> commits) {
        this.commits = commits;
    }

    public BinaryArtifact getLastArtifact() {
        return lastArtifact;
    }

    public void setLastArtifact(BinaryArtifact lastArtifact) {
        this.lastArtifact = lastArtifact;
    }


}
