package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitHubRepo;
import com.capitalone.dashboard.model.Issue;
import com.capitalone.dashboard.model.Pull;
import com.capitalone.dashboard.repository.IssueRepository;
import com.capitalone.dashboard.repository.PullRepository;


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
     * @param repo GitHubRepo
     * @param firstRun
     * @param pullRepository
     * @return all commits in repo
     */

    List<Pull> getPulls(GitHubRepo repo, boolean firstRun, PullRepository pullRepository);

    /**
     * Fetch all of the issues for the provided Git.
     *
     * @param repo SubversionRepo
     * @param firstRun
     * @param issueRepository
     * @return all commits in repo
     */

    List<Issue> getIssues(GitHubRepo repo, boolean firstRun, IssueRepository issueRepository);
}
