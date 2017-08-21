package com.capitalone.dashboard.gitlab;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.capitalone.dashboard.collector.FeatureSettings;
import com.capitalone.dashboard.model.Project;

@Component
public class GitlabUriUtility {
	
	private static final String DEFAULT_PROTOCOL = "http";
	private static final String DEFAULT_HOST = "gitlab.com";
	private static final String API_PATH_SEGMENT = "api";
    private static final String V3 = "v3";
    private static final String V4 = "v4";
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
	
    public URI buildProjectsForTeamUri(String teamName) {
        UriComponentsBuilder builder = buildApiUri();
        URI uri = builder.pathSegment(GROUPS_PATH_SEGMENT).pathSegment(teamName).pathSegment(PROJECTS_PATH_SEGMENT).build().toUri();
        return uri;
    }
	
	public URI buildBoardsUri(Project project) {
		UriComponentsBuilder builder = buildApiUri();
		URI uri = builder.pathSegment(PROJECTS_PATH_SEGMENT).pathSegment(buildGitlabProjectId(project)).pathSegment(BOARDS_PATH_SEGMENT).build(true).toUri();
		return uri;
	}
	
    public URI buildIssuesForProjectUri(Project project) {
		UriComponentsBuilder builder = buildApiUri();
		URI uri = builder.pathSegment(PROJECTS_PATH_SEGMENT).pathSegment(buildGitlabProjectId(project)).pathSegment(ISSUES_PATH_SEGMENT).build(true).toUri();
		return uri;
	}
	
	public HttpEntity<String> buildAuthenticationHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.set(PRIVATE_TOKEN_HEADER_KEY, settings.getApiToken());
		HttpEntity<String> headersEntity = new HttpEntity<>(headers);
		return headersEntity;
	}

	private UriComponentsBuilder buildApiUri() {
		String protocol = getProtocol();
		String host = getHost();
		String apiVersion = getApiVersion();
		
		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
		
		if(StringUtils.isNotBlank(settings.getPort())) {
			builder.port(settings.getPort());
		}
		
		return builder.scheme(protocol)
				.host(host)
				.path(settings.getPath())
				.pathSegment(API_PATH_SEGMENT)
				.pathSegment(apiVersion)
				.queryParam(RESULT_PER_PAGE_QUERY_PARAM_KEY, RESULTS_PER_PAGE);
	}

    private String getApiVersion() {
        return settings.getApiVersion() == 3 ? V3 : V4;
    }

    private String getHost() {
        return StringUtils.isBlank(settings.getHost()) ? DEFAULT_HOST : settings.getHost();
    }

    private String getProtocol() {
        return StringUtils.isBlank(settings.getProtocol()) ? DEFAULT_PROTOCOL : settings.getProtocol();
    }
    
    private String buildGitlabProjectId(Project project) {
        String projectId = project.getTeamId() + "/" + project.getProjectId();
        String result;
        try {
            result = URLEncoder.encode(projectId, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            result = StringUtils.replace(projectId, "/", "%2F"); 
        }
        
        return result;
    }

}
