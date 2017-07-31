package com.capitalone.dashboard.gitlab;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.capitalone.dashboard.collector.GitlabSettings;
import com.capitalone.dashboard.model.GitlabGitRepo;

@Component
public class GitlabUrlUtility {
	
	private static final Log LOG = LogFactory.getLog(GitlabUrlUtility.class);
	
	private GitlabSettings gitlabSettings;
	
	private static final String GIT_EXTENSION = ".git";
	private static final String DEFAULT_PROTOCOL = "http";
    private static final String SEGMENT_API = "api";
    private static final String V3 = "v3";
    private static final String V4 = "v4";
    private static final String PROJECTS_SEGMENT = "projects";
	private static final String COMMITS_API = "/repository/commits/";
	private static final String DATE_QUERY_PARAM_KEY = "since";
	private static final String BRANCH_QUERY_PARAM_KEY = "ref_name";
	private static final String PER_PAGE_QUERY_PARAM_KEY = "per_page";
    private static final String PUBLIC_GITLAB_HOST_NAME = "gitlab.com";
	private static final int FIRST_RUN_HISTORY_DEFAULT = 14;
	
	@Autowired
	public GitlabUrlUtility(GitlabSettings gitlabSettings) {
		this.gitlabSettings = gitlabSettings;
	}
	
	public URI buildApiUrl(GitlabGitRepo repo, boolean firstRun, int resultsPerPage) {
		String repoUrl = repo.getRepoUrl();
        if (repoUrl.endsWith(GIT_EXTENSION)) {
            repoUrl = StringUtils.removeEnd(repoUrl, GIT_EXTENSION);
        }
        
        String apiVersion = getApiVersion();
        String protocol = getProtocol();
		String repoName = getRepoName(repoUrl);
		String host = getRepoHost();
		String date = getDateForCommits(repo, firstRun);

		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
		
		if(StringUtils.isNotBlank(gitlabSettings.getPort())) {
		    builder.port(gitlabSettings.getPort());
		}
		
		URI uri = builder.scheme(protocol)
				.host(host)
				.path(gitlabSettings.getPath())
				.pathSegment(SEGMENT_API)
				.pathSegment(apiVersion)
				.pathSegment(PROJECTS_SEGMENT)
				.path(repoName)
				.path(COMMITS_API)
				.queryParam(BRANCH_QUERY_PARAM_KEY, repo.getBranch())
				.queryParam(DATE_QUERY_PARAM_KEY, date)
				.queryParam(PER_PAGE_QUERY_PARAM_KEY, resultsPerPage)
				.build(true).toUri();

		return uri;
    }

    private String getProtocol() {
        return StringUtils.isBlank(gitlabSettings.getProtocol()) ? DEFAULT_PROTOCOL : gitlabSettings.getProtocol();
    }

    private String getApiVersion() {
        return gitlabSettings.getApiVersion() == 3 ? V3 : V4;
    }

	public URI updatePage(URI uri, int nextPage) {
		return UriComponentsBuilder.fromUri(uri).replaceQueryParam("page", nextPage).build(true).toUri();
	}

	private String getRepoHost() {
		String providedGitLabHost = gitlabSettings.getHost();
		String apiHost;
		if (StringUtils.isBlank(providedGitLabHost)) {
			apiHost = PUBLIC_GITLAB_HOST_NAME;
		} else {
			apiHost = providedGitLabHost;
		}
		return apiHost;
	}

	private String getRepoName(String repoUrl) {
		String repoName = "";
		try {
			URL url = new URL(repoUrl);
			repoName = url.getFile();
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage());
		}
		repoName = StringUtils.removeStart(repoName, "/");
		repoName = repoName.replace("/", "%2F");
		return repoName;
	}

	private String getDateForCommits(GitlabGitRepo repo, boolean firstRun) {
		Date dt;
		if (firstRun) {
			int firstRunDaysHistory = gitlabSettings.getFirstRunHistoryDays();
			if (firstRunDaysHistory > 0) {
				dt = getDate(new Date(), -firstRunDaysHistory, 0);
			} else {
				dt = getDate(new Date(), -FIRST_RUN_HISTORY_DEFAULT, 0);
			}
		} else {
			dt = getDate(new Date(repo.getLastUpdated()), 0, -10);
		}
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		String thisMoment = df.format(dt);
		return thisMoment;
	}

	private Date getDate(Date dateInstance, int offsetDays, int offsetMinutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateInstance);
		cal.add(Calendar.DATE, offsetDays);
		cal.add(Calendar.MINUTE, offsetMinutes);
		return cal.getTime();
	}

}
