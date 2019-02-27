package com.capitalone.dashboard.gitlab;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.GitlabGitRepo;

/**
 * Created by benathmane on 23/06/16.
 */

/**
 * Client for fetching commit history from Gitlab
 */
public interface GitlabGitClient {

	/**
	 * Fetch all of the commits.
	 *
	 * @param repo GitlabGitRepo
	 * @return all commits in repo
	 */
	List<Commit> getCommits(GitlabGitRepo repo, boolean firstRun);

	/**
	 * Fetch all of the issues.
	 *
	 * @param repo GitlabGitRepo
	 * @return all issues in repo
	 */
	List<GitRequest> getIssues(GitlabGitRepo repo, boolean firstRun) throws MalformedURLException, HygieiaException;

	/**
	 * Fetch all of the merge requests.
	 *
	 * @param repo       GitlabGitRepo
	 * @param status     merge requests' status
	 * @param firstRun   isFirstRun
	 * @param mrCloseMap all existing MRs
	 * @return all merge requests in repo
	 */
	List<GitRequest> getMergeRequests(GitlabGitRepo repo, String status, boolean firstRun, Map<Long, String> mrCloseMap)
			throws MalformedURLException, HygieiaException;

}
