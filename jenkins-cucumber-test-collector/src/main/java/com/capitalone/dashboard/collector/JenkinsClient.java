package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.JenkinsJob;
import com.capitalone.dashboard.model.TestResult;

import java.util.Map;
import java.util.Set;

/**
 * Client for fetching job and build information from Hudson
 */
public interface JenkinsClient {

    /**
     * Finds all of the configured jobs for a given instance and returns the set of
     * builds for each job. At a minimum, the number and url of each Build will be
     * populated.
     *
     * @param instanceUrl the URL for the Hudson instance
     * @return a summary of every build for each job on the instance
     */
    Map<JenkinsJob, Set<Build>> getInstanceJobs(String instanceUrl);

    /**
     *
     * @param buildUrl the build url
     * @return boolean
     */
    boolean buildHasCucumberResults(String buildUrl);

    /**
     * This method is responsible for going to the Jenkins systems, accessing a specific build, getting the
     * (cucumber) results.json file, and parsing it to a TestResult
     *
     * @param buildUrl url of the Jenkins build
     * @return a TestResult
     */
    TestResult getCucumberTestResult(String buildUrl);

    Build getLastSuccessfulBuild (String buildUrl);
}
