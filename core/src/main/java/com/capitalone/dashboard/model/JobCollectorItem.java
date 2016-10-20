package com.capitalone.dashboard.model;

public class JobCollectorItem extends CollectorItem {
    protected static final String INSTANCE_URL = "instanceUrl";
    protected static final String JOB_NAME = "jobName";
    protected static final String JOB_URL = "jobUrl";

    public String getInstanceUrl() {
        return (String) getOptions().get(INSTANCE_URL);
    }

    public void setInstanceUrl(String instanceUrl) {
        getOptions().put(INSTANCE_URL, instanceUrl);
    }

    public String getJobName() {
        return (String) getOptions().get(JOB_NAME);
    }

    public void setJobName(String jobName) {
        getOptions().put(JOB_NAME, jobName);
    }

    public String getJobUrl() {
        return (String) getOptions().get(JOB_URL);
    }

    public void setJobUrl(String jobUrl) {
        getOptions().put(JOB_URL, jobUrl);
    }
}
