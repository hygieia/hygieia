package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.GitRequestRequest;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public interface GitRequestService {

    /**
     * Finds all of the Pulls matching the specified request criteria.
     *
     * @param request search criteria
     * @param type search criteria - pull or issue
     * @param state search criteria - open, closed, merged or all (default)
     * @return Pulls matching criteria
     */
    DataResponse<Iterable<GitRequest>> search(GitRequestRequest request,
                                              String type, String state);

    String createFromGitHubv3(JSONObject request) throws ParseException, HygieiaException;

}
