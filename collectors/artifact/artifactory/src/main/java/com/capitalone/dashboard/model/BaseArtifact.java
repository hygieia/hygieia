package com.capitalone.dashboard.model;

public class BaseArtifact {

    ArtifactItem artifactItem;
    BinaryArtifact binaryArtifact;


    public ArtifactItem getArtifactItem() {
        return artifactItem;
    }

    public void setArtifactItem(ArtifactItem artifactItem) {
        this.artifactItem = artifactItem;
    }

    public BinaryArtifact getBinaryArtifact() {
        return binaryArtifact;
    }

    public void setBinaryArtifact(BinaryArtifact binaryArtifact) {
        this.binaryArtifact = binaryArtifact;
    }


}
