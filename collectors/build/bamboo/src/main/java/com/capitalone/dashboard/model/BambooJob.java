package com.capitalone.dashboard.model;

/**
 * CollectorItem extension to store the instance, build job and build url.
 */
public class BambooJob extends JobCollectorItem {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
        	return true;
        }
        if (o == null || getClass() != o.getClass()) {
        	return false;
        }

        BambooJob bambooJob = (BambooJob) o;

        return getInstanceUrl().equals(bambooJob.getInstanceUrl()) && getJobName().equals(bambooJob.getJobName());
    }

    @Override
    public int hashCode() {
        int result = getInstanceUrl().hashCode();
        result = 31 * result + getJobName().hashCode();
        return result;
    }
}
