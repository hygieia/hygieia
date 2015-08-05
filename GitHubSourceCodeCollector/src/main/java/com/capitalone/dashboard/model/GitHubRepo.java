package com.capitalone.dashboard.model;

import org.joda.time.DateTime;

/**
 * CollectorItem extension to store the github repo url and branch.
 */
public class GitHubRepo extends CollectorItem {
    private static final String REPO_URL = "repoUrl"; // http://github.company.com/jack/somejavacode
    private static final String BRANCH = "branch"; // master, development etc.
    private static final String LAST_UPDATE_TIME = "lastUpdate";

    public String getRepoUrl() {
        return (String) getOptions().get(REPO_URL);
    }

    public void setRepoUrl(String instanceUrl) {
        getOptions().put(REPO_URL, instanceUrl);
    }
    
    public String getBranch() {
        return (String) getOptions().get(BRANCH);
    }

    public void setBranch(String branch) {
        getOptions().put(BRANCH, branch);
    }
    
    public DateTime getLastUpdateTime() {
        Object latest = getOptions().get(LAST_UPDATE_TIME);
        return (DateTime) latest;
    }

    public void setLastUpdateTime(DateTime date) {
        getOptions().put(LAST_UPDATE_TIME, date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
        	return true;
        }
        if (o == null || getClass() != o.getClass()) {
        	return false;
        }

        GitHubRepo gitHubRepo = (GitHubRepo) o;

        return getRepoUrl().equals(gitHubRepo.getRepoUrl()) & getBranch().equals(gitHubRepo.getBranch());
    }

    @Override
    public int hashCode() {
        return getRepoUrl().hashCode();
    }

}
