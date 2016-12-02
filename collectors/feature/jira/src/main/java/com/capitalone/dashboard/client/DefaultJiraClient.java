package com.capitalone.dashboard.client;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.util.concurrent.Promise;
import com.capitalone.dashboard.util.FeatureSettings;
import com.capitalone.dashboard.util.FeatureWidgetQueries;
import com.google.common.collect.Lists;

/**
 * A client that communicates via REST API calls to jira.
 * <p>
 * Latest REST API: https://docs.atlassian.com/jira/REST/latest/
 * <br>
 * Created against API for Jira 7.x. Should work with 6.x and 5.x.
 * 
 * @author <a href="mailto:MarkRx@users.noreply.github.com">MarkRx</a>
 */
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
	public DefaultJiraClient(FeatureSettings featureSettings, FeatureWidgetQueries featureWidgetQueries, JiraRestClientSupplier restSupplier) {
		this.featureSettings = featureSettings;
		this.featureWidgetQueries = featureWidgetQueries;
		this.client = restSupplier.get();
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
				LOGGER.debug("Exception", e);
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
				LOGGER.debug("Exception", e);
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
				LOGGER.debug("Exception", e);
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
				LOGGER.debug("Exception", e);
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
}
