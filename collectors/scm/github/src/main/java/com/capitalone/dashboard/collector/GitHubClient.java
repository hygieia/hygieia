package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitHubRepo;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.repository.GitRequestRepository;

import java.util.List;

/**
 * Client for fetching commit history from GitHub
 */
public interface GitHubClient {

    /**
     * Fetch all of the commits for the provided SubversionRepo.
     *
     * @param repo Github repo
     * @param firstRun boolean true if first time running
     * @return all commits in repo
     */

	List<Commit> getCommits(GitHubRepo repo, boolean firstRun);

    /**
     * Fetch all of the commits for the provided SubversionRepo.
     *
     * @param repo SubversionRepo
     * @param firstRun
     * @param startRevision starting revision number
     * @return all commits in repo
     */

    List<GitRequest> getPulls(GitHubRepo repo, boolean firstRun, GitRequestRepository pullRepository);
    /**
     * Fetch all of the commits for the provided SubversionRepo.
     *
     * @param repo SubversionRepo
     * @param firstRun
     * @param startRevision starting revision number
     * @return all commits in repo
     */

    List<GitRequest> getIssues(GitHubRepo repo, boolean firstRun, GitRequestRepository pullRepository);

}
