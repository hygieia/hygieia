package com.capitalone.dashboard.utilities;

import java.net.URI;

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

	private UriComponentsBuilder buildApiUri(String host) {
		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
		return builder.scheme("https").host(host).path("api/v3/").queryParam("per_page", RESULTS_PER_PAGE);
	}

}
