package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Pull;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.PullRequest;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public interface PullService {

    /**
     * Finds all of the Pulls matching the specified request criteria.
     *
     * @param request search criteria
     * @return Pulls matching criteria
     */
    DataResponse<Iterable<Pull>> search(PullRequest request);

    String createFromGitHubv3(JSONObject request) throws ParseException, HygieiaException;
}
