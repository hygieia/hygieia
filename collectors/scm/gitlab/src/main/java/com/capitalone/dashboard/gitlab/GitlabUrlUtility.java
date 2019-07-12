package com.capitalone.dashboard.gitlab;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.capitalone.dashboard.collector.GitlabSettings;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.GitlabGitRepo;

@Component
public class GitlabUrlUtility {

	// GitLab max results per page. Reduces amount of network calls.
	public static final int RESULTS_PER_PAGE = 100;

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
	private static final String ISSUES_API = "/issues/";
	private static final String MERGE_REQUESTS_API = "/merge_requests/";
	private static final String SCOPE_QUERY_PARAM_KEY = "scope";
	private static final String STATE_QUERY_PARAM_KEY = "state";
	private static final String CREATED_AFTER_QUERY_PARAM_KEY = "created_after";
	private static final String ORDER_BY_QUERY_PARAM_KEY = "order_by";
	private static final String SORT_QUERY_PARAM_KEY = "sort";
	private static final String NOTES_REQUESTS_API = "/notes/";
	private static final String COMMITS_REQUESTS_API = "/commits/";
	private static final String CHANGES_REQUESTS_API = "/changes/";
	private static final String STATUSES_REQUESTS_API = "/statuses/";
	private static final String REF_QUERY_PARAM_KEY = "ref";
	private static final String PER_PAGE_QUERY_PARAM_KEY = "per_page";
	private static final String PUBLIC_GITLAB_HOST_NAME = "gitlab.com";
	private static final int FIRST_RUN_HISTORY_DEFAULT = 14;

	@Autowired
	public GitlabUrlUtility(GitlabSettings gitlabSettings) {
		this.gitlabSettings = gitlabSettings;
	}

	public URI buildCommitsApiUrl(GitlabGitRepo repo, boolean firstRun, int resultsPerPage) {
		String repoUrl = repo.getRepoUrl();
        if (repoUrl.endsWith(GIT_EXTENSION)) {
            repoUrl = StringUtils.removeEnd(repoUrl, GIT_EXTENSION);
        }
        
        String apiVersion = getApiVersion();
        String protocol = getProtocol();
		String repoName = getRepoName(repoUrl);
		String host = getRepoHost();
		String date = getCreatedAfterDate(repo, firstRun);

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

		LOG.info("---> Gitlab commits URI: " + uri.toString());
		return uri;
    }

    public URI buildIssuesApiUrl(GitlabGitRepo repo, boolean firstRun, int resultsPerPage) {
        String repoUrl = repo.getRepoUrl();
        if (repoUrl.endsWith(GIT_EXTENSION)) {
            repoUrl = StringUtils.removeEnd(repoUrl, GIT_EXTENSION);
        }

        String apiVersion = getApiVersion();
        String protocol = getProtocol();
        String repoName = getRepoName(repoUrl);
        String host = getRepoHost();
        String date = getCreatedAfterDate(repo, firstRun);

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        if (StringUtils.isNotBlank(gitlabSettings.getPort())) {
            builder.port(gitlabSettings.getPort());
        }

        URI uri = builder.scheme(protocol)
                .host(host)
                .path(gitlabSettings.getPath())
                .pathSegment(SEGMENT_API)
                .pathSegment(apiVersion)
                .pathSegment(PROJECTS_SEGMENT)
                .path(repoName)
                .path(ISSUES_API)
                .queryParam(SCOPE_QUERY_PARAM_KEY, "all")
                .queryParam(CREATED_AFTER_QUERY_PARAM_KEY, date)
                .queryParam(PER_PAGE_QUERY_PARAM_KEY, resultsPerPage)
                .queryParam(ORDER_BY_QUERY_PARAM_KEY, "updated_at")
                .queryParam(SORT_QUERY_PARAM_KEY, "desc")
                .build(true).toUri();

        LOG.info("---> Gitlab issues URI: " + uri.toString());
        return uri;
    }

    public URI buildMergeRequestsApiUrl(GitlabGitRepo repo, String status, boolean firstRun, int resultsPerPage) {
        String repoUrl = repo.getRepoUrl();
        if (repoUrl.endsWith(GIT_EXTENSION)) {
            repoUrl = StringUtils.removeEnd(repoUrl, GIT_EXTENSION);
        }

        String apiVersion = getApiVersion();
        String protocol = getProtocol();
        String repoName = getRepoName(repoUrl);
        String host = getRepoHost();
        String date = getCreatedAfterDate(repo, firstRun);

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        if (StringUtils.isNotBlank(gitlabSettings.getPort())) {
            builder.port(gitlabSettings.getPort());
        }

        URI uri = builder.scheme(protocol)
                .host(host)
                .path(gitlabSettings.getPath())
                .pathSegment(SEGMENT_API)
                .pathSegment(apiVersion)
                .pathSegment(PROJECTS_SEGMENT)
                .path(repoName)
                .path(MERGE_REQUESTS_API)
                .queryParam(STATE_QUERY_PARAM_KEY, status)
                .queryParam(CREATED_AFTER_QUERY_PARAM_KEY, date)
                .queryParam(PER_PAGE_QUERY_PARAM_KEY, resultsPerPage)
                .queryParam(SCOPE_QUERY_PARAM_KEY, "all")
                .queryParam(ORDER_BY_QUERY_PARAM_KEY, "updated_at")
                .queryParam(SORT_QUERY_PARAM_KEY, "desc")
                .build(true).toUri();

        LOG.info("---> Gitlab merge requests URI: " + uri.toString());
        return uri;
    }

    public URI buildMergeRequestNotesApiUrl(String webUrl, String mergeRequestIid, int resultsPerPage) {
        String apiVersion = getApiVersion();
        String protocol = getProtocol();
        String repoName = getRepoName(webUrl);
        String host = getRepoHost();

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        if (StringUtils.isNotBlank(gitlabSettings.getPort())) {
            builder.port(gitlabSettings.getPort());
        }

        URI uri = builder.scheme(protocol)
                .host(host)
                .path(gitlabSettings.getPath())
                .pathSegment(SEGMENT_API)
                .pathSegment(apiVersion)
                .pathSegment(PROJECTS_SEGMENT)
                .path(repoName)
                .path(MERGE_REQUESTS_API)
                .path(mergeRequestIid)
                .path(NOTES_REQUESTS_API)
                .queryParam(PER_PAGE_QUERY_PARAM_KEY, resultsPerPage)
                .queryParam(ORDER_BY_QUERY_PARAM_KEY, "updated_at")
                .queryParam(SORT_QUERY_PARAM_KEY, "desc")
                .build(true).toUri();

        LOG.info("---> Gitlab merge request notes URI: " + uri.toString());
        return uri;
    }

    public URI buildMergeRequestCommitsApiUrl(String webUrl, String mergeRequestIid, int resultsPerPage) {
        String apiVersion = getApiVersion();
        String protocol = getProtocol();
        String repoName = getRepoName(webUrl);
        String host = getRepoHost();

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        if (StringUtils.isNotBlank(gitlabSettings.getPort())) {
            builder.port(gitlabSettings.getPort());
        }

        URI uri = builder.scheme(protocol)
                .host(host)
                .path(gitlabSettings.getPath())
                .pathSegment(SEGMENT_API)
                .pathSegment(apiVersion)
                .pathSegment(PROJECTS_SEGMENT)
                .path(repoName)
                .path(MERGE_REQUESTS_API)
                .path(mergeRequestIid)
                .path(COMMITS_REQUESTS_API)
                .queryParam(PER_PAGE_QUERY_PARAM_KEY, resultsPerPage)
                .queryParam(ORDER_BY_QUERY_PARAM_KEY, "updated_at")
                .queryParam(SORT_QUERY_PARAM_KEY, "desc")
                .build(true).toUri();

        LOG.info("---> Gitlab merge request commits URI: " + uri.toString());
        return uri;
    }

    public URI buildMergeRequestChangesApiUrl(String webUrl, String mergeRequestIid) {
        String apiVersion = getApiVersion();
        String protocol = getProtocol();
        String repoName = getRepoName(webUrl);
        String host = getRepoHost();

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        if (StringUtils.isNotBlank(gitlabSettings.getPort())) {
            builder.port(gitlabSettings.getPort());
        }

        URI uri = builder.scheme(protocol)
                .host(host)
                .path(gitlabSettings.getPath())
                .pathSegment(SEGMENT_API)
                .pathSegment(apiVersion)
                .pathSegment(PROJECTS_SEGMENT)
                .path(repoName)
                .path(MERGE_REQUESTS_API)
                .path(mergeRequestIid)
                .path(CHANGES_REQUESTS_API)
                .build(true).toUri();

        LOG.info("---> Gitlab merge request changes URI: " + uri.toString());
        return uri;
    }

    public URI buildCommitStatusesApiUrl(String webUrl, String branch, String commitSha, int resultsPerPage) {
        String apiVersion = getApiVersion();
        String protocol = getProtocol();
        String repoName = getRepoName(webUrl);
        String host = getRepoHost();

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        if (StringUtils.isNotBlank(gitlabSettings.getPort())) {
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
                .path(commitSha)
                .path(STATUSES_REQUESTS_API)
                .queryParam(PER_PAGE_QUERY_PARAM_KEY, resultsPerPage)
                .queryParam(REF_QUERY_PARAM_KEY, branch)
                .build(true).toUri();

        LOG.info("---> Gitlab commit statuses URI: " + uri.toString());
        return uri;
    }

    public URI buildSingleCommitApiUrl(String webUrl, String commitSha) {
        String apiVersion = getApiVersion();
        String protocol = getProtocol();
        String repoName = getRepoName(webUrl);
        String host = getRepoHost();

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        if (StringUtils.isNotBlank(gitlabSettings.getPort())) {
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
                .path(commitSha)
                .build(true).toUri();

        LOG.info("---> Gitlab single commit URI: " + uri.toString());
        return uri;
    }

    public String[] getOrgAndRepoName(String gitlabRepoUrl) throws HygieiaException {
        String[] orgAndRepoName = new String[2];

        try {
            String repoUrl = gitlabRepoUrl;
            if (repoUrl.endsWith(GIT_EXTENSION)) {
                repoUrl = repoUrl.substring(0, repoUrl.lastIndexOf(GIT_EXTENSION));
            }

            String path = repoUrl;

            // SSH
            if (repoUrl.startsWith("git@")) {
                path = repoUrl.substring(repoUrl.indexOf(":"));

                // HTTP
            } else {
                URL u = new URL(repoUrl);
                path = u.getFile();
            }

            path = path.substring(1);
            String[] parts = path.split("/");
            if ((parts == null) || (parts.length < 2)) {
                throw new HygieiaException("Bad gitlab repo URL: " + repoUrl, HygieiaException.BAD_DATA);
            }
            orgAndRepoName = parts;

        } catch (MalformedURLException e) {
            LOG.error(e.getMessage(), e);
            throw new HygieiaException(e.getMessage(), HygieiaException.BAD_DATA);
        }

        return orgAndRepoName;
    }

	private String getProtocol() {
		return StringUtils.isBlank(gitlabSettings.getProtocol()) ? DEFAULT_PROTOCOL : gitlabSettings.getProtocol();
	}

	private String getApiVersion() {
		return gitlabSettings.getApiVersion() == 3 ? V3 : V4;
	}

	public URI updatePage(URI uri, int nextPage) {
		URI ret = UriComponentsBuilder.fromUri(uri).replaceQueryParam("page", nextPage).build(true).toUri();
		LOG.info("Paging - Gitlab URI updated to: " + ret.toString());
		return ret;
	}

    public long toLong(String value) {
        try {
            if (value != null) {
                return Long.parseLong(value);
            }
        } catch (NumberFormatException ex) {
            LOG.error("Invalid number format: " + ex.getMessage());
        }
        return 0;
    }

    public HttpHeaders createHttpHeaders(String apiToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("PRIVATE-TOKEN", apiToken);
        return headers;
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
			LOG.error(e.getMessage(), e);
		}
		repoName = StringUtils.removeStart(repoName, "/");
		String[] urlParts = repoName.split("/");
		repoName = urlParts[0] + "%2F" + urlParts[1];
		return repoName;
	}

    private String getCreatedAfterDate(GitlabGitRepo repo, boolean firstRun) {
        Date createdAfterDate;

        if (firstRun) {
            // first run, fetch the data from settings or default days ago
            int firstRunHistoryDays = gitlabSettings.getFirstRunHistoryDays();
            if (firstRunHistoryDays > 0) {
                createdAfterDate = getDate(new Date(), -firstRunHistoryDays, 0);
            } else {
                createdAfterDate = getDate(new Date(), -FIRST_RUN_HISTORY_DEFAULT, 0);
            }
        } else {
            // not first run, fetch the data from 10 minutes ago of last updated date
            createdAfterDate = getDate(new Date(repo.getLastUpdated()), 0, -10);
        }

        return DateTimeFormatter.ISO_INSTANT.format(createdAfterDate.toInstant());
    }

	private Date getDate(Date dateInstance, int offsetDays, int offsetMinutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateInstance);
		cal.add(Calendar.DATE, offsetDays);
		cal.add(Calendar.MINUTE, offsetMinutes);
		return cal.getTime();
	}

}
