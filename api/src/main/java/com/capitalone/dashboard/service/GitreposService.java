package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.GitRepoData;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.CommitRequest;
import com.capitalone.dashboard.request.GitreposRequest;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public interface GitreposService {

    /**
     * Finds all of the Commits matching the specified request criteria.
     *
     * @param request search criteria
     * @return commits matching criteria
     */
    DataResponse<Iterable<GitRepoData>> search(GitreposRequest request);

    //String createFromGitHubv3(JSONObject request) throws ParseException, HygieiaException;
}
