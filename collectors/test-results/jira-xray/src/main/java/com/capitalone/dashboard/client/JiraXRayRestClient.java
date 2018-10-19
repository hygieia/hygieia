package com.capitalone.dashboard.client;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.capitalone.dashboard.client.test.TestRestClient;
import com.capitalone.dashboard.client.testexecution.TestExecutionRestClient;
import com.capitalone.dashboard.client.testrun.TestRunRestClient;
import com.capitalone.dashboard.client.testset.TestSetRestClient;

/**
 * Interface for Jira XRay Rest Client
 */
public interface JiraXRayRestClient extends JiraRestClient {

    TestRestClient getTestClient();
    TestExecutionRestClient getTestExecutionClient();
    TestRunRestClient getTestRunClient();
    TestSetRestClient getTestSetClient();

}
