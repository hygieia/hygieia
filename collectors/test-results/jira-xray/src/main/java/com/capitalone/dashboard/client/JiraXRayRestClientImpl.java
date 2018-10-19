package com.capitalone.dashboard.client;

import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.capitalone.dashboard.client.test.TestRestClient;
import com.capitalone.dashboard.client.testexecution.TestExecutionRestClient;
import com.capitalone.dashboard.client.testexecution.TestExecutionRestClientImpl;
import com.capitalone.dashboard.client.testrun.TestRunRestClient;
import com.capitalone.dashboard.client.testrun.TestRunRestClientImpl;
import com.capitalone.dashboard.client.testset.TestSetRestClient;

import java.net.URI;

/**
 * Implementation for Jira XRay Rest Client
 */
public class JiraXRayRestClientImpl extends AsynchronousJiraRestClient implements JiraXRayRestClient {
    private TestRestClient testClient=null;
    private TestExecutionRestClient testExecutionClient=null;
    private TestRunRestClient testRunClient=null;
    private TestSetRestClient testSetClient=null;

    public JiraXRayRestClientImpl(URI serverUri, DisposableHttpClient httpClient) {
        super(serverUri, httpClient);
        this.testRunClient=new TestRunRestClientImpl(serverUri,httpClient);
        this.testExecutionClient=new TestExecutionRestClientImpl(serverUri,httpClient);
    }

    public TestRestClient getTestClient() {
        return testClient;
    }

    public TestExecutionRestClient getTestExecutionClient() {
        return testExecutionClient;
    }

    public TestRunRestClient getTestRunClient() {
        return testRunClient;
    }

    public TestSetRestClient getTestSetClient() {
        return testSetClient;
    }
}
