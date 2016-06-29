package com.capitalone.dashboard.client;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import com.capitalone.dashboard.util.FeatureSettings;
import com.capitalone.dashboard.util.FeatureWidgetQueries;
import com.google.common.collect.Lists;

@Component
public class DefaultJiraClient implements JiraClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJiraClient.class);
	
	private final DateFormat QUERY_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	private static final Set<String> DEFAULT_FIELDS = new HashSet<>();
	static {
		DEFAULT_FIELDS.add("*all,-comment,-watches,-worklog,-votes,-reporter,-creator,-attachment");
	}
	
	private final FeatureSettings featureSettings;
	private final FeatureWidgetQueries featureWidgetQueries;
	
	private JiraRestClient client;
	
	@Autowired
	public DefaultJiraClient(FeatureSettings featureSettings, FeatureWidgetQueries featureWidgetQueries) {
		this.featureSettings = featureSettings;
		this.featureWidgetQueries = featureWidgetQueries;
		
		client = createClient(featureSettings);
	}
	
	@Override
	public List<Issue> getIssues(long startTime, int pageStart) {
		List<Issue> rt = new ArrayList<>();
		
		if (client != null) {
			try {
				// example "1900-01-01 00:00"
				String startDateStr = QUERY_DATE_FORMAT.format(new Date(startTime));
				
				String query = featureWidgetQueries.getStoryQuery(startDateStr,
						featureSettings.getJiraIssueTypeId(), featureSettings.getStoryQuery());
				
				Promise<SearchResult> promisedRs = client.getSearchClient().searchJql(
						query, featureSettings.getPageSize(), pageStart, DEFAULT_FIELDS);
				
				SearchResult sr = promisedRs.claim();

				Iterable<Issue> jiraRawRs = sr.getIssues();
				
				if (jiraRawRs != null) {
					if (LOGGER.isDebugEnabled()) {
						int pageEnd = Math.min(pageStart + getPageSize() - 1, sr.getTotal());
						
						LOGGER.debug(String.format("Processing issues %d - %d out of %d", pageStart, pageEnd, sr.getTotal()));
					}
					
					rt = Lists.newArrayList(jiraRawRs);
				}
			} catch (RestClientException e) {
				if (e.getStatusCode().get() != null && e.getStatusCode().get() == 401 ) {
					LOGGER.error("Error 401 connecting to JIRA server, your credentials are probably wrong. Note: Ensure you are using JIRA user name not your email address.");
				} else {
					LOGGER.error("No result was available from Jira unexpectedly - defaulting to blank response. The reason for this fault is the following:" + e.getCause());
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Exception", e);
				}
			}
		} else {
			LOGGER.warn("Jira client setup failed. No results obtained. Check your jira setup.");
		}
		
		return rt;
	}

	@Override
	public List<BasicProject> getProjects() {
		List<BasicProject> rt = new ArrayList<>();
		
		if (client != null) {
			try {
				Promise<Iterable<BasicProject>> promisedRs = client.getProjectClient().getAllProjects();
				
				Iterable<BasicProject> jiraRawRs = promisedRs.claim();
				if (jiraRawRs != null) {
					rt = Lists.newArrayList(jiraRawRs);
				}
			} catch (RestClientException e) {
				if (e.getStatusCode().get() != null && e.getStatusCode().get() == 401 ) {
					LOGGER.error("Error 401 connecting to JIRA server, your credentials are probably wrong. Note: Ensure you are using JIRA user name not your email address.");
				} else {
					LOGGER.error("No result was available from Jira unexpectedly - defaulting to blank response. The reason for this fault is the following:" + e.getCause());
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Exception", e);
				}
			}
		} else {
			LOGGER.warn("Jira client setup failed. No results obtained. Check your jira setup.");
		}
		
		return rt;
	}

	@Override
	public Issue getEpic(String epicKey) {
		List<Issue> rt = new ArrayList<>();
		
		if (client != null) {
			try {
				String query = this.featureWidgetQueries.getEpicQuery(epicKey, "epic");
				
				Promise<SearchResult> promisedRs = client.getSearchClient().searchJql(
						query, featureSettings.getPageSize(), 0, DEFAULT_FIELDS);
				
				SearchResult sr = promisedRs.claim();
				
				Iterable<Issue> jiraRawRs = sr.getIssues();
				
				if (jiraRawRs != null) {
					rt = Lists.newArrayList(jiraRawRs);
				}
			} catch (RestClientException e) {
				if (e.getStatusCode().get() != null && e.getStatusCode().get() == 401 ) {
					LOGGER.error("Error 401 connecting to JIRA server, your credentials are probably wrong. Note: Ensure you are using JIRA user name not your email address.");
				} else {
					LOGGER.error("No result was available from Jira unexpectedly - defaulting to blank response. The reason for this fault is the following:" + e.getCause());
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Exception", e);
				}
			}
		} else {
			LOGGER.warn("Jira client setup failed. No results obtained. Check your jira setup.");
		}
		
		return rt.isEmpty()? null : rt.iterator().next();
	}
	
	/**
	 * 
	 */
	@Override
	public List<Issue> getEpics(List<String> epicKeys) {
		List<Issue> rt = new ArrayList<>();
		
		if (client != null) {
			try {
				String query = this.featureWidgetQueries.getEpicQuery(epicKeys, "epics");
				
				// This could be paged too
				int total = Integer.MAX_VALUE;
				for (int j = 0; j < total; j += featureSettings.getPageSize()) {

					Promise<SearchResult> promisedRs = client.getSearchClient().searchJql(
							query, featureSettings.getPageSize(), j, DEFAULT_FIELDS);
					
					SearchResult sr = promisedRs.claim();
					total = sr.getTotal();
					
					Iterable<Issue> jiraRawRs = sr.getIssues();
					
					if (jiraRawRs != null) {
						rt.addAll(Lists.newArrayList(jiraRawRs));
					}
				}
			} catch (RestClientException e) {
				if (e.getStatusCode().get() != null && e.getStatusCode().get() == 401 ) {
					LOGGER.error("Error 401 connecting to JIRA server, your credentials are probably wrong. Note: Ensure you are using JIRA user name not your email address.");
				} else {
					LOGGER.error("No result was available from Jira unexpectedly - defaulting to blank response. The reason for this fault is the following:" + e.getCause());
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Exception", e);
				}
			}
		} else {
			LOGGER.warn("Jira client setup failed. No results obtained. Check your jira setup.");
		}
		
		return rt;
	}
	
	@Override
	public int getPageSize() {
		return featureSettings.getPageSize();
	}
	
	private JiraRestClient createClient(FeatureSettings featureSettings) {
		JiraRestClient client = null;
		
		String jiraCredentials = featureSettings.getJiraCredentials();
		String jiraBaseUrl = featureSettings.getJiraBaseUrl();
		String proxyUri = null;
		String proxyPort = null;
		
		URI jiraUri = null;
		
		try {
			if (!featureSettings.getJiraProxyUrl().isEmpty() && (featureSettings.getJiraProxyPort() != null)) {
				proxyUri = this.featureSettings.getJiraProxyUrl();
				proxyPort = this.featureSettings.getJiraProxyPort();
				
				jiraUri = this.createJiraConnection(jiraBaseUrl,
						proxyUri + ":" + proxyPort, 
						this.decodeCredentials(jiraCredentials).get("username"),
						this.decodeCredentials(jiraCredentials).get("password"));
			} else {
				jiraUri = new URI(jiraBaseUrl);
			}
			
			InetAddress.getByName(jiraUri.getHost());
			client = new AsynchronousJiraRestClientFactory()
					.createWithBasicHttpAuthentication(jiraUri, 
							decodeCredentials(jiraCredentials).get("username"),
							decodeCredentials(jiraCredentials).get("password"));
			
		} catch (UnknownHostException | URISyntaxException e) {
			LOGGER.error("The Jira host name is invalid");
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Exception", e);
			}
		}
		
		return client;
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
	private Map<String, String> decodeCredentials(String jiraBasicAuthCredentialsInBase64) {
		Map<String, String> credMap = new LinkedHashMap<String, String>();
		if (jiraBasicAuthCredentialsInBase64 != null) {
				//the tokenize includes a \n to ensure we trim those off the end (mac base64 adds these!)
			StringTokenizer tokenizer = new StringTokenizer(new String(
					Base64.decodeBase64(jiraBasicAuthCredentialsInBase64)), ":\n");
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
	private URI createJiraConnection(String jiraBaseUri, String fullProxyUrl, String username,
			String password) {
		final String uname = username;
		final String pword = password;
		Proxy proxy = null;
		URLConnection connection = null;
		try {
			if (!StringUtils.isEmpty(jiraBaseUri)) {
				URL baseUrl = new URL(jiraBaseUri);
				if (!StringUtils.isEmpty(fullProxyUrl)) {
					URL proxyUrl = new URL(fullProxyUrl);
					URI proxyUri = new URI(proxyUrl.getProtocol(), proxyUrl.getUserInfo(),
							proxyUrl.getHost(), proxyUrl.getPort(), proxyUrl.getPath(),
							proxyUrl.getQuery(), null);
					proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUri.getHost(),
							proxyUri.getPort()));
					connection = baseUrl.openConnection(proxy);

					if (!StringUtils.isEmpty(username) && (!StringUtils.isEmpty(password))) {
						String creds = uname + ":" + pword;
						Authenticator.setDefault(new Authenticator() {
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(uname, pword.toCharArray());
							}
						});
						connection.setRequestProperty("Proxy-Authorization",
								"Basic " + Base64.encodeBase64String((creds).getBytes()));
					}
				} else {
					connection = baseUrl.openConnection();
				}
			} else {
				LOGGER.error("The response from Jira was blank or non existant - please check your property configurations");
				return null;
			}

			return connection.getURL().toURI();

		} catch (URISyntaxException | IOException e) {
			try {
				LOGGER.error("There was a problem parsing or reading the proxy configuration settings during openning a Jira connection. Defaulting to a non-proxy URI.");
				return new URI(jiraBaseUri);
			} catch (URISyntaxException e1) {
				LOGGER.error("Correction:  The Jira connection base URI cannot be read!");
				return null;
			}
		}
	}
}
