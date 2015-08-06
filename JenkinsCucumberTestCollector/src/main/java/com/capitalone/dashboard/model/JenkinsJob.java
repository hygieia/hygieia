package com.capitalone.dashboard.model;

/**
 * CollectorItem extension to store the subversion url.
 */
public class JenkinsJob extends CollectorItem {

    private static final String INSTANCE_URL = "instanceUrl";
    private static final String JOB_NAME = "jobName";
    private static final String JOB_URL = "jobUrl";

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JenkinsJob hudsonJob = (JenkinsJob) o;

        return getInstanceUrl().equals(hudsonJob.getInstanceUrl()) && getJobName().equals(hudsonJob.getJobName());
    }

    @Override
    public int hashCode() {
        int result = getInstanceUrl().hashCode();
        result = 31 * result + getJobName().hashCode();
        return result;
    }
}
