package com.capitalone.dashboard.model;

/**
 * Class representing any stage of a {@link Pipeline}
 */
public class EnvironmentStage extends Stage{
    /** {@link BinaryArtifact} The last artifact to be processed */
    private BinaryArtifact lastArtifact;

    public BinaryArtifact getLastArtifact() {
        return lastArtifact;
    }

    public void setLastArtifact(BinaryArtifact lastArtifact) {
        this.lastArtifact = lastArtifact;
    }


}
