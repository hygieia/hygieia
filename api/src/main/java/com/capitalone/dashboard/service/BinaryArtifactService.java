package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.BinaryArtifact;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.BinaryArtifactCreateRequest;
import com.capitalone.dashboard.request.BinaryArtifactSearchRequest;

public interface BinaryArtifactService {

    /**
     * Finds all of the Builds matching the specified request criteria.
     *
     * @param request search criteria
     * @return builds matching criteria
     */
    DataResponse<Iterable<BinaryArtifact>> search(BinaryArtifactSearchRequest request);

    String create(BinaryArtifactCreateRequest request) throws HygieiaException;
}
