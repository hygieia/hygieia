package com.capitalone.dashboard.gitlab;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.capitalone.dashboard.collector.GitlabSettings;
import com.capitalone.dashboard.gitlab.model.GitlabCommit;
import com.capitalone.dashboard.gitlab.model.GitlabIssue;
import com.capitalone.dashboard.gitlab.model.GitlabRequest;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.GitlabGitRepo;
import com.capitalone.dashboard.util.Supplier;

/**
 * Created by benathmane on 23/06/16.
 */

@Component
public class DefaultGitlabGitClient implements GitlabGitClient {

	
    private static final Log LOG = LogFactory.getLog(DefaultGitlabGitClient.class);

	private final RestOperations restOperations;
	private final GitlabUrlUtility gitlabUrlUtility;
	private final GitlabSettings gitlabSettings;
	private final GitlabCommitsResponseMapper commitsResponseMapper;
	private final GitlabIssuesResponseMapper issuesResponseMapper;
	private final GitlabRequestsResponseMapper requestsResponseMapper;

	@Autowired
    public DefaultGitlabGitClient(GitlabUrlUtility gitlabUrlUtility, 
    								   GitlabSettings gitlabSettings,
                                       Supplier<RestOperations> restOperationsSupplier,
                                       GitlabCommitsResponseMapper commitsResponseMapper,
                                       GitlabIssuesResponseMapper issuesResponseMapper, 
                                       GitlabRequestsResponseMapper requestsResponseMapper) {
		this.gitlabUrlUtility = gitlabUrlUtility;
		this.gitlabSettings = gitlabSettings;
		this.restOperations = restOperationsSupplier.get();

		this.commitsResponseMapper = commitsResponseMapper;

		this.issuesResponseMapper = issuesResponseMapper;
		this.issuesResponseMapper.init(this.gitlabUrlUtility);

		this.requestsResponseMapper = requestsResponseMapper;
		this.requestsResponseMapper.init(this.gitlabUrlUtility, this.restOperations);
	}

	@Override
	public List<Commit> getCommits(GitlabGitRepo repo, boolean firstRun) {
		List<Commit> commits = new ArrayList<>();

		URI apiUrl = gitlabUrlUtility.buildCommitsApiUrl(repo, firstRun, GitlabUrlUtility.RESULTS_PER_PAGE);
		String providedApiToken = repo.getUserId();
		String apiToken = (StringUtils.isNotBlank(providedApiToken)) ? providedApiToken : gitlabSettings.getApiToken();

		boolean hasMorePages = true;
		int nextPage = 1;
		while (hasMorePages) {
			ResponseEntity<GitlabCommit[]> response = makeCommitRestCall(apiUrl, apiToken);
			LOG.info("page " + nextPage + ": " + response.getStatusCode());
			List<Commit> pageOfCommits = commitsResponseMapper.map(response.getBody(), repo.getRepoUrl(),
					repo.getBranch());
			commits.addAll(pageOfCommits);
			if (pageOfCommits.size() < GitlabUrlUtility.RESULTS_PER_PAGE) {
				hasMorePages = false;
				continue;
			}
			apiUrl = gitlabUrlUtility.updatePage(apiUrl, nextPage);
			nextPage++;
		}

		return commits;
	}

	@Override
	public List<GitRequest> getIssues(GitlabGitRepo repo, boolean firstRun)
			throws MalformedURLException, HygieiaException {
		List<GitRequest> issues = new ArrayList<>();

		URI apiUrl = gitlabUrlUtility.buildIssuesApiUrl(repo, firstRun, GitlabUrlUtility.RESULTS_PER_PAGE);
		String providedApiToken = repo.getUserId();
		String apiToken = (StringUtils.isNotBlank(providedApiToken)) ? providedApiToken : gitlabSettings.getApiToken();

		boolean hasMorePages = true;
		int nextPage = 1;
		while (hasMorePages) {
			ResponseEntity<GitlabIssue[]> response = makeIssueRestCall(apiUrl, apiToken);
			List<GitRequest> pageOfIssues = issuesResponseMapper.map(response.getBody(), repo.getRepoUrl());
			issues.addAll(pageOfIssues);
			if (pageOfIssues.size() < GitlabUrlUtility.RESULTS_PER_PAGE) {
				hasMorePages = false;
				continue;
			}
			apiUrl = gitlabUrlUtility.updatePage(apiUrl, nextPage);
			nextPage++;
		}

		return issues;
	}

	@Override
	public List<GitRequest> getMergeRequests(GitlabGitRepo repo, String status, boolean firstRun,
			Map<Long, String> mrCloseMap) throws MalformedURLException, HygieiaException {
		List<GitRequest> mergeRequests = new ArrayList<>();

		URI apiUrl = gitlabUrlUtility.buildMergeRequestsApiUrl(repo, status, firstRun, GitlabUrlUtility.RESULTS_PER_PAGE);
		String providedApiToken = repo.getUserId();
		String apiToken = (StringUtils.isNotBlank(providedApiToken)) ? providedApiToken : gitlabSettings.getApiToken();

		boolean hasMorePages = true;
		int nextPage = 1;
		while (hasMorePages) {
			ResponseEntity<GitlabRequest[]> response = makeRequestRestCall(apiUrl, apiToken);
			List<GitRequest> pageOfRequests = requestsResponseMapper.map(response.getBody(), repo.getRepoUrl(),
					repo.getBranch(), apiToken, mrCloseMap);
			mergeRequests.addAll(pageOfRequests);
			if (pageOfRequests.size() < GitlabUrlUtility.RESULTS_PER_PAGE) {
				hasMorePages = false;
				continue;
			}
			apiUrl = gitlabUrlUtility.updatePage(apiUrl, nextPage);
			nextPage++;
		}

		return mergeRequests;
	}

	private ResponseEntity<GitlabCommit[]> makeCommitRestCall(URI url, String apiToken) {
		return restOperations.exchange(url, HttpMethod.GET, 
		        new HttpEntity<>(gitlabUrlUtility.createHttpHeaders(apiToken)), GitlabCommit[].class);
	}

	private ResponseEntity<GitlabIssue[]> makeIssueRestCall(URI url, String apiToken) {
		return restOperations.exchange(url, HttpMethod.GET, 
		        new HttpEntity<>(gitlabUrlUtility.createHttpHeaders(apiToken)), GitlabIssue[].class);
	}

	private ResponseEntity<GitlabRequest[]> makeRequestRestCall(URI url, String apiToken) {
		return restOperations.exchange(url, HttpMethod.GET, 
		        new HttpEntity<>(gitlabUrlUtility.createHttpHeaders(apiToken)), GitlabRequest[].class);
	}

}
