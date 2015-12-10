package com.capitalone.dashboard.collector;


import com.capitalone.dashboard.model.ChatOpsRepo;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.ChatOpsRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CollectorTask that fetches Commit information from GitHub
 */
@Component
public class ChatOpsCollectorTask extends CollectorTask<Collector> {
    private static final Log LOG = LogFactory.getLog(ChatOpsCollectorTask.class);

    private final BaseCollectorRepository<Collector> collectorRepository;
    private final ChatOpsRepository chatOpsRepository;
    private final CommitRepository commitRepository;
    private final ChatOpsClient chatOpsClient;
    private final ChatOpsSettings chatOpsSettings;
    private final ComponentRepository dbComponentRepository;

    @Autowired
    public ChatOpsCollectorTask(TaskScheduler taskScheduler,
                                   BaseCollectorRepository<Collector> collectorRepository,
                                   ChatOpsRepository chatOpsRepository,
                                   CommitRepository commitRepository,
                                   ChatOpsClient chatOpsClient,
                                   ChatOpsSettings chatOpsSettings,
                                   ComponentRepository dbComponentRepository) {
        super(taskScheduler, "ChatOps");
        this.collectorRepository = collectorRepository;
        this.chatOpsRepository = chatOpsRepository;
        this.commitRepository = commitRepository;
        this.chatOpsClient = chatOpsClient;
        this.chatOpsSettings = chatOpsSettings;
        this.dbComponentRepository = dbComponentRepository;
    }

    @Override
    public Collector getCollector() {
        Collector protoType = new Collector();
        protoType.setName("ChatOps");
        protoType.setCollectorType(CollectorType.ChatOps);
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
        return chatOpsSettings.getCron();
    }
    


	private void clean(Collector collector) {
		Set<ObjectId> uniqueIDs = new HashSet<ObjectId>();
		/**
		 * Logic: For each component, retrieve the collector item list of the type SCM. 
		 * Store their IDs in a unique set ONLY if their collector IDs match with GitHub collectors ID.
		 */
		for (com.capitalone.dashboard.model.Component comp : dbComponentRepository
				.findAll()) {
			if ((comp.getCollectorItems() != null)
					&& !comp.getCollectorItems().isEmpty()) {
				List<CollectorItem> itemList = comp.getCollectorItems().get(
						CollectorType.SCM);
				if (itemList != null) {
					for (CollectorItem ci : itemList) {
						if ((ci != null) && (ci.getCollectorId().equals(collector.getId()))){
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
		List<ChatOpsRepo> repoList = new ArrayList<ChatOpsRepo>();
		Set<ObjectId> gitID = new HashSet<ObjectId>();
		gitID.add(collector.getId());
		for (ChatOpsRepo repo : chatOpsRepository.findByCollectorIdIn(gitID)) {
			if (repo != null) {
				repo.setEnabled(uniqueIDs.contains(repo.getId()));
				repoList.add(repo);
			}
		}
		chatOpsRepository.save(repoList);
	}
	

    @Override
    public void collect(Collector collector) {

        logBanner("Starting...");
        long start = System.currentTimeMillis();

        clean(collector);
        for (ChatOpsRepo repo : enabledRepos(collector)) {
        	repo.setLastUpdateTime(new DateTime());
            chatOpsRepository.save(repo);
            log("Finished", start);

    }
    }

    private DateTime lastUpdated(ChatOpsRepo repo) {
        return repo.getLastUpdateTime();
    }

    private List<ChatOpsRepo> enabledRepos(Collector collector) {
        return chatOpsRepository.findEnabledChatOpsRepos(collector.getId());
    }

 

    protected void log(String marker, long start) {
        log(marker, start, null);
    }

    protected void log(String text, long start, Integer count) {
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

    protected void logBanner(String instanceUrl) {
        LOG.info("------------------------------");
        LOG.info(instanceUrl);
        LOG.info("------------------------------");
    }
}
