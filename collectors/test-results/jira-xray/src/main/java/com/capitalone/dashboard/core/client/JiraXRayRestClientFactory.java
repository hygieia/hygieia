package com.capitalone.dashboard.core.client;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;

import java.net.URI;

/**
 * This class has the factory methods for making connections to jira server
 */
public class JiraXRayRestClientFactory extends AsynchronousJiraRestClientFactory {


    public JiraRestClient create(URI serverUri, AuthenticationHandler authenticationHandler) {
        DisposableHttpClient httpClient = (new AsynchronousHttpClientFactory()).createClient(serverUri, authenticationHandler);

        return new JiraXRayRestClientImpl(serverUri, httpClient);
    }

    public JiraRestClient createWithBasicHttpAuthentication(URI serverUri, String username, String password) {
        return this.create(serverUri, new BasicHttpAuthenticationHandler(username, password));
    }

    public JiraRestClient create(URI serverUri, HttpClient httpClient) {
        DisposableHttpClient disposableHttpClient = (new AsynchronousHttpClientFactory()).createClient(httpClient);
        return new JiraXRayRestClientImpl(serverUri, disposableHttpClient);
    }


}
