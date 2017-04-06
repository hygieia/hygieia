package com.capitalone.dashboard.gitlab;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.capitalone.dashboard.collector.FeatureSettings;

@Component
public class GitlabUriUtility {
	
	private static final String DEFAULT_PROTOCOL = "http";
	private static final String DEFAULT_HOST = "gitlab.com";
	private static final String API_PATH_SEGMENT = "api";
	private static final String VERSION_PATH_SEGMENT = "v3";
	private static final String ISSUES_PATH_SEGMENT = "issues";
	private static final String BOARDS_PATH_SEGMENT = "boards";
	private static final String PROJECTS_PATH_SEGMENT = "projects";
	private static final String GROUPS_PATH_SEGMENT = "groups";
	private static final String PAGE_QUERY_PARAM_KEY = "page";
	private static final String RESULT_PER_PAGE_QUERY_PARAM_KEY = "per_page";
	private static final String RESULTS_PER_PAGE = "100";
	private static final String PRIVATE_TOKEN_HEADER_KEY = "PRIVATE-TOKEN";
	
	private final FeatureSettings settings;
	
	@Autowired
	public GitlabUriUtility(FeatureSettings settings) {
		this.settings = settings;
	}
	
	public URI updatePage(URI uri, String page) {
		return UriComponentsBuilder.fromUri(uri).queryParam(PAGE_QUERY_PARAM_KEY, page).build(true).toUri();
	}

	public URI buildTeamsUri() {
		UriComponentsBuilder builder = buildApiUri();
		URI uri = builder.pathSegment(GROUPS_PATH_SEGMENT)
						.build()
						.toUri();
		return uri;
	}
	
	public URI buildProjectsUri() {
		UriComponentsBuilder builder = buildApiUri();
		URI uri = builder.pathSegment(PROJECTS_PATH_SEGMENT).build().toUri();
		return uri;
	}
	
    public URI buildProjectsForTeamUri(String teamId) {
        UriComponentsBuilder builder = buildApiUri();
        URI uri = builder.pathSegment(GROUPS_PATH_SEGMENT).pathSegment(teamId).pathSegment(PROJECTS_PATH_SEGMENT).build().toUri();
        return uri;
    }
    
    public URI buildProjectsByIdUri(String projectId) {
        UriComponentsBuilder builder = buildApiUri();
        URI uri = builder.pathSegment(PROJECTS_PATH_SEGMENT).pathSegment(projectId).build().toUri();
        return uri;
    }
	
	public URI buildBoardsUri(String projectId) {
		UriComponentsBuilder builder = buildApiUri();
		URI uri = builder.pathSegment(PROJECTS_PATH_SEGMENT).pathSegment(projectId).pathSegment(BOARDS_PATH_SEGMENT).build().toUri();
		return uri;
	}
	
	public URI buildIssuesForProjectUri(String projectId) {
		UriComponentsBuilder builder = buildApiUri();
		URI uri = builder.pathSegment(PROJECTS_PATH_SEGMENT).pathSegment(projectId).pathSegment(ISSUES_PATH_SEGMENT).build().toUri();
		return uri;
	}
	
	public HttpEntity<String> buildAuthenticationHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.set(PRIVATE_TOKEN_HEADER_KEY, settings.getApiToken());
		HttpEntity<String> headersEntity = new HttpEntity<>(headers);
		return headersEntity;
	}

	private UriComponentsBuilder buildApiUri() {
		String protocol = StringUtils.isBlank(settings.getProtocol()) ? DEFAULT_PROTOCOL : settings.getProtocol();
		String host = StringUtils.isBlank(settings.getHost()) ? DEFAULT_HOST : settings.getHost();
		
		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
		
		if(StringUtils.isNotBlank(settings.getPort())) {
			builder.port(settings.getPort());
		}
		
		return builder.scheme(protocol)
				.host(host)
				.path(settings.getPath())
				.pathSegment(API_PATH_SEGMENT)
				.pathSegment(VERSION_PATH_SEGMENT)
				.queryParam(RESULT_PER_PAGE_QUERY_PARAM_KEY, RESULTS_PER_PAGE);
	}

}
