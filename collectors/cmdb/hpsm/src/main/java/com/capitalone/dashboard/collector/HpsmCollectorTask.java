package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.ChangeOrder;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.HpsmCollector;
import com.capitalone.dashboard.model.Incident;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.ChangeOrderRepository;
import com.capitalone.dashboard.repository.CmdbRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.HpsmRepository;
import com.capitalone.dashboard.repository.IncidentRepository;
import com.capitalone.dashboard.repository.IncidentUpdatesRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

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
    private final IncidentUpdatesRepository incidentUpdatesRepository;
    private final CollectorItemRepository collectorItemRepository;
    private final HpsmClient hpsmClient;
    private final HpsmIncidentUpdateClient incidentUpdateClient;
    private final HpsmSettings hpsmSettings;

    private static final String APP_ACTION_NAME = "Hpsm";
    private static final String CHANGE_ACTION_NAME = "HpsmChange";
    private static final String INCIDENT_ACTION_NAME = "HpsmIncident";
    private static final String INCIDENT_UPDATES_ACTION_NAME = "HpsmIncidentUpdate";
    private static final String INCIDENT_UPDATES = "incident_updates";

    private String collectorAction;

    private static final String DEFAULT_COLLECTOR_ACTION_NAME = APP_ACTION_NAME;
    private static final String COLLECTOR_ACTION_PROPERTY_KEY="collector.action";

    @Autowired
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public HpsmCollectorTask(TaskScheduler taskScheduler, HpsmSettings hpsmSettings,
                                HpsmRepository hpsmRepository,
                                CmdbRepository cmdbRepository,
                                ChangeOrderRepository changeOrderRepository,
                                IncidentRepository incidentRepository,
                                CollectorItemRepository collectorItemRepository,
                                HpsmClient hpsmClient,
                                HpsmIncidentUpdateClient incidentUpdateClient,
                                IncidentUpdatesRepository incidentUpdatesRepository) {

            super(taskScheduler, (System.getProperty(COLLECTOR_ACTION_PROPERTY_KEY) == null) ? DEFAULT_COLLECTOR_ACTION_NAME : System.getProperty(COLLECTOR_ACTION_PROPERTY_KEY));
            collectorAction = (System.getProperty(COLLECTOR_ACTION_PROPERTY_KEY) == null) ? DEFAULT_COLLECTOR_ACTION_NAME : System.getProperty(COLLECTOR_ACTION_PROPERTY_KEY);

            this.hpsmSettings = hpsmSettings;
            this.hpsmRepository = hpsmRepository;
            this.cmdbRepository = cmdbRepository;
            this.changeOrderRepository = changeOrderRepository;
            this.incidentRepository = incidentRepository;
            this.incidentUpdatesRepository = incidentUpdatesRepository;
            this.collectorItemRepository = collectorItemRepository;
            this.hpsmClient = hpsmClient;
            this.incidentUpdateClient = incidentUpdateClient;
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
        } else if(collectorAction.equals(INCIDENT_ACTION_NAME)) {
            cron = hpsmSettings.getIncidentCron();
        } else if(collectorAction.equals(INCIDENT_UPDATES_ACTION_NAME)) {
            cron = hpsmSettings.getIncidentUpdatesCron();
        }
        return cron;
    }

    public String getCollectorAction() { return collectorAction; }

    public void setCollectorAction(String collectorAction) { this.collectorAction = collectorAction; }

    private void collectApps(HpsmCollector collector) throws HygieiaException{
        List<Cmdb> cmdbList;
        List<String> configurationItemNameList = new ArrayList<>();

        int updatedCount = 0;
        int insertCount = 0;
        int inValidCount;

        cmdbList = hpsmClient.getApps();

        for(Cmdb cmdb: cmdbList) {
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

    private void collectChangeOrders(HpsmCollector collector) throws HygieiaException{

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

    private void collectIncidents(HpsmCollector collector) throws HygieiaException {
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
            String itemName = incident.getAffectedItem();
            if (StringUtils.isEmpty(itemName)) { continue; }

            // Create a CollectorItem for the incident.
            CollectorItem collectorItem = createCollectorItem(itemName, collector);
            if (collectorItem == null) { continue; }

            incident.setCollectorItemId(collectorItem.getId());
            Incident incidentDbItem = incidentRepository.findByIncidentID(incidentId);
            if (incidentDbItem != null) {
                incident.setId(incidentDbItem.getId());
                updatedCount++;
            } else { insertCount++; }
            incidentRepository.save(incident);
        }
        LOG.info("Inserted Incident Item Count: " + insertCount);
        LOG.info("Updated Incident Item Count: " + updatedCount);
    }

    private CollectorItem createCollectorItem(String itemName, HpsmCollector collector) {
        CollectorItem collectorItem = new CollectorItem();
        Cmdb cmdb = cmdbRepository.findByConfigurationItem(itemName);
        if (cmdb != null) {
            ObjectId cmdbId = cmdb.getId();
            collectorItem.setId(cmdbId);
        }
        collectorItem.setCollector(collector);
        collectorItem.setCollectorId(collector.getId());
        Map<String,Object> options = collectorItem.getOptions();
        options.put("affectedItem", itemName);

        CollectorItem existing
        = collectorItemRepository.findByCollectorAndOptions(collectorItem.getCollectorId(), collectorItem.getOptions());

        if (existing != null) {
            collectorItem.setId(existing.getId());
        }
        CollectorItem collectorItemSaved = collectorItemRepository.save(collectorItem);

        return collectorItemSaved;
    }

    @Override
    public void collect(HpsmCollector collector) {
        long start = System.currentTimeMillis();
        logBanner("Starting...");
        try {
            switch (collectorAction) {
                case APP_ACTION_NAME:
                    log("Collecting Apps");
                    collectApps(collector);
                    break;
                case CHANGE_ACTION_NAME:
                    log("Collecting Changes");
                    collectChangeOrders(collector);
                    break;
                case INCIDENT_ACTION_NAME:
                    log("Collecting Incidents");
                    collectIncidents(collector);
                    break;
                case INCIDENT_UPDATES_ACTION_NAME:
                    log("Begin: Updating Incidents");

                    // Fetch the latest info on the incidents and update them
                    long totalIterations = updateIncidents();
                    if (totalIterations > 0) {
                        moveIncidentsUpdatedToIncidents();
                    }
                    log("End: Update Incidents");
                    break;
                default:
                    log("Unknown value passed to -D" + COLLECTOR_ACTION_PROPERTY_KEY + ": " + collectorAction);
                    break;
            }

        }catch (HygieiaException he){
            LOG.error(he);
        }
        log("Finished", start);
    }

    private long updateIncidents() {
        String severity = hpsmSettings.getIncidentUpdatesSeverity();
        String[] severityValues = severity.split(",");

        int days = (-1*hpsmSettings.getIncidentUpdatesDaysBack())-1;

        // Get current date/time
        Date today = new Date();
        Date tomorrow = addDays(today, 1);
        long tomorrowMillis = tomorrow.getTime();

        // Date x days back
        Date daysBack = addDays(today, days);
        long daysBackMillis = daysBack.getTime();

        long totalIterations = getTotalIterations(severityValues, daysBackMillis, tomorrowMillis);
        int pageSize = hpsmSettings.getIncidentUpdatesPageSize();

        LOG.info("Total Iterations = "+totalIterations);

        if (totalIterations > 0) {
            incidentUpdatesRepository.dropCollection(INCIDENT_UPDATES);
        }

        for (int iterationCount=0; iterationCount<totalIterations; iterationCount++) {
            LOG.info("--------------------------------------------------------------------------------------------");
            LOG.info("Iteration : "+(iterationCount+1));

            Pageable pageable = new PageRequest(iterationCount, pageSize);
            Page<Incident> incidentPageList
                    = incidentRepository.findIncidentsBySeverityAndOpenTimeBetween(severityValues, daysBackMillis, tomorrowMillis, pageable);

            if (incidentPageList == null) { continue; }

            List<Incident> incidentList = incidentPageList.getContent();
            if (!CollectionUtils.isEmpty(incidentList)) {
                processIncidentList(incidentList);
            }
        }
        return totalIterations;
    }

    private void processIncidentList(List<Incident> incidentIdList) {
        for (Incident incident : incidentIdList) {
            String incidentId = incident.getIncidentID();
            String severity = incident.getSeverity();

            LOG.info("Fetching Incident : "+incidentId+" ; Severity : "+severity);
            try {
                Incident incidentLatest = incidentUpdateClient.getIncident(incidentId);
                if (incidentLatest != null) {
                    String updatedSeverity = incidentLatest.getSeverity();
                    LOG.info("Updating Incident : "+incidentId+" ; latest severity from hpsm : "+updatedSeverity);

                    incidentLatest.setId(incident.getId());
                    incidentLatest.setCollectorItemId(incident.getCollectorItemId());
                    incidentUpdatesRepository.save(incidentLatest, INCIDENT_UPDATES);
                }
            } catch (HygieiaException he) {
                LOG.error("Exception when processing incident: "+incidentId,he);
            }
        }
    }

    private void moveIncidentsUpdatedToIncidents() {
        long totalIterations = getTotalIterations();
        int pageSize = hpsmSettings.getIncidentUpdatesPageSize();

        LOG.info("Total iterations to move updated incidents = "+totalIterations);

        for (int iterationCount=0; iterationCount<totalIterations; iterationCount++) {
            LOG.info("--------------------------------------------------------------------------------------------");
            LOG.info("Moving Updated Incidents, Iteration : "+(iterationCount+1));

            Pageable pageable = new PageRequest(iterationCount, pageSize);
            List<Incident> incidentList = incidentUpdatesRepository.fetchIncidents(INCIDENT_UPDATES, pageable);
            if (!CollectionUtils.isEmpty(incidentList)) {
                for (Incident incident : incidentList) {
                    String incidentId = incident.getIncidentID();

                    LOG.info("Moving Incident : "+incidentId);

                    incidentRepository.save(incident);
                }
            }
        }
    }

    private long getTotalIterations() {
        long totalCount = incidentUpdatesRepository.count(INCIDENT_UPDATES);
        int pageSize = hpsmSettings.getIncidentUpdatesPageSize();

        LOG.info("Total Incidents Updated = "+totalCount+"; Page Size = "+pageSize);

        long quotient = totalCount/pageSize;
        long remainder = totalCount % pageSize;

        return (quotient + ((remainder > 0)?1:0));
    }

    private long getTotalIterations (String[] severityValues, long startDateMillis, long endDateMillis) {
        long totalCount
                = incidentRepository.countIncidentsBySeverityAndOpenTimeBetween(severityValues, startDateMillis, endDateMillis);
        int pageSize = hpsmSettings.getIncidentUpdatesPageSize();

        LOG.info("Total Incidents = "+totalCount+"; Page Size = "+pageSize);

        long quotient = totalCount/pageSize;
        long remainder = totalCount % pageSize;

        return (quotient + ((remainder > 0)?1:0));
    }

    private Date addDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }

    /**
     *  Takes configurationItemNameList (list of all APP/component names) and List<Cmdb> from client and sets flag to false for old items in mongo
     * @param configurationItemNameList
     * @return return count of items invalidated
     */
    private int cleanUpOldCmdbItems(List<String> configurationItemNameList) {
        int inValidCount = 0;
        for(Cmdb cmdb:  cmdbRepository.findAllByValidConfigItem(true)){
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