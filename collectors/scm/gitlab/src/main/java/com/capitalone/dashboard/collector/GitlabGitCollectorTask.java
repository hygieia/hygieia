package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.gitlab.DefaultGitlabGitClient;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitlabGitRepo;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.GitlabGitCollectorRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by benathmane on 23/06/16.
 */

/**
 * CollectorTask that fetches Commit information from Gitlab
 */
@Component
public class GitlabGitCollectorTask  extends CollectorTask<Collector> {
    private static final Log LOG = LogFactory.getLog(GitlabGitCollectorTask.class);

    private final BaseCollectorRepository<Collector> collectorRepository;
    private final GitlabGitCollectorRepository gitlabGitCollectorRepository;
    private final GitlabSettings gitlabSettings;
    private final DefaultGitlabGitClient defaultGitlabGitClient;
    private final ComponentRepository dbComponentRepository;
    private final CommitRepository commitRepository;


    @Autowired
    public GitlabGitCollectorTask(TaskScheduler taskScheduler,
                                  BaseCollectorRepository<Collector> collectorRepository,
                                  GitlabSettings gitlabSettings,
                                  CommitRepository commitRepository,
                                  GitlabGitCollectorRepository gitlabGitCollectorRepository,
                                  DefaultGitlabGitClient defaultGitlabGitClient,
                                  ComponentRepository dbComponentRepository
    ) {
        super(taskScheduler, "Gitlab");
        this.collectorRepository = collectorRepository;
        this.gitlabSettings = gitlabSettings;
        this.commitRepository = commitRepository;
        this.gitlabGitCollectorRepository = gitlabGitCollectorRepository;
        this.defaultGitlabGitClient = defaultGitlabGitClient;
        this.dbComponentRepository = dbComponentRepository;
    }

	@Override
	public Collector getCollector() {
		Collector protoType = new Collector();
		protoType.setName("Gitlab");
		protoType.setCollectorType(CollectorType.SCM);
		protoType.setOnline(true);
		protoType.setEnabled(true);

		Map<String, Object> allOptions = new HashMap<>();
		allOptions.put(GitlabGitRepo.REPO_URL, "");
		allOptions.put(GitlabGitRepo.BRANCH, "");
		allOptions.put(GitlabGitRepo.USER_ID, "");
		allOptions.put(GitlabGitRepo.PASSWORD, "");
		allOptions.put(GitlabGitRepo.LAST_UPDATE_TIME, new Date());
		protoType.setAllFields(allOptions);

		Map<String, Object> uniqueOptions = new HashMap<>();
		uniqueOptions.put(GitlabGitRepo.REPO_URL, "");
		uniqueOptions.put(GitlabGitRepo.BRANCH, "");
		protoType.setUniqueFields(uniqueOptions);
		return protoType;
	}

    @Override
    public BaseCollectorRepository<Collector> getCollectorRepository() {
        return collectorRepository;
    }

    @Override
    public String getCron() {
        return gitlabSettings.getCron();
    }

    @Override
    public void collect(Collector collector) {
        logBanner("Starting...");
        long start = System.currentTimeMillis();
        int repoCount = 0;
        int commitCount = 0;

        clean(collector);
        for (GitlabGitRepo repo : enabledRepos(collector)) {
			boolean firstRun = false;
			if (repo.getLastUpdated() == 0)
				firstRun = true;
			repo.setLastUpdated(System.currentTimeMillis());
			repo.removeLastUpdateDate();
			
			try {
				List<Commit> commits = defaultGitlabGitClient.getCommits(repo, firstRun);
				commitCount = saveNewCommits(commitCount, repo, commits);
				gitlabGitCollectorRepository.save(repo);
			} catch (HttpClientErrorException | ResourceAccessException e) {
				LOG.info("Failed to retrieve data, the repo or collector is most likey misconfigured: " + repo.getRepoUrl() + ", " + e.getMessage());
			}
			
			repoCount++;
        }
        log("Repo Count", start, repoCount);
        log("New Commits", start, commitCount);
        log("Finished", start);
    }

	private int saveNewCommits(int commitCount, GitlabGitRepo repo, List<Commit> commits) {
		int totalCommitCount = commitCount;
		for (Commit commit : commits) {
			LOG.debug(commit.getTimestamp() + ":::" + commit.getScmCommitLog());
			if (isNewCommit(repo, commit)) {
				commit.setCollectorItemId(repo.getId());
				commitRepository.save(commit);
				totalCommitCount++;
			}
		}
		return totalCommitCount;
	}

	@SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts") // agreed, fixme
	private void clean(Collector collector) {
		Set<ObjectId> uniqueIDs = new HashSet<ObjectId>();
		/**
		 * Logic: For each component, retrieve the collector item list of the
		 * type SCM. Store their IDs in a unique set ONLY if their collector IDs
		 * match with GitHub collectors ID.
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
		 * Logic: Get all the collector items from the collector_item collection
		 * for this collector. If their id is in the unique set (above), keep
		 * them enabled; else, disable them.
		 */
		List<GitlabGitRepo> repoList = new ArrayList<>();
		Set<ObjectId> gitID = new HashSet<>();
		gitID.add(collector.getId());
		for (GitlabGitRepo repo : gitlabGitCollectorRepository.findByCollectorIdIn(gitID)) {
			if (repo != null) {
				repo.setEnabled(uniqueIDs.contains(repo.getId()));
				repoList.add(repo);
			}
		}
		gitlabGitCollectorRepository.save(repoList);
	}



    private List<GitlabGitRepo> enabledRepos(Collector collector) {
        return gitlabGitCollectorRepository.findEnabledGitlabRepos(collector.getId());
    }

	private boolean isNewCommit(GitlabGitRepo repo, Commit commit) {
		return commitRepository.findByCollectorItemIdAndScmRevisionNumber(repo.getId(),
				commit.getScmRevisionNumber()) == null;
	}
}
