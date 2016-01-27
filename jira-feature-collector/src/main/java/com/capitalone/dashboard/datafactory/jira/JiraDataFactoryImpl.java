package com.capitalone.dashboard.datafactory.jira;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Set;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import com.google.common.collect.Lists;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JiraDataFactoryImpl implements JiraDataFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(JiraDataFactoryImpl.class);
	protected JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
	protected JiraRestClient client;
	protected int pageSize;
	protected int pageIndex;
	protected String basicQuery;

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
	 */
	public JiraDataFactoryImpl(String jiraCredentials, String jiraBaseUrl) {
		URI jiraUri;
		try {
			jiraUri = new URI(jiraBaseUrl);
		} catch (URISyntaxException e) {
			LOGGER.error("There was a problem reading the provide Jira base URI syntax");
			jiraUri = null;
		}
		client = factory.createWithBasicHttpAuthentication(jiraUri,
				this.decodeCredentials(jiraCredentials).get("username"),
				this.decodeCredentials(jiraCredentials).get("password"));

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
	 * @param jiraProxyUrl
	 *            Jira proxy URL
	 * @param jiraProxyUrl
	 *            Jira proxy port number
	 */
	public JiraDataFactoryImpl(String jiraCredentials, String jiraBaseUrl, String jiraProxyUrl,
			String jiraProxyPort) {
		URI jiraUri = this.createJiraConnection(jiraBaseUrl, jiraProxyUrl + ":" + jiraProxyPort,
				this.decodeCredentials(jiraCredentials).get("username"),
				this.decodeCredentials(jiraCredentials).get("password"));
		client = factory.createWithBasicHttpAuthentication(jiraUri,
				this.decodeCredentials(jiraCredentials).get("username"),
				this.decodeCredentials(jiraCredentials).get("password"));

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
	 */
	public JiraDataFactoryImpl(int inPageSize, String jiraCredentials, String jiraBaseUrl) {
		URI jiraUri;
		try {
			jiraUri = new URI(jiraBaseUrl);
		} catch (URISyntaxException e) {
			LOGGER.error("There was a problem reading the provide Jira base URI syntax");
			jiraUri = null;
		}
		this.client = factory.createWithBasicHttpAuthentication(jiraUri,
				this.decodeCredentials(jiraCredentials).get("username"),
				this.decodeCredentials(jiraCredentials).get("password"));

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
	 * @param jiraProxyUrl
	 *            Jira proxy URL
	 * @param jiraProxyUrl
	 *            Jira proxy port number
	 */
	public JiraDataFactoryImpl(int inPageSize, String jiraCredentials, String jiraBaseUrl,
			String jiraProxyUrl, String jiraProxyPort) {
		URI jiraUri = this.createJiraConnection(jiraBaseUrl, jiraProxyUrl + ":" + jiraProxyPort,
				this.decodeCredentials(jiraCredentials).get("username"),
				this.decodeCredentials(jiraCredentials).get("password"));
		this.client = factory.createWithBasicHttpAuthentication(jiraUri,
				this.decodeCredentials(jiraCredentials).get("username"),
				this.decodeCredentials(jiraCredentials).get("password"));

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
	public String setQuery(String query) {
		this.setBasicQuery(query);
		return this.getBasicQuery();
	}

	/**
	 * Runs the jira-client library tools against a given REST-formatted query.
	 * This requires a pre-formatted paged query to run, and will not perform
	 * the paging for you - there are other helper methods for this.
	 * 
	 * @return A formatted JSONArray response
	 */
	public List<Issue> getJiraIssues() {
		Iterable<Issue> jiraRawRs = null;
		List<Issue> issues = new ArrayList<Issue>();
		Set<String> fields = new LinkedHashSet<String>();
		fields.add("*all");
		Promise<SearchResult> promisedRs = client.getSearchClient().searchJql(this.getBasicQuery(),
				this.getPageSize(), this.getPageIndex(), fields);
		try {
			jiraRawRs = promisedRs.claim().getIssues();
			if (jiraRawRs != null) {
				issues = Lists.newArrayList(jiraRawRs);
			} else {
				issues = new ArrayList<Issue>();
			}
		} catch (Exception e) {
			issues = new ArrayList<Issue>();
			LOGGER.warn("No result was available from Jira unexpectedly - defaulting to blank response. The reason for this fault is the following:"
					+ e.getCause());
		}

		return issues;
	}

	/**
	 * Runs the jira-client library tools against a given REST-formatted query.
	 * This works only for project/team-style values.
	 * 
	 * @return A list of Jira projects/teams
	 */
	public List<BasicProject> getJiraTeams() {
		Iterable<BasicProject> jiraRawRs = null;
		List<BasicProject> issues = new ArrayList<BasicProject>();
		Promise<Iterable<BasicProject>> promisedRs = client.getProjectClient().getAllProjects();
		try {
			jiraRawRs = promisedRs.claim();
			if (jiraRawRs != null) {
				issues = Lists.newArrayList(jiraRawRs);
			} else {
				issues = new ArrayList<BasicProject>();
			}
		} catch (Exception e) {
			issues = new ArrayList<BasicProject>();
			LOGGER.warn("No result was available from Jira unexpectedly - defaulting to blank response. The reason for this fault is the following:"
					+ e.getCause());
		}

		return issues;
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
	 * Used for testing: Accessor Method to get currently set page size
	 */
	public int getPageSize() {
		return this.pageSize;
	}

	/**
	 * Converts Jira basic authentication credentials from Base 64 string to a
	 * username/password map
	 * 
	 * @param jiraBasicAuthCredentialsInBase64
	 *            Base64-encoded single string in the following format:
	 *            <em>username:password</em><br/>
	 * <br/>
	 *            A null parameter value will result in an empty hash map
	 *            response (e.g., nothing gets decoded)
	 * @return Decoded username/password map of strings
	 */
	protected Map<String, String> decodeCredentials(String jiraBasicAuthCredentialsInBase64) {
		Map<String, String> credMap = new LinkedHashMap<String, String>();
		if (jiraBasicAuthCredentialsInBase64 != null) {
			StringTokenizer tokenizer = new StringTokenizer(new String(
					Base64.decodeBase64(jiraBasicAuthCredentialsInBase64)), ":");
			for (int i = 0; tokenizer.hasMoreTokens(); i++) {
				if (i == 0) {
					credMap.put("username", tokenizer.nextToken());
				} else {
					credMap.put("password", tokenizer.nextToken());
				}
			}
		}

		return credMap;

	}

	/**
	 * Generates an authenticated proxy connection URI and Jira URI for use in
	 * talking to Jira.
	 * 
	 * @param jiraBaseUri
	 *            A string representation of a Jira URI
	 * @param fullProxyUrl
	 *            A string representation of a completed proxy URL:
	 *            http://your.proxy.com:8080
	 * @param username
	 *            A string representation of a username to be authenticated
	 * @param password
	 *            A string representation of a password to be used in
	 *            authentication
	 * @return A fully configured Jira URI with authenticated proxy connection
	 */
	protected URI createJiraConnection(String jiraBaseUri, String fullProxyUrl, String username,
			String password) {
		try {
			URL url = new URL(jiraBaseUri);
			URI uri = new URI(fullProxyUrl);
			final String uname = username;
			final String pword = password;
			Proxy authProxy = null;

			if ((!uri.getHost().isEmpty()) || (uri.getPort() > 0)) {
				authProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(uri.getHost(),
						uri.getPort()));
				if ((username != null) && (password != null)) {
					Authenticator.setDefault(new Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(uname, pword.toCharArray());
						}
					});
				}
			}

			URLConnection connection = null;

			if (authProxy != null) {
				try {
					connection = url.openConnection(authProxy);
				} catch (IOException e) {
					LOGGER.error("There was a problem reading and openning the proxy connection");
				}
				if (((!uri.getHost().isEmpty()) || (uri.getPort() > 0))
						&& ((username != null) && (password != null))) {
					String proxyAuth = username + ":" + password;
					connection.setRequestProperty("Proxy-Authorization",
							"Basic " + Base64.encodeBase64String((proxyAuth).getBytes()));
				}
			}

			return connection.getURL().toURI();

		} catch (URISyntaxException | MalformedURLException e) {
			try {
				LOGGER.error("There was a problem parsing or reading the proxy configuration settings during openning a Jira connection. Defaulting to a non-proxy URI.");
				return new URI(jiraBaseUri);
			} catch (URISyntaxException e1) {
				LOGGER.error("Correction:  The Jira connection base URI cannot be read!");
				return null;
			}
		}
	}

	/**
	 * Destroys current Jira Client connection during asynchronous connection
	 */
	public void destroy() {
		try {
			this.client.close();
		} catch (IOException e) {
			LOGGER.error("There was a problem closing your Jira connection during query collection");
		}
	}
}
