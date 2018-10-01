package com.capitalone.dashboard.collector;

import java.util.List;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitRepo;
import com.capitalone.dashboard.model.pullrequest.PullRequest;

/**
 * Client for fetching commit history from Git
 */
public interface GitClient {

    /**
     * Fetch all of the commits for the provided GitRepo.
     *
     * @param repo git repo
     * @param firstRun
     * @return all commits in repo
     */

    List<Commit> getCommits(GitRepo repo, boolean firstRun);

    /**
     * Fetch all of the pull requests for the provided GitRepo.
     *
     * @param repo     git repo
     * @param firstRun
     * @return all pull requests in repo
     */
    List<PullRequest> getPullRequests(GitRepo repo, boolean firstRun);

    /**
     * Fetch all of the merged pull requests for the provided GitRepo.
     *
     * @param repo git repo
     * @return all merged pull requests ids in repo
     */
    List<Long> getMergedPullRequests(GitRepo repo);

}
