package com.capitalone.dashboard.collector;

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

import com.capitalone.dashboard.model.GitlabTeam;

@Component
public class DefaultGitlabClient implements GitlabClient {
	private static final Log LOG = LogFactory.getLog(DefaultGitlabClient.class);
	
	private final RestOperations restOperations;
	private final FeatureSettings settings;
	
	@Autowired
	public DefaultGitlabClient(RestOperations restOperations, FeatureSettings settings) {
		this.restOperations = restOperations;
		this.settings = settings;
	}

	@Override
	public GitlabTeam[] getTeams() {
		URI gitlabTeamUri = buildTeamUri();
		HttpEntity<String> headersEntity = buildAuthenticationHeader();
		
		ResponseEntity<GitlabTeam[]> response = restOperations.exchange(gitlabTeamUri, HttpMethod.GET, headersEntity, GitlabTeam[].class);
		
		return response.getBody();
	}

	private HttpEntity<String> buildAuthenticationHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("PRIVATE-TOKEN", settings.getApiToken());
		HttpEntity<String> headersEntity = new HttpEntity<>(headers);
		return headersEntity;
	}

	private URI buildTeamUri() {
		UriComponentsBuilder builder = buildApiUri();
		URI uri = builder.path("groups")
						.build(true)
						.toUri();
		return uri;
	}

	private UriComponentsBuilder buildApiUri() {
		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
		return builder.scheme("https").host(settings.getHost()).path("api/v3/");
	}
	
	

}
