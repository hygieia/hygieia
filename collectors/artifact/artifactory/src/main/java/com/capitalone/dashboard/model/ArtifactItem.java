package com.capitalone.dashboard.model;

import java.util.Objects;

public class ArtifactItem extends CollectorItem {
    public static final String INSTANCE_URL = "instanceUrl";
    public static final String REPO_NAME = "repoName";
    public static final String ARTIFACT_NAME = "artifactName";
    public static final String PATH ="path";

    public String getInstanceUrl() {
        return (String) getOptions().get(INSTANCE_URL);
    }

    public void setInstanceUrl(String instanceUrl) {
        getOptions().put(INSTANCE_URL, instanceUrl);
    }

    public String getArtifactName() {
        return (String) getOptions().get(ARTIFACT_NAME);
    }

    public void setArtifactName(String artifactName) {
        getOptions().put(ARTIFACT_NAME, artifactName);
    }

    public String getRepoName() {
        return (String) getOptions().get(REPO_NAME);
    }

    public void setRepoName(String repoName) {
        getOptions().put(REPO_NAME, repoName);
    }

    public String getPath() {
        return (String) getOptions().get(PATH);
    }

    public void setPath(String path){
        getOptions().put(PATH, path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ArtifactItem artifactItem = (ArtifactItem) o;

        boolean a =  Objects.equals(getInstanceUrl(), artifactItem.getInstanceUrl()) && Objects.equals(getRepoName(), artifactItem.getRepoName()) &&
                Objects.equals(getArtifactName(), artifactItem.getArtifactName()) && Objects.equals(getPath(), artifactItem.getPath());
        return a;
    }

    @Override
    public int hashCode() {
        int result = getInstanceUrl().hashCode();
        result = 31 * result + getRepoName().hashCode();
        return result;
    }
}
