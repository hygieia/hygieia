package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.Performance;
import com.capitalone.dashboard.request.PerformanceCreateRequest;
import com.capitalone.dashboard.request.PerformanceSearchRequest;

public interface PerformanceService {

    /**
     * Finds all of the CodeQuality data matching the specified request criteria.
     *
     * @param request search criteria
     * @return quality data matching criteria
     */
    DataResponse<Iterable<Performance>> search(PerformanceSearchRequest request);
    String create(PerformanceCreateRequest request) throws HygieiaException;
}
