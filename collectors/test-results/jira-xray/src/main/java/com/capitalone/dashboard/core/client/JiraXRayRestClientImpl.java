package com.capitalone.dashboard.core.client;

import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.atlassian.util.concurrent.Promise;
import com.capitalone.dashboard.api.TestExecutionRestClient;
import com.capitalone.dashboard.api.TestRestClient;
import com.capitalone.dashboard.api.TestRunRestClient;
import com.capitalone.dashboard.api.TestSetRestClient;
import com.capitalone.dashboard.api.JiraXRayRestClient;
import com.capitalone.dashboard.api.domain.TestExecution;
import java.net.URI;

/**
 * This is the implementation class for JiraXRayRestClient
 */
public class JiraXRayRestClientImpl extends AsynchronousJiraRestClient implements JiraXRayRestClient {
    private TestRestClient testClient=null;
    private TestExecutionRestClient testExecutionClient=null;
    private TestRunRestClient testRunClient=null;
    private TestSetRestClient testSetClient=null;

    public JiraXRayRestClientImpl(URI serverUri, DisposableHttpClient httpClient) {
        super(serverUri, httpClient);
        this.testRunClient=new TestRunRestClientImpl(serverUri,httpClient);
        this.testExecutionClient= new TestExecutionRestClientImpl(serverUri, httpClient) {
            @Override
            public Promise<Void> setTests(TestExecution testExec) {
                return null;
            }
        };
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
