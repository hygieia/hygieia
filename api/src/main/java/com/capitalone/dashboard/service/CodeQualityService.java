package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.CodeQualityCreateRequest;
import com.capitalone.dashboard.request.CodeQualityRequest;

public interface CodeQualityService {

    /**
     * Finds all of the CodeQuality data matching the specified request criteria.
     *
     * @param request search criteria
     * @return quality data matching criteria
     */
    DataResponse<Iterable<CodeQuality>> search(CodeQualityRequest request);
    String create(CodeQualityCreateRequest request) throws HygieiaException;
}
