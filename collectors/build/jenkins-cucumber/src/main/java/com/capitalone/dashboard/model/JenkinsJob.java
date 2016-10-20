package com.capitalone.dashboard.model;

/**
 * CollectorItem extension to store the subversion url.
 */
public class JenkinsJob extends JobCollectorItem {

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
