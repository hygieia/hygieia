package com.capitalone.dashboard.model;

public class XLDeployApplication extends CollectorItem {
    private static final String INSTANCE_URL = "instanceUrl";
    private static final String APP_NAME = "applicationName";
    private static final String APP_ID = "applicationId";
    private static final String APP_TYPE = "applicationType";

    public String getInstanceUrl() {
        return (String) getOptions().get(INSTANCE_URL);
    }

    public void setInstanceUrl(String instanceUrl) {
        getOptions().put(INSTANCE_URL, instanceUrl);
    }

    public String getApplicationId() {
        return (String) getOptions().get(APP_ID);
    }

    
    public void setApplicationId(String id) {
        getOptions().put(APP_ID, id);
    }

    public String getApplicationName() {
        return (String) getOptions().get(APP_NAME);
    }

    public void setApplicationName(String name) {
        getOptions().put(APP_NAME, name);
    }

    public String getApplicationType() {
    	return (String) getOptions().get(APP_TYPE);
    }
    
    public void setApplicationType(String type) {
    	getOptions().put(APP_TYPE, type);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XLDeployApplication that = (XLDeployApplication) o;
        return getApplicationId().equals(that.getApplicationId()) && getInstanceUrl().equals(that.getInstanceUrl());
    }

    @Override
    public int hashCode() {
        int result = getInstanceUrl().hashCode();
        result = 31 * result + getApplicationId().hashCode();
        return result;
    }
}
