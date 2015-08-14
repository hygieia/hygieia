package com.capitalone.dashboard.datafactory.jira;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.datafactory.jira.sdk.connector.GetResponseBuilder;
import com.capitalone.dashboard.datafactory.jira.sdk.connector.GetResponseBuilderImpl;
import com.google.api.client.http.HttpRequestFactory;

@Component
public class JiraDataFactoryImpl implements JiraDataFactory {
	private static Log logger = LogFactory.getLog(JiraDataFactoryImpl.class);
	protected GetResponseBuilder jiraConnection = new GetResponseBuilderImpl();
	protected int pageSize;
	protected int pageIndex;
	protected JSONArray jsonOutputArray;
	protected String basicQuery;
	protected String pagingQuery;

	/**
	 * Default constructor required for Spring (not used)
	 */
	public JiraDataFactoryImpl() {
		// Intentionally empty for Spring
	}

	/**
	 * Default constructor, which sets page size to 1000 and page index to 0.
	 *
	 * @param jiraCredentials
	 *            Jira x64 encoded credentials (see jira client library for
	 *            details)
	 * @param jiraBaseUrl
	 *            Jira base URL
	 * @param jiraQueryEndpoint
	 *            Jira API query endpoint/context
	 */
	public JiraDataFactoryImpl(String jiraCredentials, String jiraBaseUrl,
			String jiraQueryEndpoint) {
		logger.debug("constructor: jiraCredentials = " + jiraCredentials +
		             "; jiraBaseUrl = " + jiraBaseUrl +
		             "; jiraQueryEndpoint = " + jiraQueryEndpoint);
		jiraConnection.setBasicAuth(jiraCredentials);
		jiraConnection.setJiraURI(jiraBaseUrl, jiraQueryEndpoint);

		this.pageSize = 1000;
		this.pageIndex = 0;
	}

	/**
	 * Default constructor, which sets page size to 1000 and page index to 0.
	 *
	 * @param jiraCredentials
	 *            Jira x64 encoded credentials (see jira client library for
	 *            details)
	 * @param jiraBaseUrl
	 *            Jira base URL
	 * @param jiraQueryEndpoint
	 *            Jira API query endpoint/context
	 * @param jiraProxyUrl
	 *            Jira proxy URL
	 * @param jiraProxyUrl
	 *            Jira proxy port number
	 */
	public JiraDataFactoryImpl(String jiraCredentials, String jiraBaseUrl,
			String jiraQueryEndpoint, String jiraProxyUrl, String jiraProxyPort) {
		jiraConnection.setBasicAuth(jiraCredentials);
		jiraConnection.setJiraURI(jiraBaseUrl, jiraQueryEndpoint);
		jiraConnection.setProxy(jiraProxyUrl, jiraProxyPort);

		this.pageSize = 1000;
		this.pageIndex = 0;
	}

	/**
	 * Constructs Jira data factory, but defaults the page size to the page size
	 * parameter given, and the page index to 0.
	 *
	 * @param inPageSize
	 *            A default page size to give the class on construction
	 * @param jiraCredentials
	 *            Jira x64 encoded credentials (see jira client library for
	 *            details)
	 * @param jiraBaseUrl
	 *            Jira base URL
	 * @param jiraQueryEndpoint
	 *            Jira API query endpoint/context
	 */
	public JiraDataFactoryImpl(int inPageSize, String jiraCredentials,
			String jiraBaseUrl, String jiraQueryEndpoint) {
		jiraConnection.setBasicAuth(jiraCredentials);
		jiraConnection.setJiraURI(jiraBaseUrl, jiraQueryEndpoint);

		this.pageSize = inPageSize;
		pageIndex = 0;
	}

	/**
	 * Constructs Jira data factory, but defaults the page size to the page size
	 * parameter given, and the page index to 0.
	 *
	 * @param inPageSize
	 *            A default page size to give the class on construction
	 * @param jiraCredentials
	 *            Jira x64 encoded credentials (see jira client library for
	 *            details)
	 * @param jiraBaseUrl
	 *            Jira base URL
	 * @param jiraQueryEndpoint
	 *            Jira API query endpoint/context
	 * @param jiraProxyUrl
	 *            Jira proxy URL
	 * @param jiraProxyUrl
	 *            Jira proxy port number
	 */
	public JiraDataFactoryImpl(int inPageSize, String jiraCredentials,
			String jiraBaseUrl, String jiraQueryEndpoint, String jiraProxyUrl,
			String jiraProxyPort) {
		jiraConnection.setBasicAuth(jiraCredentials);
		jiraConnection.setJiraURI(jiraBaseUrl, jiraQueryEndpoint);
		jiraConnection.setProxy(jiraProxyUrl, jiraProxyPort);

		this.pageSize = inPageSize;
		pageIndex = 0;
	}

	/**
	 * Sets the local query value on demand based on a given basic query.
	 *
	 * @param query
	 *            A query in REST syntax as a String
	 * @return The saved REST-syntax basic query
	 */
	public String buildBasicQuery(String query) {
		this.setBasicQuery(query);
		return this.getBasicQuery();
	}

	/**
	 * Creates a query on demand based on a given basic query and a specified
	 * page index value. It is recommended to use this method in a loop to
	 * ensure all pages are covered.
	 *
	 * @param pageIndex
	 *            A given query's current page index, from 0-oo
	 * @return A JSON-formatted response
	 */
	public String buildPagingQuery(int pageIndex) {
		this.setPageIndex(pageIndex);
		String pageFilter = "&maxResults=" + this.pageSize + "&startAt="
				+ pageIndex;
		this.setPagingQuery(this.getBasicQuery() + pageFilter);
		return this.getPagingQuery();
	}

	/**
	 * Runs the jira-client library tools against a given REST-formatted query.
	 * This requires a pre-formatted paged query to run, and will not perform
	 * the paging for you - there are other helper methods for this.
	 *
	 * @return A formatted JSONArray response
	 */
	@SuppressWarnings("unchecked")
	public JSONArray getPagingQueryResponse() {
		JSONArray mainMsg = new JSONArray();
		JSONObject innerObj = new JSONObject();
		HttpRequestFactory rqFactory = jiraConnection.generateRequestFactory();
		logger.debug(this.pagingQuery);
		JSONObject response = jiraConnection.getResponse(rqFactory,
				this.getPagingQuery());

		innerObj.put("issues", response.get("issues"));
		mainMsg.add(innerObj.get("issues"));

		mainMsg.add(response);
		return mainMsg;
	}

	/**
	 * Runs the jira-client library tools against a given REST-formatted query.
	 * This requires a pre-formatted basic query (single-use).
	 *
	 * @return A formatted JSONArray response
	 */
	@SuppressWarnings("unchecked")
	public JSONArray getQueryResponse() {
		JSONArray mainMsg = new JSONArray();
		JSONObject innerObj = new JSONObject();
		HttpRequestFactory rqFactory = jiraConnection.generateRequestFactory();
		JSONObject response = jiraConnection.getResponse(rqFactory,
				this.getBasicQuery());

		innerObj.put("issues", response.get("issues"));
		mainMsg.add(innerObj.get("issues"));

		return mainMsg;
	}

	/**
	 * Runs the jira-client library tools against a given REST-formatted query.
	 * This requires a pre-formatted paged query to run, and will not perform
	 * the paging for you - there are other helper methods for this. This is
	 * designed to work explicitly for team-related queries
	 *
	 * @return A formatted JSONArray response
	 */
	@SuppressWarnings("unchecked")
	public JSONArray getArrayQueryResponse() {
		JSONArray mainMsg = new JSONArray();
		HttpRequestFactory rqFactory = jiraConnection.generateRequestFactory();
		logger.debug(this.basicQuery);
		JSONArray response = jiraConnection.getResponseArray(rqFactory,
				this.getBasicQuery());

		mainMsg.add(response);

		return mainMsg;
	}

	/**
	 * Runs the jira-client library tools against a given REST-formatted query.
	 * This requires a pre-formatted basic query (single-use) and works only for
	 * Epic-style values.
	 *
	 * @return A formatted JSONArray response
	 */
	@SuppressWarnings("unchecked")
	public JSONArray getEpicQueryResponse() {
		JSONArray mainMsg = new JSONArray();
		HttpRequestFactory rqFactory = jiraConnection.generateRequestFactory();
		JSONObject response = jiraConnection.getResponse(rqFactory,
				this.getBasicQuery());

		mainMsg.add(response);

		return mainMsg;
	}

	/**
	 * Mutator method for page index.
	 *
	 * @param pageIndex
	 *            Page index of query
	 */
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	/**
	 * Accessor method for page index.
	 *
	 * @return Page index of query
	 */
	public int getPageIndex() {
		return this.pageIndex;
	}

	/**
	 * Accessor method for basic query formatted object.
	 *
	 * @return Basic Jira REST query
	 */
	public String getBasicQuery() {
		return this.basicQuery;
	}

	/**
	 * Mutator method for basic query formatted object.
	 *
	 * @param Basic
	 *            Jira REST query
	 */
	private void setBasicQuery(String basicQuery) {
		this.basicQuery = basicQuery;
	}

	/**
	 * Accessor method for retrieving paged query.
	 *
	 * @return The paged REST query
	 */
	public String getPagingQuery() {
		return this.pagingQuery;
	}

	/**
	 * Mutator method for setting paged query
	 *
	 * @param pagingQuery
	 *            The paged REST query
	 */
	private void setPagingQuery(String pagingQuery) {
		this.pagingQuery = pagingQuery;
	}

	/**
	 * Used for testing: Accessor Method to get currently set page size
	 */
	public int getPageSize() {
		return this.pageSize;
	}
}
