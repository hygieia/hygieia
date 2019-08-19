package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        BaseArtifact that = (BaseArtifact) obj;
        return Objects.equals(getArtifactItem().getArtifactName(),that.getArtifactItem().getArtifactName()) &&
                Objects.equals(getArtifactItem().getPath(),that.getArtifactItem().getPath()) &&
                Objects.equals(getArtifactItem().getRepoName(), that.getArtifactItem().getRepoName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getArtifactItem().getArtifactName(), getArtifactItem().getPath(),getArtifactItem().getRepoName());
    }

}
