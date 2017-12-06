package com.capitalone.dashboard.model;

/**
 * CollectorItem extension to store the instance, repo name and repo url.
 */
public class ArtifactoryRepo extends CollectorItem {
    protected static final String INSTANCE_URL = "instanceUrl";
    protected static final String REPO_NAME = "repoName";
    protected static final String REPO_URL = "repoUrl";

    public String getInstanceUrl() {
        return (String) getOptions().get(INSTANCE_URL);
    }

    public void setInstanceUrl(String instanceUrl) {
        getOptions().put(INSTANCE_URL, instanceUrl);
    }

    public String getRepoName() {
        return (String) getOptions().get(REPO_NAME);
    }

    public void setRepoName(String repoName) {
        getOptions().put(REPO_NAME, repoName);
    }

    public String getRepoUrl() {
        return (String) getOptions().get(REPO_URL);
    }

    public void setRepoUrl(String repoUrl) {
        getOptions().put(REPO_URL, repoUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
        	return true;
        }
        if (o == null || getClass() != o.getClass()) {
        	return false;
        }

        ArtifactoryRepo artifactoryRepo = (ArtifactoryRepo) o;

        return getInstanceUrl().equals(artifactoryRepo.getInstanceUrl()) && getRepoName().equals(artifactoryRepo.getRepoName());
    }

    @Override
    public int hashCode() {
        int result = getInstanceUrl().hashCode();
        result = 31 * result + getRepoName().hashCode();
        return result;
    }
}
