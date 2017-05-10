package com.capitalone.dashboard.model;

/**
 * CollectorItem extension to store the instance, build job and build url.
 */
public class TeamCityJob extends JobCollectorItem {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
        	return true;
        }
        if (o == null || getClass() != o.getClass()) {
        	return false;
        }

        TeamCityJob teamcityJob = (TeamCityJob) o;

        return getInstanceUrl().equals(teamcityJob.getInstanceUrl()) && getJobName().equals(teamcityJob.getJobName());
    }

    @Override
    public int hashCode() {
        int result = getInstanceUrl().hashCode();
        result = 31 * result + getJobName().hashCode();
        return result;
    }
}
