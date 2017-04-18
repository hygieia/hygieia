package com.capitalone.dashboard.model;

public class NexusIQApplication extends CollectorItem {
    private static final String INSTANCE_URL = "instanceUrl";
    private static final String APPLICATION_NAME = "applicationName";
    private static final String APPLICATION_ID = "applicationId";
    private static final String APPLICATION_PUBLIC_ID = "publicId";

    public String getInstanceUrl() {
        return (String) getOptions().get(INSTANCE_URL);
    }

    public void setInstanceUrl(String instanceUrl) {
        getOptions().put(INSTANCE_URL, instanceUrl);
    }

    public String getApplicationId() {
        return (String) getOptions().get(APPLICATION_ID);
    }

    public void setApplicationId(String id) {
        getOptions().put(APPLICATION_ID, id);
    }

    public String getApplicationName() {
        return (String) getOptions().get(APPLICATION_NAME);
    }

    public void setApplicationName(String name) {
        getOptions().put(APPLICATION_NAME, name);
    }

    public String getPublicId() {
        return (String) getOptions().get(APPLICATION_PUBLIC_ID);
    }

    public void setPublicId(String id) {
        getOptions().put(APPLICATION_PUBLIC_ID, id);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NexusIQApplication that = (NexusIQApplication) o;
        return getApplicationId().equals(that.getApplicationId()) && getInstanceUrl().equals(that.getInstanceUrl());
    }

    @Override
    public int hashCode() {
        int result = getInstanceUrl().hashCode();
        result = 31 * result + getApplicationId().hashCode();
        return result;
    }
}
