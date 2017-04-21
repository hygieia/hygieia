package com.capitalone.dashboard.gitlab;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.collector.GitlabSettings;
import com.capitalone.dashboard.gitlab.model.GitlabCommit;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitlabGitRepo;
import com.capitalone.dashboard.util.Supplier;

/**
 * Created by benathmane on 23/06/16.
 */

@Component
public class DefaultGitlabGitClient implements  GitlabGitClient {

    //Gitlab max results per page. Reduces amount of network calls.
    private static final int RESULTS_PER_PAGE = 100;
    
    private final RestOperations restOperations;
    private final GitlabUrlUtility gitlabUrlUtility;
    private final GitlabSettings gitlabSettings;
    private final GitlabCommitsResponseMapper responseMapper;
    
    @Autowired
    public DefaultGitlabGitClient(GitlabUrlUtility gitlabUrlUtility, 
    								   GitlabSettings gitlabSettings,
                                       Supplier<RestOperations> restOperationsSupplier,
                                       GitlabCommitsResponseMapper responseMapper) {
        this.gitlabUrlUtility = gitlabUrlUtility;
        this.gitlabSettings = gitlabSettings;
        this.restOperations = restOperationsSupplier.get();
        this.responseMapper = responseMapper;
    }

    @Override
	public List<Commit> getCommits(GitlabGitRepo repo, boolean firstRun) {
        List<Commit> commits = new ArrayList<>();

		URI apiUrl = gitlabUrlUtility.buildApiUrl(repo, firstRun, RESULTS_PER_PAGE);
		String providedApiToken = repo.getUserId();
		String apiToken = (StringUtils.isNotBlank(providedApiToken)) ? providedApiToken:gitlabSettings.getApiToken();

		boolean hasMorePages = true;
		int nextPage = 1;
		while (hasMorePages) {
			ResponseEntity<GitlabCommit[]> response = makeRestCall(apiUrl, apiToken);
			List<Commit> pageOfCommits = responseMapper.map(response.getBody(), repo.getRepoUrl(), repo.getBranch());
			commits.addAll(pageOfCommits);
			if (pageOfCommits.size() < RESULTS_PER_PAGE) {
				hasMorePages = false;
				continue;
			}
			apiUrl = gitlabUrlUtility.updatePage(apiUrl, nextPage);
			nextPage++;
		}

        return commits;
    }

	private ResponseEntity<GitlabCommit[]> makeRestCall(URI url, String apiToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("PRIVATE-TOKEN", apiToken);
		return restOperations.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), GitlabCommit[].class);
	}

}
