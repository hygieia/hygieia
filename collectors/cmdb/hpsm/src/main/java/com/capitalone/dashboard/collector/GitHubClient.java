package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitHubRepo;

import java.util.List;

/**
 * Client for fetching commit history from GitHub
 */
public interface GitHubClient {

    /**
     * Fetch all of the commits for the provided SubversionRepo.
     *
     * @param repo SubversionRepo
     * @param firstRun
     * @param startRevision starting revision number
     * @return all commits in repo
     */

	List<Commit> getCommits(GitHubRepo repo, boolean firstRun);

}
