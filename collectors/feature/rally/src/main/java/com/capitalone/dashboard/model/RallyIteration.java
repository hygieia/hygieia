package com.capitalone.dashboard.model;

public class RallyIteration extends CollectorItem {
    private static final String INSTANCE_URL = "instanceUrl";
    private static final String ITERATION_NAME = "iterationName";
    private static final String ITERATION_ID = "iterationId";

    public String getInstanceUrl() {
        return (String) getOptions().get(INSTANCE_URL);
    }

    public void setInstanceUrl(String instanceUrl) {
        getOptions().put(INSTANCE_URL, instanceUrl);
    }

    public String getIterationId() {
        return (String) getOptions().get(ITERATION_ID);
    }

    public void setIterationId(String id) {
        getOptions().put(ITERATION_ID, id);
    }

    public String getIterationName() {
        return (String) getOptions().get(ITERATION_NAME);
    }

    public void setIterationName(String name) {
        getOptions().put(ITERATION_NAME, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RallyIteration that = (RallyIteration) o;
        return getIterationId().equals(that.getIterationId()) && getInstanceUrl().equals(that.getInstanceUrl());
    }

    @Override
    public int hashCode() {
        int result = getInstanceUrl().hashCode();
        result = 31 * result + getIterationId().hashCode();
        return result;
    }
}
