package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.BuildDataCreateRequest;
import com.capitalone.dashboard.request.BuildSearchRequest;
import com.capitalone.dashboard.request.BuildServerWatchRequest;
import org.springframework.http.ResponseEntity;

public interface BuildService {

    /**
     * Finds all of the Builds matching the specified request criteria.
     *
     * @param request search criteria
     * @return builds matching criteria
     */
    DataResponse<Iterable<Build>> search(BuildSearchRequest request);

    String create(BuildDataCreateRequest request) throws HygieiaException;

    ResponseEntity watch(BuildServerWatchRequest request);
}
