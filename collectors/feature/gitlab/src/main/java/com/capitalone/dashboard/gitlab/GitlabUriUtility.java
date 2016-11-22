package com.capitalone.dashboard.gitlab;

import java.net.URI;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GitlabUriUtility {
	
	private static final String SCHEME = "https";
	private static final String API_PATH_SEGMENT = "api";
	private static final String VERSION_PATH_SEGMENT = "v3";
	private static final String ISSUES_PATH_SEGMENT = "issues";
	private static final String BOARDS_PATH_SEGMENT = "boards";
	private static final String PROJECTS_PATH_SEGMENT = "projects";
	private static final String GROUPS_PATH_SEGMENT = "groups";
	private static final String PAGE_QUERY_PARAM_KEY = "page";
	private static final String RESULT_PER_PAGE_QUERY_PARAM_KEY = "per_page";
	private static final String RESULTS_PER_PAGE = "100";
	
	public URI updatePage(URI uri, String page) {
		return UriComponentsBuilder.fromUri(uri).queryParam(PAGE_QUERY_PARAM_KEY, page).build(true).toUri();
	}

	public URI buildTeamsUri(String host) {
		UriComponentsBuilder builder = buildApiUri(host);
		URI uri = builder.pathSegment(GROUPS_PATH_SEGMENT)
						.build()
						.toUri();
		return uri;
	}
	
	public URI buildProjectsUri(String host, String teamId) {
		UriComponentsBuilder builder = buildApiUri(host);
		URI uri = builder.pathSegment(GROUPS_PATH_SEGMENT).pathSegment(teamId).pathSegment(PROJECTS_PATH_SEGMENT).build().toUri();
		return uri;
	}
	
	public URI buildBoardsUri(String host, String projectId) {
		UriComponentsBuilder builder = buildApiUri(host);
		URI uri = builder.pathSegment(PROJECTS_PATH_SEGMENT).pathSegment(projectId).pathSegment(BOARDS_PATH_SEGMENT).build().toUri();
		return uri;
	}
	
	public URI buildIssuesForProjectUri(String host, String projectId) {
		UriComponentsBuilder builder = buildApiUri(host);
		URI uri = builder.pathSegment(PROJECTS_PATH_SEGMENT).pathSegment(projectId).pathSegment(ISSUES_PATH_SEGMENT).build().toUri();
		return uri;
	}

	private UriComponentsBuilder buildApiUri(String host) {
		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
		return builder.scheme(SCHEME).host(host).pathSegment(API_PATH_SEGMENT).pathSegment(VERSION_PATH_SEGMENT).queryParam(RESULT_PER_PAGE_QUERY_PARAM_KEY, RESULTS_PER_PAGE);
	}

}
