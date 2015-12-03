package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitRepo;

import java.util.List;

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

}
