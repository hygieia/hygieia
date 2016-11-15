package com.capitalone.dashboard.utilities;

import java.net.URI;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GitlabUrlUtility {
	
	private static final String RESULTS_PER_PAGE = "100";
	
	public URI updatePage(URI uri, String page) {
		return UriComponentsBuilder.fromUri(uri).queryParam("page", page).build(true).toUri();
	}

	public URI buildTeamUri(String host) {
		UriComponentsBuilder builder = buildApiUri(host);
		URI uri = builder.path("groups")
						.build(true)
						.toUri();
		return uri;
	}
	
	public URI buildProjectsUrl(String host, String teamId) {
		UriComponentsBuilder builder = buildApiUri(host);
		URI uri = builder.path("groups/").path(teamId).path("/projects").build(true).toUri();
		return uri;
	}
	
	public URI buildBoardsUrl(String host, String projectId) {
		UriComponentsBuilder builder = buildApiUri(host);
		URI uri = builder.path("projects/").path(projectId).path("/boards").build(true).toUri();
		return uri;
	}
	
	public URI buildIssuesForLabelsUrl(String host, String projectId, List<String> labels) {
		UriComponentsBuilder builder = buildApiUri(host);
		String labelsParam = StringUtils.join(labels, ',');
		URI uri = builder.path("projects/").path(projectId).path("/issues").queryParam("labels", labelsParam).build().toUri();
		return uri;
	}

	private UriComponentsBuilder buildApiUri(String host) {
		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
		return builder.scheme("https").host(host).path("api/v3/").queryParam("per_page", RESULTS_PER_PAGE);
	}

}
