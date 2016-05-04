package com.capitalone.dashboard.collector;




import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitHubRepo;
import com.capitalone.dashboard.model.Issue;
import com.capitalone.dashboard.model.Pull;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.GitHubRepoRepository;
import com.capitalone.dashboard.repository.IssueRepository;
import com.capitalone.dashboard.repository.PullRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CollectorTask that fetches Commit information from GitHub
 */
@org.springframework.stereotype.Component
public class GitHubCollectorTask extends CollectorTask<Collector> {
    private static final Log LOG = LogFactory.getLog(GitHubCollectorTask.class);

    private final BaseCollectorRepository<Collector> collectorRepository;

    private final GitHubRepoRepository gitHubRepoRepository;

    private final CommitRepository commitRepository;
    private final PullRepository pullRepository;
    private final IssueRepository issueRepository;


    private final GitHubClient gitHubClient;
    private final GitHubSettings gitHubSettings;
    private final ComponentRepository dbComponentRepository;

    @Autowired
    public GitHubCollectorTask(TaskScheduler taskScheduler,
                               BaseCollectorRepository<Collector> collectorRepository,
                               GitHubRepoRepository gitHubRepoRepository,
                               CommitRepository commitRepository,
                               PullRepository pullRepository,
                               IssueRepository issueRepository,
                               GitHubClient gitHubClient,
                               GitHubSettings gitHubSettings,
                               ComponentRepository dbComponentRepository) {
        super(taskScheduler, "GitHub");
        this.collectorRepository = collectorRepository;
        this.gitHubRepoRepository = gitHubRepoRepository;
        this.commitRepository = commitRepository;
        this.pullRepository = pullRepository;
        this.issueRepository = issueRepository;

        this.gitHubClient = gitHubClient;
        this.gitHubSettings = gitHubSettings;
        this.dbComponentRepository = dbComponentRepository;
    }

    @Override
    public Collector getCollector() {
        Collector protoType = new Collector();
        protoType.setName("GitHub");
        protoType.setCollectorType(CollectorType.SCM);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        return protoType;
    }

    @Override
    public BaseCollectorRepository<Collector> getCollectorRepository() {
        return collectorRepository;
    }

    @Override
    public String getCron() {
        return gitHubSettings.getCron();
    }

    /**
     * Clean up unused deployment collector items
     *
     * @param collector the {@link Collector}
     */
    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts") // agreed, fixme
    private void clean(Collector collector) {
        Set<ObjectId> uniqueIDs = new HashSet<ObjectId>();
        /**
         * Logic: For each component, retrieve the collector item list of the type SCM.
         * Store their IDs in a unique set ONLY if their collector IDs match with GitHub collectors ID.
         */
        for (com.capitalone.dashboard.model.Component comp : dbComponentRepository.findAll()) {
            if (comp.getCollectorItems() != null && !comp.getCollectorItems().isEmpty()) {
                List<CollectorItem> itemList = comp.getCollectorItems().get(CollectorType.SCM);
                if (itemList != null) {
                    for (CollectorItem ci : itemList) {
                        if (ci != null && ci.getCollectorId().equals(collector.getId())) {
                            uniqueIDs.add(ci.getId());
                        }
                    }
                }
            }
        }

        /**
         * Logic: Get all the collector items from the collector_item collection for this collector.
         * If their id is in the unique set (above), keep them enabled; else, disable them.
         */
        List<GitHubRepo> repoList = new ArrayList<>();
        Set<ObjectId> gitID = new HashSet<>();
        gitID.add(collector.getId());
        for (GitHubRepo repo : gitHubRepoRepository.findByCollectorIdIn(gitID)) {
            if (repo != null) {
                repo.setEnabled(uniqueIDs.contains(repo.getId()));
                repoList.add(repo);
            }
        }
        gitHubRepoRepository.save(repoList);

        /**
         * Logic: Get all the collector items from the collector_item collection for this collector.
         * If their id is in the unique set (above), keep them enabled; else, disable them.

         List<GitHubOrg> reposList = new ArrayList<>();
         Set<ObjectId> gitID1 = new HashSet<>();
         gitID1.add(collector.getId());
         for (GitHubOrg repo : gitRepoRepository.findByCollectorIdIn(gitID)) {
         if (repo != null) {
         repo.setEnabled(uniqueIDs.contains(repo.getId()));
         reposList.add(repo);
         }
         }
         gitRepoRepository.save(reposList);

         */
    }


    @Override
    public void collect(Collector collector) {

        logBanner("Starting...");
        long start = System.currentTimeMillis();
        int repoCount = 0;
        int commitCount = 0;

        clean(collector);
        for (GitHubRepo repo : enabledRepos(collector)) {
            boolean firstRun = false;
            if (repo.getLastUpdated() == 0) firstRun = true;
            repo.setLastUpdated(System.currentTimeMillis());
            repo.removeLastUpdateDate();  //moved last update date to collector item. This is to clean old data.
            gitHubRepoRepository.save(repo);
            LOG.debug(repo.getOptions().toString() + "::" + repo.getBranch());
            for (Commit commit : gitHubClient.getCommits(repo, firstRun)) {
                LOG.debug(commit.getTimestamp() + ":::" + commit.getScmCommitLog());
                if (isNewCommit(repo, commit)) {
                    commit.setCollectorItemId(repo.getId());
                    commitRepository.save(commit);
                    commitCount++;
                }
            }
            for (Pull commit : gitHubClient.getPulls(repo, firstRun)) {
                //LOG.debug(commit.getTimestamp()+":::"+commit.getScmCommitLog());
                if (isNewPull(repo, commit)) {
                    commit.setCollectorItemId(repo.getId());
                    pullRepository.save(commit);
                    commitCount++;
                }
            }
            for (Issue commit : gitHubClient.getIssues(repo, firstRun)) {
                //LOG.debug(commit.getTimestamp()+":::"+commit.getScmCommitLog());
                if (isNewIssue(repo, commit)) {
                    commit.setCollectorItemId(repo.getId());
                    issueRepository.save(commit);
                    commitCount++;
                }
            }

            repoCount++;
        }
        log("Repo Count", start, repoCount);
        log("New Commits", start, commitCount);

    }


    private List<GitHubRepo> enabledRepos(Collector collector) {
        return gitHubRepoRepository.findEnabledGitHubRepos(collector.getId());
    }


    private boolean isNewCommit(GitHubRepo repo, Commit commit) {
        return commitRepository.findByCollectorItemIdAndScmRevisionNumber(
                repo.getId(), commit.getScmRevisionNumber()) == null;
    }

    private boolean isNewPull(GitHubRepo repo, Pull commit) {
        return pullRepository.findByCollectorItemIdAndNumber(
                repo.getId(), commit.getScmRevisionNumber()) == null;
    }

    private boolean isNewIssue(GitHubRepo repo, Issue commit) {
        return issueRepository.findByCollectorItemIdAndNumber(
                repo.getId(), commit.getScmRevisionNumber()) == null;
    }
}
