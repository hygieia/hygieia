package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;

public class BaseArtifact {

    ArtifactItem artifactItem;

    List<BinaryArtifact> binaryArtifacts = new ArrayList<>();


    public ArtifactItem getArtifactItem() {
        return artifactItem;
    }

    public void setArtifactItem(ArtifactItem artifactItem) {
        this.artifactItem = artifactItem;
    }

    public List<BinaryArtifact> getBinaryArtifacts() {
        return binaryArtifacts;
    }

    public void setBinaryArtifacts(List<BinaryArtifact> binaryArtifacts) {
        this.binaryArtifacts = binaryArtifacts;
    }

    public  void addBinaryArtifact(BinaryArtifact ba){
        getBinaryArtifacts().add(ba);
    }

}
