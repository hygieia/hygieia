package com.capitalone.dashboard.collector;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.model.GitlabBoard;
import com.capitalone.dashboard.model.GitlabIssue;
import com.capitalone.dashboard.model.GitlabLabel;
import com.capitalone.dashboard.model.GitlabList;
import com.capitalone.dashboard.model.GitlabProject;
import com.capitalone.dashboard.model.GitlabTeam;
import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;
import com.capitalone.dashboard.utilities.GitlabUrlUtility;

@Component
public class DefaultGitlabClient implements GitlabClient {
	
	private final RestOperations restOperations;
	private final FeatureSettings settings;
	private final GitlabUrlUtility urlUtility;
	
	@Autowired
	public DefaultGitlabClient(RestOperations restOperations, FeatureSettings settings, GitlabUrlUtility urlUtility) {
		this.restOperations = restOperations;
		this.settings = settings;
		this.urlUtility = urlUtility;
	}

	@Override
	public List<GitlabTeam> getTeams() {
		URI gitlabTeamUri = urlUtility.buildTeamUri(settings.getHost());
		return makePaginatedGitlabRequest(gitlabTeamUri, GitlabTeam[].class);
	}
	
	@Override
	public List<GitlabProject> getProjects(ScopeOwnerCollectorItem team) {
		URI uri = urlUtility.buildProjectsUrl(settings.getHost(), team.getTeamId());
		return makePaginatedGitlabRequest(uri, GitlabProject[].class);
	}
	
	@Override
	public List<GitlabLabel> getInProgressLabelsForProject(Long projectId) {
		List<GitlabLabel> labels = new ArrayList<>();		
		List<GitlabBoard> boards = getBoardsForProject(String.valueOf(projectId));	
		for (GitlabBoard board : boards) {
			 labels.addAll(getLabelsForInProgressIssues(board));
		}
		
		return labels;
	}
	
	@Override
	public List<GitlabIssue> getIssuesForProject(GitlabProject project) {
		URI uri = urlUtility.buildIssuesForProjectUrl(settings.getHost(), String.valueOf(project.getId()));
		List<GitlabIssue> issues = makePaginatedGitlabRequest(uri, GitlabIssue[].class);
		for(GitlabIssue issue : issues) {
			issue.setProject(project);
		}
		return issues;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> List<T> makePaginatedGitlabRequest(URI uri, Class gitlabResponseType) {
		HttpEntity<String> headersEntity = buildAuthenticationHeader();
		
		List<T> body =  new ArrayList<>();
		boolean hasNextPage = true;
		while (hasNextPage) {
			ResponseEntity<T[]> response = restOperations.exchange(uri, HttpMethod.GET, headersEntity, gitlabResponseType);
			CollectionUtils.addAll(body, response.getBody());
			
			if(hasNextPage = hasNextPage(response.getHeaders())) {
				uri = urlUtility.updatePage(uri, response.getHeaders().get("X-Next-Page").get(0));
			}
		}
		
		return body;
	}

	private List<GitlabLabel> getLabelsForInProgressIssues(GitlabBoard board) {
		List<GitlabLabel> labels = new ArrayList<>();
		for (GitlabList list : board.getLists()) {
			labels.add(list.getLabel());
		}
		return labels;
	}

	private List<GitlabBoard> getBoardsForProject(String projectId) {
		URI gitlabBoardsUrl = urlUtility.buildBoardsUrl(settings.getHost(), projectId);
		return makePaginatedGitlabRequest(gitlabBoardsUrl, GitlabBoard[].class);
	}

	private HttpEntity<String> buildAuthenticationHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("PRIVATE-TOKEN", settings.getApiToken());
		HttpEntity<String> headersEntity = new HttpEntity<>(headers);
		return headersEntity;
	}
	
	private boolean hasNextPage(HttpHeaders headers) {
		String nextPage;
		try {
			nextPage = headers.get("X-Next-Page").get(0);
		} catch (NullPointerException e) {
			return false;
		}
		return StringUtils.isNotBlank(nextPage);
	}

}
