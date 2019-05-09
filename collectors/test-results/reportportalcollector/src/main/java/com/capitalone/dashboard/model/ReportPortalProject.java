package com.capitalone.dashboard.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class ReportPortalProject extends CollectorItem {
    protected static final String INSTANCE_URL = "instanceUrl";
    protected static final String PROJECT_NAME = "projectName";
    protected static final String PROJECT_ID = "projectId";
	//private String launchNumber;
	@Field
    private String launchId;
	 

    public String getInstanceUrl() {
        return (String) getOptions().get(INSTANCE_URL);
    }

    public void setInstanceUrl(String instanceUrl) {
        getOptions().put(INSTANCE_URL, instanceUrl);
    }

    public String getProjectId() {
        return (String) getOptions().get(PROJECT_ID);
    }

    public void setProjectId(String id) {
        getOptions().put(PROJECT_ID, id);
    }

    public String getProjectName() {
        return (String) getOptions().get(PROJECT_NAME);
    }

    public void setProjectName(String name) {
        getOptions().put(PROJECT_NAME, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportPortalProject that = (ReportPortalProject) o;
        return getProjectId().equals(that.getProjectId()) && getInstanceUrl().equals(that.getInstanceUrl());
    }

    @Override
    public int hashCode() {
        int result = getInstanceUrl().hashCode();
        result = 31 * result + getProjectId().hashCode();
        return result;
    }

	public void setLaunchNumber(String number) {
		// TODO Auto-generated method stub
		this.getOptions().put("launchNumber", number);
		//this.launchNumber=number;
		
	}
	public String getLaunchNumber() {
		 return (String) getOptions().get("launchNumber");
	}

	public String getLaunchId() {
		// TODO Auto-generated method stub
		return this.launchId;
	}
	public void setLaunchId(String launchId) {
		this.launchId=launchId;
	}
	
	

	
}
