package com.capitalone.dashboard.datafactory.jira.sdk.connector;

import com.google.api.client.http.HttpRequestFactory;

/**
 * Generates the base HTTP connection artifact needed to connect to Jira
 *
 * @author kfk884
 *
 */
public interface BaseConnection {
	/**
	 * Generates proxy credentials
	 *
	 * @param proxy
	 * @param port
	 * @return Boolean indicating if set was successful
	 */
	boolean setProxy(String proxy, String port);

	/**
	 * Generates OAuth2.0 based authentication credentials
	 *
	 * @param authToken
	 * @param refreshToken
	 * @param redirectUri
	 * @param expireTime
	 * @return Boolean indicating if set was successful
	 */
	boolean setOAuth(String authToken, String refreshToken,
			String redirectUri, String expireTime);

	/**
	 * Generates basic authentication credentials
	 *
	 * @param credentials
	 *            A 64-bit encoded has using the following format:
	 *            <strong>EID:PASSWORD</strong>
	 * @return Boolean indicating if set was successful
	 */
	boolean setBasicAuth(String credentials);

	/**
	 * Generates a Jira URI that includes the API endpoint (e.g., API Context
	 * Path) required for generic use
	 *
	 * @param baseUrl
	 * @param apiContextPath
	 * @return Boolean indicating if set was successful
	 */
	boolean setJiraURI(String baseUrl, String apiContextPath);

	/**
	 * Generates a fully qualified Generic URL for talking to Jira via the API
	 * at a specified endpoint
	 *
	 * @return A formatted HttpRequestFactory object that can be given to a
	 *         HttpRequest Builder for talking to Jira
	 */
	HttpRequestFactory generateRequestFactory();
}
