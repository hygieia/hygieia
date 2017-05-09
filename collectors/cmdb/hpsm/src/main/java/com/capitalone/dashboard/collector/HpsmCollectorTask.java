package com.capitalone.dashboard.collector;


import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.HpsmCollector;
import com.capitalone.dashboard.repository.HpsmRepository;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CollectorTask that fetches Commit information from GitHub
 */
@Component
public class HpsmCollectorTask extends CollectorTask<Collector> {
    private static final Log LOG = LogFactory.getLog(HpsmCollectorTask.class);
    private final BaseCollectorRepository<Collector> collectorRepository;
//    private final BaseCollectorRepository<Collector> collectorRepository;
 //     private final HpsmRepository HpsmRepository;
//    private final CommitRepository commitRepository;
      private final HpsmClient hpsmClient;
      private final HpsmSettings hpsmSettings;
    //    private final ComponentRepository dbComponentRepository;

//    @Autowired
//    public HpsmCollectorTask(TaskScheduler taskScheduler,
//                                   BaseCollectorRepository<Collector> collectorRepository,
//                                   HpsmRepository gitHubRepoRepository,
//                                   CommitRepository commitRepository,
//                                   HpsmClient gitHubClient,
//                                    HpsmSettings hpsmSettings,
//                                   ComponentRepository dbComponentRepository) {
@Autowired
public HpsmCollectorTask(TaskScheduler taskScheduler, HpsmSettings hpsmSettings, BaseCollectorRepository<Collector> collectorRepository, HpsmClient hpsmClient) {
        super(taskScheduler, "hpsm");

        this.hpsmSettings = hpsmSettings;
//        this.HpsmRepository = HpsmRepository;

    this.collectorRepository = collectorRepository;
//    this.gitHubRepoRepository = gitHubRepoRepository;
//    this.commitRepository = commitRepository;
      this.hpsmClient = hpsmClient;
//    this.hpsmSettings = hpsmSettings;
//    this.dbComponentRepository = dbComponentRepository;
    }

    @Override
    public Collector getCollector() {

        Collector protoType = new Collector();
        protoType.setName("Hpsm");
        protoType.setCollectorType(CollectorType.CMDB);
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
        return hpsmSettings.getCron();
    }

	/**
	 * Clean up unused deployment collector items
	 *
	 * @param collector
	 *            the {@link Collector}
	 */
//    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts") // agreed, fixme
//	private void clean(Collector collector) {
//		Set<ObjectId> uniqueIDs = new HashSet<ObjectId>();
//		/**
//		 * Logic: For each component, retrieve the collector item list of the type SCM.
//		 * Store their IDs in a unique set ONLY if their collector IDs match with GitHub collectors ID.
//		 */
//		for (com.capitalone.dashboard.model.Component comp : dbComponentRepository.findAll()) {
//			if (comp.getCollectorItems() != null && !comp.getCollectorItems().isEmpty()) {
//				List<CollectorItem> itemList = comp.getCollectorItems().get(CollectorType.SCM);
//				if (itemList != null) {
//					for (CollectorItem ci : itemList) {
//						if (ci != null && ci.getCollectorId().equals(collector.getId())){
//							uniqueIDs.add(ci.getId());
//						}
//					}
//				}
//			}
//		}
//
//		/**
//		 * Logic: Get all the collector items from the collector_item collection for this collector.
//		 * If their id is in the unique set (above), keep them enabled; else, disable them.
//		 */
//		List<HpsmCollector> repoList = new ArrayList<>();
//		Set<ObjectId> gitID = new HashSet<>();
//		gitID.add(collector.getId());
//		for (HpsmCollector repo : gitHubRepoRepository.findByCollectorIdIn(gitID)) {
//			if (repo != null) {
//				repo.setEnabled(uniqueIDs.contains(repo.getId()));
//				repoList.add(repo);
//			}
//		}
//		gitHubRepoRepository.save(repoList);
//	}


    @Override
    public void collect(Collector collector) {
        logBanner("Starting...");
        long start = System.currentTimeMillis();
        List<HpsmCollector> appList = hpsmClient.getApps();

//        gitHubClient.toString();
//        int repoCount = 0;
//        int commitCount = 0;
//
//        clean(collector);
//        for (HpsmCollector repo : enabledRepos(collector)) {
//        	boolean firstRun = false;
//        	if (repo.getLastUpdated() == 0) firstRun = true;
//        	repo.setLastUpdated(System.currentTimeMillis());
//            repo.removeLastUpdateDate();  //moved last update date to collector item. This is to clean old data.
//            gitHubRepoRepository.save(repo);
//            LOG.debug(repo.getOptions().toString()+"::"+repo.getBranch());
//            for (Commit commit : gitHubClient.getCommits(repo, firstRun)) {
//            	LOG.debug(commit.getTimestamp()+":::"+commit.getScmCommitLog());
//                if (isNewCommit(repo, commit)) {
//                    commit.setCollectorItemId(repo.getId());
//                    commitRepository.save(commit);
//                    commitCount++;
//                }
//            }
//
//            repoCount++;
//        }
//        log("Repo Count", start, repoCount);
//        log("New Commits", start, commitCount);
//
        log("Finished", start);
    }



}
