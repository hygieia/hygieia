package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.CommitRequest;
import org.json.simple.JSONObject;

public interface CommitService {

    /**
     * Finds all of the Commits matching the specified request criteria.
     *
     * @param request search criteria
     * @return commits matching criteria
     */
    DataResponse<Iterable<Commit>> search(CommitRequest request);

    String createFromGitHubv3(JSONObject request);
}
