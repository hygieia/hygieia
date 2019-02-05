package com.capitalone.dashboard.api;

import com.atlassian.util.concurrent.Promise;
import com.capitalone.dashboard.api.domain.TestExecution;

/**
 * This interface is a client for test execution
 */
public interface TestExecutionRestClient {
    Promise<Iterable<TestExecution.Test>> getTests(TestExecution key);
    Promise<Void> setTests(TestExecution testExec);
    Promise<Void> removeTest(TestExecution testExecKey, TestExecution.Test testKey);

}
