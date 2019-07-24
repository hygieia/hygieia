package com.capitalone.dashboard.api;

import com.atlassian.util.concurrent.Promise;
import com.capitalone.dashboard.api.domain.*;

/**
 * This interface is a client for test run
 */
public interface TestRunRestClient {

    Promise<TestRun> getTestRun(String testExecKey, String testKey);
    Promise<TestRun> getTestRun(Long testRunId);
    Promise<Void> updateTestRun(TestRun testRunInput);
    Promise<Iterable<TestRun>> getTestRuns(String testKey);


    Promise<TestRun.Status> getStatus(Long testRunId);


}
