package com.capitalone.dashboard.client;

import java.util.List;
import java.util.Map;

import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Issue;

public interface JiraClient {
	List<Issue> getIssues(long startTime, int pageStart);
	
	List<BasicProject> getProjects();
	
	Issue getEpic(String epicId);
	
	int getPageSize();

	List<Issue> getEpics(List<String> epicKeys);
	
	Map<String, String> getStatusMapping();
}
