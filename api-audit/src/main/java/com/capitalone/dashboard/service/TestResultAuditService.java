package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.response.PerformaceTestAuditResponse;
import com.capitalone.dashboard.response.TestResultsResponse;

public interface TestResultAuditService {

    TestResultsResponse getTestResultExecutionDetails(String jobUrl, long beginDt, long endDt) throws HygieiaException;

    PerformaceTestAuditResponse getresultsBycomponetAndTime(String businessComp, long from, long to);


}
