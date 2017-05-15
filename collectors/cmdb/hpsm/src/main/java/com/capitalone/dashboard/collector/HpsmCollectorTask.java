package com.capitalone.dashboard.collector;


import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.HpsmCollector;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CmdbRepository;
import com.capitalone.dashboard.repository.HpsmRepository;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
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
public class HpsmCollectorTask extends CollectorTask<HpsmCollector> {
    private static final Log LOG = LogFactory.getLog(HpsmCollectorTask.class);
    private final BaseCollectorRepository<Collector> collectorRepository;

    private final HpsmRepository hpsmRepository;
    private final CmdbRepository cmdbRepository;
    private final HpsmClient hpsmClient;
    private final HpsmSettings hpsmSettings;

    @Autowired
    public HpsmCollectorTask(TaskScheduler taskScheduler, HpsmSettings hpsmSettings,
                                BaseCollectorRepository<Collector> collectorRepository,
                                HpsmRepository hpsmRepository,
                                CmdbRepository cmdbRepository,
                                HpsmClient hpsmClient) {
        super(taskScheduler, FeatureCollectorConstants.HPSM);

        this.hpsmSettings = hpsmSettings;
        this.hpsmRepository = hpsmRepository;
        this.collectorRepository = collectorRepository;
        this.cmdbRepository = cmdbRepository;
        this.hpsmClient = hpsmClient;

    }

    /**
     * Accessor method for the collector prototype object
     */
    @Override
    public HpsmCollector getCollector() {
        return HpsmCollector.prototype();
    }

    @Override
    public BaseCollectorRepository<HpsmCollector> getCollectorRepository() {
        return hpsmRepository;
    }

    @Override
    public String getCron() {
        return hpsmSettings.getCron();
    }

    @Override
    public void collect(HpsmCollector collector) {
        logBanner("Starting...");
        List<Cmdb> cmdbList;
        long start = System.currentTimeMillis();
        int updatedCount = 0;
        int insertCount = 0;

        cmdbList = hpsmClient.getApps();

        for(Cmdb cmdb: cmdbList){

            String configItem = cmdb.getConfigurationItem();
            Cmdb cmdbDbItem =  cmdbRepository.findByConfigurationItem(configItem);

           if(cmdbDbItem != null && !cmdb.equals(cmdbDbItem)){
               cmdb.setId(cmdbDbItem.getId());
               cmdb.setCollectorItemId(collector.getId());
               cmdbRepository.save(cmdb);
               updatedCount++;
           }else if(cmdbDbItem == null){
               cmdb.setCollectorItemId(collector.getId());
               cmdbRepository.save(cmdb);
               insertCount++;
           }


        }
        LOG.info("Inserted Item Count" + insertCount);
        LOG.info("Updated Item Count" +  updatedCount);
        log("Finished", start);
    }
}
