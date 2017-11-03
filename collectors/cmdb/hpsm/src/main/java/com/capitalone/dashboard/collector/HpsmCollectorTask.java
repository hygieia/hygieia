package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.ChangeOrder;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.HpsmCollector;
import com.capitalone.dashboard.model.Incident;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.ChangeOrderRepository;
import com.capitalone.dashboard.repository.CmdbRepository;
import com.capitalone.dashboard.repository.HpsmRepository;
import com.capitalone.dashboard.repository.IncidentRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * CollectorTask that fetches configuration item data from HPSM
 */
@Component
public class HpsmCollectorTask extends CollectorTask<HpsmCollector> {
    private static final Log LOG = LogFactory.getLog(HpsmCollectorTask.class);

    private final HpsmRepository hpsmRepository;
    private final CmdbRepository cmdbRepository;
    private final ChangeOrderRepository changeOrderRepository;
    private final IncidentRepository incidentRepository;
    private final HpsmClient hpsmClient;
    private final HpsmSettings hpsmSettings;

    private static final String APP_ACTION_NAME = "Hpsm";
    private static final String CHANGE_ACTION_NAME = "HpsmChange";
    private static final String INCIDENT_ACTION_NAME = "HpsmIncident";

    private String collectorAction;

    private static final String DEFAULT_COLLECTOR_ACTION_NAME = APP_ACTION_NAME;
    private static final String COLLECTOR_ACTION_PROPERTY_KEY="collector.action";

    @Autowired
    public HpsmCollectorTask(TaskScheduler taskScheduler, HpsmSettings hpsmSettings,
                                HpsmRepository hpsmRepository,
                                CmdbRepository cmdbRepository,
                                ChangeOrderRepository changeOrderRepository,
                                IncidentRepository incidentRepository,
                                HpsmClient hpsmClient) {

            super(taskScheduler, (System.getProperty(COLLECTOR_ACTION_PROPERTY_KEY) == null) ? DEFAULT_COLLECTOR_ACTION_NAME : System.getProperty(COLLECTOR_ACTION_PROPERTY_KEY));
            collectorAction = (System.getProperty(COLLECTOR_ACTION_PROPERTY_KEY) == null) ? DEFAULT_COLLECTOR_ACTION_NAME : System.getProperty(COLLECTOR_ACTION_PROPERTY_KEY);

            this.hpsmSettings = hpsmSettings;
            this.hpsmRepository = hpsmRepository;
            this.cmdbRepository = cmdbRepository;
            this.changeOrderRepository = changeOrderRepository;
            this.incidentRepository = incidentRepository;
            this.hpsmClient = hpsmClient;
    }

    /**
     * Accessor method for the collector prototype object
     */
    @Override
    public HpsmCollector getCollector() {
        return HpsmCollector.prototype(collectorAction);
    }

    @Override
    public BaseCollectorRepository<HpsmCollector> getCollectorRepository() {
        return hpsmRepository;
    }

    @Override
    public String getCron() {
        String cron = hpsmSettings.getCron();

        if(collectorAction.equals(CHANGE_ACTION_NAME)) {
            cron = hpsmSettings.getChangeOrderCron();
        }
        else if(collectorAction.equals(INCIDENT_ACTION_NAME)) {
            cron = hpsmSettings.getIncidentCron();
        }
        return cron;
    }

    public String getCollectorAction() { return collectorAction; }

    public void setCollectorAction(String collectorAction) { this.collectorAction = collectorAction; }

    private void collectApps(HpsmCollector collector) {
        List<Cmdb> cmdbList;
        List<String> configurationItemNameList = new ArrayList<>();

        int updatedCount = 0;
        int insertCount = 0;
        int inValidCount;

        cmdbList = hpsmClient.getApps();

        for(Cmdb cmdb: cmdbList){

            String configItem = cmdb.getConfigurationItem();
            Cmdb cmdbDbItem =  cmdbRepository.findByConfigurationItem(configItem);
            configurationItemNameList.add(configItem);
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

        inValidCount = cleanUpOldCmdbItems(configurationItemNameList);

        LOG.info("Inserted Cmdb Item Count: " + insertCount);
        LOG.info("Updated Cmdb Item Count: " +  updatedCount);
        LOG.info("Invalid Cmdb Item Count: " +  inValidCount);

    }

    private void collectChangeOrders(HpsmCollector collector) {

        long lastExecuted = collector.getLastExecuted();
        long changeCount = changeOrderRepository.count();

        hpsmClient.setLastExecuted(lastExecuted);
        hpsmClient.setChangeCount(changeCount);

        List<ChangeOrder> changeList;

        int updatedCount = 0;
        int insertCount = 0;

        changeList = hpsmClient.getChangeOrders();

        for (ChangeOrder changeOrder : changeList) {
            String changeId = changeOrder.getChangeID();
            ChangeOrder changeDbItem = changeOrderRepository.findByChangeID(changeId);
            if (changeDbItem != null && !changeOrder.equals(changeDbItem)) {
                changeOrder.setId(changeDbItem.getId());
                changeOrder.setCollectorItemId(collector.getId());
                changeOrderRepository.save(changeOrder);
                updatedCount++;
            } else if (changeDbItem == null) {
                changeOrder.setCollectorItemId(collector.getId());
                changeOrderRepository.save(changeOrder);
                insertCount++;
            }
        }

        LOG.info("Inserted ChangeOrder Item Count: " + insertCount);
        LOG.info("Updated ChangeOrder Item Count: " +  updatedCount);

    }

    private void collectIncidents(HpsmCollector collector) {

        long lastExecuted = collector.getLastExecuted();
        long incidentCount = incidentRepository.count();

        List<Incident> incidentList;

        int updatedCount = 0;
        int insertCount = 0;

        hpsmClient.setLastExecuted(lastExecuted);
        hpsmClient.setIncidentCount(incidentCount);
        incidentList = hpsmClient.getIncidents();

        for (Incident incident : incidentList) {

            String incidentId = incident.getIncidentID();
            Incident incidentDbItem = incidentRepository.findByIncidentID(incidentId);
            if (incidentDbItem != null && !incident.equals(incidentDbItem)) {
                incident.setId(incidentDbItem.getId());
                incident.setCollectorItemId(collector.getId());
                incidentRepository.save(incident);
                updatedCount++;
            } else if (incidentDbItem == null) {
                incident.setCollectorItemId(collector.getId());
                incidentRepository.save(incident);
                insertCount++;
            }
        }

        LOG.info("Inserted Incident Item Count: " + insertCount);
        LOG.info("Updated Incident Item Count: " + updatedCount);
    }

    @Override
    public void collect(HpsmCollector collector) {
        long start = System.currentTimeMillis();
        logBanner("Starting...");

        if(collectorAction.equals(APP_ACTION_NAME)) {
            log("Collecting Apps");
            collectApps(collector);
        }
        else if(collectorAction.equals(CHANGE_ACTION_NAME)) {
            log("Collecting Changes");
            collectChangeOrders(collector);
        }
        else if(collectorAction.equals(INCIDENT_ACTION_NAME)) {
            log("Collecting Incidents");
            collectIncidents(collector);
        }
        else{
            log("Unknown value passed to -D" + COLLECTOR_ACTION_PROPERTY_KEY + ": " + collectorAction);
        }

        log("Finished", start);
    }

    /**
     *  Takes configurationItemNameList (list of all APP/component names) and List<Cmdb> from client and sets flag to false for old items in mongo
     * @param configurationItemNameList
     * @return return count of items invalidated
     */
    private int cleanUpOldCmdbItems(List<String> configurationItemNameList) {
        int inValidCount = 0;
        for(Cmdb cmdb:  cmdbRepository.findAll()){
            String configItem = cmdb.getConfigurationItem();

            if(configurationItemNameList != null && !configurationItemNameList.contains(configItem)){
                cmdb.setValidConfigItem(false);
                cmdbRepository.save(cmdb);
                inValidCount++;
            }
        }
        return inValidCount;
    }

}
