package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Developer;

/**
 * Client for fetching commit history from GitHub
 */
public interface DeveloperDataClient {

    /**
     * Fetch all of the commits for the provided Git.
     *
     * @param userId
     * @param keys
     * @return Developer Data
    */
     Developer getDeveloper(String userId, DeveloperDataSettings keys);
}
