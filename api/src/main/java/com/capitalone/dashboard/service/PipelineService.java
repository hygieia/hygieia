package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.PipelineResponse;
import com.capitalone.dashboard.request.PipelineSearchRequest;

public interface PipelineService {

    /**
     * Retrieves all pipeline objects based on the provided search criteria.
     *
     * @param searchRequest search request
     * @return all pipelines for team dashboards
     */
    Iterable<PipelineResponse> search(PipelineSearchRequest searchRequest);
}
