package com.capitalone.dashboard.model;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by benathmane on 20/06/16.
 */

/**
 * CollectorItem extension to store the gitlab repo url
 */
public class GitlabGitRepo extends  CollectorItem {
    private static final String REPO_URL = "url";
    private static final String BRANCH = "branch";
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

    public String getBranch() {
        return (String) getOptions().get(BRANCH);
    }

    public void setBranch(String branch) {
        getOptions().put(BRANCH, branch);
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
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		GitlabGitRepo rhs = (GitlabGitRepo) obj;
		return new EqualsBuilder()
				.append(getRepoUrl(), rhs.getRepoUrl())
				.append(getBranch(), rhs.getBranch())
				.append(getUserId(), rhs.getUserId())
				.append(getPassword(), rhs.getPassword())
				.isEquals();
	}

    @Override
    public int hashCode() {
        return new HashCodeBuilder(11, 17)
        		.append(getRepoUrl())
        		.append(getBranch())
        		.append(getUserId())
        		.append(getPassword())
        		.toHashCode();
    }

}
