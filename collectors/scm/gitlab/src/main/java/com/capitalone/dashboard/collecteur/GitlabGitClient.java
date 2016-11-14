package com.capitalone.dashboard.collecteur;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitlabGitRepo;

import java.util.List;

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

    List<Commit> getCommits(GitlabGitRepo repo);
}
