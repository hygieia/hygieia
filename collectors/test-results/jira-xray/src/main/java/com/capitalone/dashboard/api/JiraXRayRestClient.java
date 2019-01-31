package com.capitalone.dashboard.api;

import com.atlassian.jira.rest.client.api.JiraRestClient;

/**
 * This interface have clients for jira xray test objects
 */
public interface JiraXRayRestClient extends JiraRestClient {

    TestRestClient getTestClient();
    TestExecutionRestClient getTestExecutionClient();
    TestRunRestClient getTestRunClient();
    TestSetRestClient getTestSetClient();

}
