package com.capitalone.dashboard.model;

import java.util.Date;
import java.util.List;

/**
 * CollectorItem extension to store the github repo url and branch.
 */
public class GitHubRepo extends CollectorItem {
    private static final String REPO_URL = "url"; // http://github.company.com/jack/somejavacode
    private static final String DEFAULT_BRANCH = "branch"; // master, development etc.
    private static final String BRANCHES = "branches"; //list of all branches for this repository
    private static final String USER_ID = "userID";
    private static final String PASSWORD = "password";
    private static final String LAST_UPDATE_TIME = "lastUpdate";

    public String getUserId() {
        return (String) getOptions().get(USER_ID);
    }

    public void setUserId(String userId) {
        getOptions().put(USER_ID, userId);
    }
    
    public String getPassword() {
        return (String) getOptions().get(PASSWORD);
    }

    public void setPassword(String password) {
        getOptions().put(PASSWORD, password);
    }

    public String getRepoUrl() {
        return (String) getOptions().get(REPO_URL);
    }

    public void setRepoUrl(String instanceUrl) {
        getOptions().put(REPO_URL, instanceUrl);
    }
    
    public String getDefaultBranch() {
        return (String) getOptions().get(DEFAULT_BRANCH);
    }

    public void setDefaultBranch(String branch) {
        getOptions().put(DEFAULT_BRANCH, branch);
    }

    public List<String> getBranches() {
        return (List<String>)getOptions().get(BRANCHES);
    }

    public void setBranches(List<String> branches) {
        getOptions().put(BRANCHES, branches);
    }

    public Date getLastUpdateTime() {
        Object latest = getOptions().get(LAST_UPDATE_TIME);
        return (Date) latest;
    }

    public void setLastUpdateTime(Date date) {
        getOptions().put(LAST_UPDATE_TIME, date);
    }

    public void removeLastUpdateDate() {
        getOptions().remove(LAST_UPDATE_TIME);
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

        return getRepoUrl().equals(gitHubRepo.getRepoUrl()) & getBranches().equals(gitHubRepo.getBranches());
    }

    @Override
    public int hashCode() {
        return getRepoUrl().hashCode();
    }

}
