package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Pull;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.PullRequest;

public interface PullService {

    /**
     * Finds all of the Pulls matching the specified request criteria.
     *
     * @param request search criteria
     * @return Pulls matching criteria
     */
    DataResponse<Iterable<Pull>> search(PullRequest request);

    /**
     * Finds all of the Pulls matching the specified request criteria.
     *
     * @param request search criteria
     * @return Pulls matching criteria
     */
    DataResponse<Iterable<Pull>> searchMerged(PullRequest request);

    /**
     * Finds all of the Pulls matching the specified request criteria.
     *
     * @param request search criteria
     * @return Pulls matching criteria
     */
    DataResponse<Iterable<Pull>> searchOpen(PullRequest request);

    /**
     * Finds all of the Pulls matching the specified request criteria.
     *
     * @param request search criteria
     * @return Pulls matching criteria
     *
    DataResponse<Iterable<Pull>> searchByOrgs(PullRequest request);

    /**
     * Finds all of the Pulls matching the specified request criteria.
     *
     * @param request search criteria
     * @return Pulls matching criteria
     *
    DataResponse<Iterable<Pull>> searchMergedByOrgs(PullRequest request);
    //String createFromGitHubv3(JSONObject request) throws ParseException, HygieiaException;

    /**
     * Finds all of the Pulls matching the specified request criteria.
     *
     * @param request search criteria
     * @return Pulls matching criteria
     *
    DataResponse<Iterable<Pull>> searchOpenByOrgs(PullRequest request);
    */
}
