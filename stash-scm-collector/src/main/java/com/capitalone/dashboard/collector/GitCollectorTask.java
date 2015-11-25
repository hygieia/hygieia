package com.capitalone.dashboard.collector;


import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitRepo;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.GitRepoRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * CollectorTask that fetches Commit information from Git
 */
@Component
public class GitCollectorTask extends CollectorTask<Collector> {
    private static final Log LOG = LogFactory.getLog(GitCollectorTask.class);

    private final BaseCollectorRepository<Collector> collectorRepository;
    private final GitRepoRepository gitRepoRepository;
    private final CommitRepository commitRepository;
    private final GitClient gitClient;
    private final GitSettings gitSettings;
    private final ComponentRepository dbComponentRepository;

    @Autowired
    public GitCollectorTask(TaskScheduler taskScheduler,
                                   BaseCollectorRepository<Collector> collectorRepository,
                                   GitRepoRepository gitRepoRepository,
                                   CommitRepository commitRepository,
                                   GitClient gitClient,
                                   GitSettings gitSettings,
                                   ComponentRepository dbComponentRepository) {
        super(taskScheduler, "Git");
        this.collectorRepository = collectorRepository;
        this.gitRepoRepository = gitRepoRepository;
        this.commitRepository = commitRepository;
        this.gitClient = gitClient;
        this.gitSettings = gitSettings;
        this.dbComponentRepository = dbComponentRepository;
    }

    @Override
    public Collector getCollector() {
        Collector protoType = new Collector();
        protoType.setName("Git");
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
        return gitSettings.getCron();
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
		 * Store their IDs in a unique set ONLY if their collector IDs match with Stash collectors ID.
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
		List<GitRepo> repoList = new ArrayList<GitRepo>();
		Set<ObjectId> gitID = new HashSet<ObjectId>();
		gitID.add(collector.getId());
		for (GitRepo repo : gitRepoRepository.findByCollectorIdIn(gitID)) {
			if (repo != null) {
				repo.setEnabled(uniqueIDs.contains(repo.getId()));
				repoList.add(repo);
			}
		}
		gitRepoRepository.save(repoList);
	}


    @Override
    public void collect(Collector collector) {

        logBanner("Starting...");
        long start = System.currentTimeMillis();
        int repoCount = 0;
        int commitCount = 0;

        clean(collector);
        for (GitRepo repo : enabledRepos(collector)) {
        	boolean firstRun = false;
        	if (repo.getLastUpdateTime() == null) firstRun = true;
        	repo.setLastUpdateTime(new Date());
            gitRepoRepository.save(repo);
            LOG.debug(repo.getOptions().toString()+"::"+repo.getBranch());
            for (Commit commit : gitClient.getCommits(repo, firstRun)) {
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
    private Date lastUpdated(GitRepo repo) {
        return repo.getLastUpdateTime();
    }

    private List<GitRepo> enabledRepos(Collector collector) {
        return gitRepoRepository.findEnabledGitRepos(collector.getId());
    }

    private boolean isNewCommit(GitRepo repo, Commit commit) {
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
