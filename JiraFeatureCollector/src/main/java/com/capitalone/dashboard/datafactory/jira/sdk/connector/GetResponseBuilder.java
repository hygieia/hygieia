package com.capitalone.dashboard.datafactory.jira.sdk.connector;

import org.apache.commons.lang3.NotImplementedException;

import com.google.api.client.http.HttpRequestFactory;

/**
 * Builds HTTP GET responses for talking to Jira. Requires a fully qualified
 * HttpRequestFactory object before use
 * 
 * @author kfk884
 * 
 */
public interface GetResponseBuilder extends BaseConnection {
	/**
	 * Retrieves a JSONObject response object from Jira based on connection
	 * details and a given REST query using basic authentication (i.e., username
	 * / password)
	 * 
	 * @param rqFactory
	 *            A pre-generated HttpRequestFactory for talking to Jira
	 * @param query
	 *            This should be a rest query formatted to follow the base
	 *            context path / API endpoint provided
	 * @return JSONObject object response from Jira
	 */
	public org.json.simple.JSONObject getResponse(HttpRequestFactory rqFactory,
			String query);

	/**
	 * Retrieves a JSONArray response object from Jira based on connection
	 * details and a given REST query using basic authentication (i.e., username
	 * / password)
	 * 
	 * @param rqFactory
	 *            A pre-generated HttpRequestFactory for talking to Jira
	 * @param query
	 *            This should be a rest query formatted to follow the base
	 *            context path / API endpoint provided
	 * @return JSONObject object response from Jira
	 */
	public org.json.simple.JSONArray getResponseArray(
			HttpRequestFactory rqFactory, String query);

	/**
	 * Retrieves a JSONArray response object from Jira based on connection
	 * details and a given REST query using OAuth 2.0 authentication
	 * 
	 * @param rqFactory
	 *            A pre-generated HttpRequestFactory for talking to Jira
	 * @param query
	 *            This should be a rest query formatted to follow the base
	 *            context path / API endpoint provided
	 * @return JSONObject object response from Jira
	 */
	public org.json.simple.JSONObject getResponseOAuth(
			HttpRequestFactory rqFactory, String query)
			throws NotImplementedException;
}
