package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.HudsonJob;

import java.util.Map;
import java.util.Set;

/**
 * Client for fetching job and build information from Hudson
 */
public interface HudsonClient {

    /**
     * Finds all of the configured jobs for a given instance and returns the set of
     * builds for each job. At a minimum, the number and url of each Build will be
     * populated.
     *
     * @param instanceUrl the URL for the Hudson instance
     * @return a summary of every build for each job on the instance
     */
    Map<HudsonJob, Set<Build>> getInstanceJobs(String instanceUrl);

    /**
     * Fetch full populated build information for a build.
     *
     * @param buildUrl the url of the build
     * @param instanceUrl
     * @return a Build instance or null
     */
    Build getBuildDetails(String buildUrl, String instanceUrl);
}
