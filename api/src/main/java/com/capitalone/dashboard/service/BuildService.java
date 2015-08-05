package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.BuildRequest;

public interface BuildService {

    /**
     * Finds all of the Builds matching the specified request criteria.
     *
     * @param request search criteria
     * @return builds matching criteria
     */
    DataResponse<Iterable<Build>> search(BuildRequest request);
}
