package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Issue;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.IssueRequest;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public interface IssueService {

    /**
     * Finds all of the Issues matching the specified request criteria.
     *
     * @param request search criteria
     * @return Issues matching criteria
     */
    DataResponse<Iterable<Issue>> search(IssueRequest request);

    String createFromGitHubv3(JSONObject request) throws ParseException, HygieiaException;
}
