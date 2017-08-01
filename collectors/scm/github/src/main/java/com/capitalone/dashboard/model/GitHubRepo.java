package com.capitalone.dashboard.model;

import java.util.Date;

/**
 * CollectorItem extension to store the github repo url and branch.
 */
public class GitHubRepo extends CollectorItem {
    public static final String REPO_URL = "url"; // http://github.company.com/jack/somejavacode
    public static final String BRANCH = "branch"; // master, development etc.
    public static final String USER_ID = "userID";
    public static final String PASSWORD = "password";
    public static final String LAST_UPDATE_TIME = "lastUpdate";
    private int lastPrPage;


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
    
    public String getBranch() {
        return (String) getOptions().get(BRANCH);
    }

    public void setBranch(String branch) {
        getOptions().put(BRANCH, branch);
    }

    public int getLastPrPage() {
        return lastPrPage;
    }

    public void setLastPrPage(int lastPrPage) {
        this.lastPrPage = lastPrPage;
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

        return getRepoUrl().equals(gitHubRepo.getRepoUrl()) & getBranch().equals(gitHubRepo.getBranch());
    }

    @Override
    public int hashCode() {
        return getRepoUrl().hashCode();
    }

}
