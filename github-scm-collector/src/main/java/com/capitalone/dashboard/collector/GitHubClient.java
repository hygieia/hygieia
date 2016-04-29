package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.*;

import java.util.List;

/**
 * Client for fetching commit history from GitHub
 */
public interface GitHubClient {

    /**
     * Fetch all of the commits for the provided Git.
     *
     * @param repo SubversionRepo
     * @param firstRun
     * @return all commits in repo
     */

	List<Commit> getCommits(GitHubRepo repo, boolean firstRun);

    /**
     * Fetch all of the commits for the provided Git.
     *
     * @param org GitHubRepo
     * @param firstRun
     * @return all commits in repo
     */

    List<GitRepoData> getRepoNames(GitHubOrg org, boolean firstRun);

}
