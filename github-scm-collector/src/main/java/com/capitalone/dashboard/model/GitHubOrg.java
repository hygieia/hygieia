package com.capitalone.dashboard.model;

import java.util.Date;

/**
 * Created by ltz038 on 4/25/16.
 */
public class GitHubOrg extends CollectorItem {
    private static final String REPO_URL = "orgUrl"; // http://github.company.com/jack/somejavacode
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


    public String getOrgUrl() {
        return (String) getOptions().get(REPO_URL);
    }

    public void setOrgUrl(String instanceUrl) {
        getOptions().put(REPO_URL, instanceUrl);
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

        GitHubOrg gitHubOrg = (GitHubOrg) o;
        return getOrgUrl().equals(gitHubOrg.getOrgUrl());
    }

    @Override
    public int hashCode() {
        return getOrgUrl().hashCode();
    }

}
