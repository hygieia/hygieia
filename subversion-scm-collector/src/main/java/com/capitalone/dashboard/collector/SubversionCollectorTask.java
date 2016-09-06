package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.SubversionRepo;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.SubversionRepoRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CollectorTask that fetches Commit information from Subversion
 */
@Component
public class SubversionCollectorTask extends CollectorTask<Collector> {
    @SuppressWarnings({ "PMD.UnusedPrivateField", "unused" })
    private static final Log LOG = LogFactory.getLog(SubversionCollectorTask.class);

    private final BaseCollectorRepository<Collector> collectorRepository;
    private final SubversionRepoRepository subversionRepoRepository;
    private final CommitRepository commitRepository;
    private final SubversionClient subversionClient;
    private final SubversionSettings subversionSettings;
    private final ComponentRepository dbComponentRepository;

    @Autowired
    public SubversionCollectorTask(TaskScheduler taskScheduler,
                                   BaseCollectorRepository<Collector> collectorRepository,
                                   SubversionRepoRepository subversionRepoRepository,
                                   CommitRepository commitRepository,
                                   ComponentRepository dbComponentRepository,
                                   SubversionClient subversionClient,
                                   SubversionSettings subversionSettings) {
        super(taskScheduler, "Subversion");
        this.collectorRepository = collectorRepository;
        this.subversionRepoRepository = subversionRepoRepository;
        this.commitRepository = commitRepository;
        this.subversionClient = subversionClient;
        this.subversionSettings = subversionSettings;
        this.dbComponentRepository = dbComponentRepository;
    }

    @Override
    public Collector getCollector() {
        Collector protoType = new Collector();
        protoType.setName("Subversion");
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
        return subversionSettings.getCron();
    }

	/**
	 * Clean up unused deployment collector items
	 */
    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts") // agreed PMD, fixme
	private void clean(Collector collector) {
		Set<ObjectId> uniqueIDs = new HashSet<>();
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
		List<SubversionRepo> repoList = new ArrayList<>();
		Set<ObjectId> svnId = new HashSet<>();
		svnId.add(collector.getId());
		for (SubversionRepo repo : subversionRepoRepository.findByCollectorIdIn(svnId)) {
			if (repo != null) {
				repo.setEnabled(uniqueIDs.contains(repo.getId()));
				repoList.add(repo);
			}
		}
		subversionRepoRepository.save(repoList);
	}

    @Override
    public void collect(Collector collector) {

        logBanner("Starting...");
        long start = System.currentTimeMillis();
        int repoCount = 0;
        int commitCount = 0;

        clean(collector);
        for (SubversionRepo repo : enabledRepos(collector)) {
            for (Commit commit : subversionClient.getCommits(repo, startRevision(repo))) {
                if (isNewCommit(repo, commit)) {
                    commit.setCollectorItemId(repo.getId());
                    commitRepository.save(commit);
                    commitCount++;

                    long revisionNumber = Long.valueOf(commit.getScmRevisionNumber());
                    if (revisionNumber > repo.getLatestRevision()) {
                        repo.setLatestRev(revisionNumber);
                    }
                }

                // Save the repo in case the latestRevision changed
                subversionRepoRepository.save(repo);

            }
            repoCount++;
        }
        log("Repo Count", start, repoCount);
        log("New Commits", start, commitCount);
        log("Finished", start);
    }

    private long startRevision(SubversionRepo repo) {
        Date revisionDate = new DateTime().minusDays(subversionSettings.getCommitThresholdDays()).toDate();
        long revisionLimit = subversionClient.getRevisionClosestTo(repo.getRepoUrl(), revisionDate);
        return revisionLimit > repo.getLatestRevision() ? revisionLimit : repo.getLatestRevision();
    }

    private List<SubversionRepo> enabledRepos(Collector collector) {
        return subversionRepoRepository.findEnabledSubversionRepos(collector.getId());
    }

    private boolean isNewCommit(SubversionRepo repo, Commit commit) {
        return commitRepository.findByCollectorItemIdAndScmRevisionNumber(
                repo.getId(), commit.getScmRevisionNumber()) == null;
    }
}
