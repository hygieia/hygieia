package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.request.TestResultRequest;

public interface TestResultService {

    DataResponse<Iterable<TestResult>> search(TestResultRequest request);
}
