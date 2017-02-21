package com.capitalone.dashboard.gitlab;

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

import com.capitalone.dashboard.gitlab.model.GitlabBoard;
import com.capitalone.dashboard.gitlab.model.GitlabIssue;
import com.capitalone.dashboard.gitlab.model.GitlabLabel;
import com.capitalone.dashboard.gitlab.model.GitlabList;
import com.capitalone.dashboard.gitlab.model.GitlabProject;
import com.capitalone.dashboard.gitlab.model.GitlabTeam;

@Component
public class DefaultGitlabClient implements GitlabClient {
	
	private static final String PAGINATION_HEADER = "X-Next-Page";
	
	private final RestOperations restOperations;
	private final GitlabUriUtility urlUtility;
	
	@Autowired
	public DefaultGitlabClient(RestOperations restOperations, GitlabUriUtility urlUtility) {
		this.restOperations = restOperations;
		this.urlUtility = urlUtility;
	}

	@Override
	public List<GitlabTeam> getTeams() {
		URI gitlabTeamUri = urlUtility.buildTeamsUri();
		return makePaginatedGitlabRequest(gitlabTeamUri, GitlabTeam[].class);
	}
	
	@Override
	public List<GitlabProject> getProjects() {
		URI uri = urlUtility.buildProjectsUri();
		return makePaginatedGitlabRequest(uri, GitlabProject[].class);
	}
	
    @Override
    public List<GitlabProject> getProjectsForTeam(String teamId) {
        URI uri = urlUtility.buildProjectsForTeamUri(teamId);
        return makePaginatedGitlabRequest(uri, GitlabProject[].class);
    }
    
    @Override
    public GitlabProject getProjectById(String projectId) {
        URI uri = urlUtility.buildProjectsByIdUri(projectId);
        HttpEntity<String> headersEntity = urlUtility.buildAuthenticationHeader();
        return restOperations.exchange(uri, HttpMethod.GET, headersEntity, GitlabProject.class).getBody();
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
		URI uri = urlUtility.buildIssuesForProjectUri(String.valueOf(project.getId()));
		List<GitlabIssue> issues = makePaginatedGitlabRequest(uri, GitlabIssue[].class);
		for(GitlabIssue issue : issues) {
			issue.setProject(project);
		}
		return issues;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> List<T> makePaginatedGitlabRequest(URI uri, Class gitlabResponseType) {
		URI restUri = uri;
		HttpEntity<String> headersEntity = urlUtility.buildAuthenticationHeader();
		
		List<T> body =  new ArrayList<>();
		boolean hasNextPage = true;
		while (hasNextPage) {
			ResponseEntity<T[]> response = restOperations.exchange(restUri, HttpMethod.GET, headersEntity, gitlabResponseType);
			CollectionUtils.addAll(body, response.getBody());
			
			if(hasNextPage = hasNextPage(response.getHeaders())) {
				restUri = urlUtility.updatePage(restUri, response.getHeaders().get(PAGINATION_HEADER).get(0));
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
		URI gitlabBoardsUrl = urlUtility.buildBoardsUri(projectId);
		return makePaginatedGitlabRequest(gitlabBoardsUrl, GitlabBoard[].class);
	}
	
	private boolean hasNextPage(HttpHeaders headers) {
		if (null == headers || CollectionUtils.isEmpty(headers.get(PAGINATION_HEADER))) {
			return false;
		}
		String nextPage = headers.get(PAGINATION_HEADER).get(0);
		return StringUtils.isNotBlank(nextPage);
	}

}
