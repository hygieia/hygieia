package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Issue;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.IssueRequest;

public interface IssueService {

    /**
     * Finds all of the Issues matching the specified request criteria.
     *
     * @param request search criteria
     * @return Issues matching criteria
     */
    DataResponse<Iterable<Issue>> search(IssueRequest request);

    /**
     * Finds all of the Issues matching the specified request criteria.
     *
     * @param request search criteria
     * @return Issues matching criteria
     */
    DataResponse<Iterable<Issue>> searchClosed(IssueRequest request);

}
