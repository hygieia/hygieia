package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.request.TestDataCreateRequest;
import com.capitalone.dashboard.request.TestResultRequest;

public interface TestResultService {

    DataResponse<Iterable<TestResult>> search(TestResultRequest request);
    String create(TestDataCreateRequest request) throws HygieiaException;
}
