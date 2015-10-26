package com.capitalone.dashboard.collector;


import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitHubRepo;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.GitHubRepoRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * CollectorTask that fetches Commit information from GitHub
 */
@Component
public class GitHubCollectorTask extends CollectorTask<Collector> {
    private static final Log LOG = LogFactory.getLog(GitHubCollectorTask.class);

    private final BaseCollectorRepository<Collector> collectorRepository;
    private final GitHubRepoRepository gitHubRepoRepository;
    private final CommitRepository commitRepository;
    private final GitHubClient gitHubClient;
    private final GitHubSettings gitHubSettings;
    private final ComponentRepository dbComponentRepository;

    @Autowired
    public GitHubCollectorTask(TaskScheduler taskScheduler,
                                   BaseCollectorRepository<Collector> collectorRepository,
                                   GitHubRepoRepository gitHubRepoRepository,
                                   CommitRepository commitRepository,
                                   GitHubClient gitHubClient,
                                   GitHubSettings gitHubSettings,
                                   ComponentRepository dbComponentRepository) {
        super(taskScheduler, "GitHub");
        this.collectorRepository = collectorRepository;
        this.gitHubRepoRepository = gitHubRepoRepository;
        this.commitRepository = commitRepository;
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
	 * @param collector
	 *            the {@link UDeployCollector}
	 */

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
						if (ci != null && ci.getCollectorId().equals(collector.getId())){
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
		List<GitHubRepo> repoList = new ArrayList<GitHubRepo>();
		Set<ObjectId> gitID = new HashSet<ObjectId>();
		gitID.add(collector.getId());
		for (GitHubRepo repo : gitHubRepoRepository.findByCollectorIdIn(gitID)) {
			if (repo != null) {
				repo.setEnabled(uniqueIDs.contains(repo.getId()));
				repoList.add(repo);
			}
		}
		gitHubRepoRepository.save(repoList);
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
        	if (repo.getLastUpdateTime() == null) firstRun = true;
        	repo.setLastUpdateTime(new Date());
            gitHubRepoRepository.save(repo);
            LOG.debug(repo.getOptions().toString()+"::"+repo.getBranch());
            for (Commit commit : gitHubClient.getCommits(repo, firstRun)) {
            	LOG.debug(commit.getTimestamp()+":::"+commit.getScmCommitLog());
                if (isNewCommit(repo, commit)) {
                    commit.setCollectorItemId(repo.getId());
                    commitRepository.save(commit);
                    commitCount++;
                }
            }

            repoCount++;
        }
        log("Repo Count", start, repoCount);
        log("New Commits", start, commitCount);

        log("Finished", start);
    }

    @SuppressWarnings("unused")
    private Date lastUpdated(GitHubRepo repo) {
        return repo.getLastUpdateTime();
    }

    private List<GitHubRepo> enabledRepos(Collector collector) {
        return gitHubRepoRepository.findEnabledGitHubRepos(collector.getId());
    }

    private boolean isNewCommit(GitHubRepo repo, Commit commit) {
        return commitRepository.findByCollectorItemIdAndScmRevisionNumber(
                repo.getId(), commit.getScmRevisionNumber()) == null;
    }

    private void log(String marker, long start) {
        log(marker, start, null);
    }

    private void log(String text, long start, Integer count) {
        long end = System.currentTimeMillis();
        String elapsed = ((end - start) / 1000) + "s";
        String token2 = "";
        String token3;
        if (count == null) {
            token3 = StringUtils.leftPad(elapsed, 30 - text.length() );
        } else {
            String countStr = count.toString();
            token2 = StringUtils.leftPad(countStr, 20 - text.length() );
            token3 = StringUtils.leftPad(elapsed, 10 );
        }
        LOG.info(text + token2 + token3);
    }

    private void logBanner(String instanceUrl) {
        LOG.info("------------------------------");
        LOG.info(instanceUrl);
        LOG.info("------------------------------");
    }
}
