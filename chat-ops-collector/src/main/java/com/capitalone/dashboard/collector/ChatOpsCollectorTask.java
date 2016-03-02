package com.capitalone.dashboard.collector;


import com.capitalone.dashboard.model.ChatOpsRepo;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.ChatOpsRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
public class ChatOpsCollectorTask extends CollectorTask<Collector> {

    private final BaseCollectorRepository<Collector> collectorRepository;
    private final ChatOpsRepository chatOpsRepository;
    private final ChatOpsSettings chatOpsSettings;
    private final ComponentRepository dbComponentRepository;

    @Autowired
    public ChatOpsCollectorTask(TaskScheduler taskScheduler,
                                BaseCollectorRepository<Collector> collectorRepository,
                                ChatOpsRepository chatOpsRepository,
                                ChatOpsSettings chatOpsSettings,
                                ComponentRepository dbComponentRepository) {
        super(taskScheduler, "ChatOps");
        this.collectorRepository = collectorRepository;
        this.chatOpsRepository = chatOpsRepository;
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

    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts") // agreed, fixme
    private void clean(Collector collector) {
        Set<ObjectId> uniqueIDs = new HashSet<ObjectId>();

        for (com.capitalone.dashboard.model.Component comp : dbComponentRepository
                .findAll()) {
            if ((comp.getCollectorItems() != null)
                    && !comp.getCollectorItems().isEmpty()) {
                List<CollectorItem> itemList = comp.getCollectorItems().get(
                        CollectorType.SCM);
                if (itemList != null) {
                    for (CollectorItem ci : itemList) {
                        if ((ci != null) && (ci.getCollectorId().equals(collector.getId()))) {
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

    @SuppressWarnings("unused")
	private DateTime lastUpdated(ChatOpsRepo repo) {
        return repo.getLastUpdateTime();
    }

    private List<ChatOpsRepo> enabledRepos(Collector collector) {
        return chatOpsRepository.findEnabledChatOpsRepos(collector.getId());
    }

}
