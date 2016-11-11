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
		HttpEntity<String> headersEntity = buildAuthenticationHeader();
		
		List<GitlabTeam> allTeams = new ArrayList<>();
		
		boolean hasNextPage = true;
		while (hasNextPage) {
			ResponseEntity<GitlabTeam[]> response = restOperations.exchange(gitlabTeamUri, HttpMethod.GET, headersEntity, GitlabTeam[].class);
			CollectionUtils.addAll(allTeams, response.getBody());
			
			if (hasNextPage = hasNextPage(response.getHeaders())) {
				gitlabTeamUri = urlUtility.updatePage(gitlabTeamUri, response.getHeaders().get("X-Next-Page").get(0));
			}
		}
		
		return allTeams;
	}
	
	@Override
	public List<GitlabProject> getProjects(ScopeOwnerCollectorItem team) {
		HttpEntity<String> headersEntity = buildAuthenticationHeader();
		
		List<GitlabProject> projects = new ArrayList<>();
		boolean hasNextPage = true;
		while (hasNextPage) {
			URI gitlabProjectUri = urlUtility.buildProjectsUrl(settings.getHost(), team.getTeamId());
			ResponseEntity<GitlabProject[]> response = restOperations.exchange(gitlabProjectUri, HttpMethod.GET, headersEntity, GitlabProject[].class);
			CollectionUtils.addAll(projects, response.getBody());
			
			if (hasNextPage = hasNextPage(response.getHeaders())) {
				gitlabProjectUri = urlUtility.updatePage(gitlabProjectUri, response.getHeaders().get("X-Next-Page").get(0));
			}
		}
		
		return projects;
	}
	
	private HttpEntity<String> buildAuthenticationHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("PRIVATE-TOKEN", settings.getApiToken());
		HttpEntity<String> headersEntity = new HttpEntity<>(headers);
		return headersEntity;
	}
	
	private boolean hasNextPage(HttpHeaders headers) {
		String nextPage = headers.get("X-Next-Page").get(0);
		return StringUtils.isNotBlank(nextPage);
	}

}
