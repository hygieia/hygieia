package com.capitalone.dashboard.client.testrun;

import com.atlassian.util.concurrent.Promise;
import com.capitalone.dashboard.client.api.domain.TestRun;
import com.capitalone.dashboard.client.api.domain.TestStep;
import com.capitalone.dashboard.client.api.domain.Defect;
import com.capitalone.dashboard.client.api.domain.Evidence;
import com.capitalone.dashboard.client.api.domain.Example;
import com.capitalone.dashboard.client.api.domain.Comment;

/**
 * Interface for Test Run Rest Client
 */
public interface TestRunRestClient {

    Promise<TestRun> getTestRun(String testExecKey, String testKey);
    Promise<TestRun> getTestRun(Long testRunId);
    Promise<Void> updateTestRun(TestRun testRunInput);
    Promise<Iterable<TestRun>> getTestRuns(String testKey);


    Promise<TestRun.Status> getStatus(Long testRunId);
    Promise<TestRun.Status> updateStatus(Long testRunId, TestRun.Status statusInput);
    Promise<TestRun.Status> getStatus(String testExecKey, String testKey);

    Promise<Defect> addDefect(String issueKey, Defect defect);
    Promise<Iterable<Defect>> getDefects(Long testRunId);
    Promise<Void> removeDefect(Long testRunId, String issueKey);


    Promise<Iterable<Evidence>> getEvidences(Long testRunId);
    Promise<Evidence> createEvidence(Long testRunId, Evidence newEvidence);
    Promise<Void> removeEvidence(Long testRunId, String resourceName);
    Promise<Void> removeEvidence(Long testRunId, Long evId);

    Promise<Comment> getComment(Long testRunId);
    Promise<Comment> updateComment(Long testRunId, String comment);

    Promise<Example> getExample(Long testRunId);

    Promise<Iterable<TestStep>> getTestSteps(Long testRunId);

}
