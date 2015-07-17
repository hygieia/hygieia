package com.capitalone.dashboard.model;

public class SonarProject extends CollectorItem {
    private static final String INSTANCE_URL = "instanceUrl";
    private static final String PROJECT_NAME = "projectName";
    private static final String PROJECT_ID = "projectId";

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

        SonarProject that = (SonarProject) o;
        return getProjectId().equals(that.getProjectId()) && getInstanceUrl().equals(that.getInstanceUrl());
    }

    @Override
    public int hashCode() {
        int result = getInstanceUrl().hashCode();
        result = 31 * result + getProjectId().hashCode();
        return result;
    }
}
