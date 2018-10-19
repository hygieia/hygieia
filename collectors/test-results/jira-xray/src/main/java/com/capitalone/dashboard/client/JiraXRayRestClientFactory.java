package com.capitalone.dashboard.client;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;

import java.net.URI;

/**
 * Jira XRay Rest Client Factory methods
 */
public class JiraXRayRestClientFactory extends AsynchronousJiraRestClientFactory {


    public AsynchronousJiraRestClient create(URI serverUri, AuthenticationHandler authenticationHandler) {
        DisposableHttpClient httpClient = (new AsynchronousHttpClientFactory()).createClient(serverUri, authenticationHandler);

        return new JiraXRayRestClientImpl(serverUri, httpClient);
    }

    public AsynchronousJiraRestClient createWithBasicHttpAuthentication(URI serverUri, String username, String password) {
        return this.create(serverUri, (AuthenticationHandler)(new BasicHttpAuthenticationHandler(username, password)));
    }

    public AsynchronousJiraRestClient create(URI serverUri, HttpClient httpClient) {
        DisposableHttpClient disposableHttpClient = (new AsynchronousHttpClientFactory()).createClient(httpClient);
        return new JiraXRayRestClientImpl(serverUri, disposableHttpClient);
    }


}
