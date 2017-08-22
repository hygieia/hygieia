package com.capitalone.dashboard.model;

import java.util.Date;



/**
 * CollectorItem extension to store the git repo url and branch.
 */
public class GitRepo extends CollectorItem {
    public static final String REPO_URL = "url"; // http://git.company.com/jack/somejavacode
    public static final String BRANCH = "branch"; // master, development etc.
    public static final String USER_ID = "userID";
    public static final String PASSWORD = "password";
    public static final String LAST_UPDATE_TIME = "lastUpdate";
    public static final String LAST_UPDATE_COMMIT = "lastUpdateCommit"; // Bitbucket Server api uses last update commit instead of time
    
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
    
    public String getLastUpdateCommit() {
    	return (String) getOptions().get(LAST_UPDATE_COMMIT);
    }
    
    public void setLastUpdateCommit(String sha) {
    	getOptions().put(LAST_UPDATE_COMMIT, sha);
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GitRepo other = (GitRepo) obj;
		if (getBranch() == null) {
			if (other.getBranch() != null)
				return false;
		} else if (!getBranch().equals(other.getBranch()))
			return false;
		if (getRepoUrl() == null) {
			if (other.getRepoUrl() != null)
				return false;
		} else if (!getRepoUrl().equals(other.getRepoUrl()))
			return false;
		return true;
	}

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getBranch() == null) ? 0 : getBranch().hashCode());
		result = prime * result + ((getRepoUrl() == null) ? 0 : getRepoUrl().hashCode());
		return result;
	}

}
