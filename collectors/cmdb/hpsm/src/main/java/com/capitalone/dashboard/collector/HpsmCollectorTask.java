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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
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
    private LinkedHashSet<String> assignmentGroups = new LinkedHashSet<String>();

    private static final String CHANGE_ORDER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";

    @Autowired
    public HpsmCollectorTask(TaskScheduler taskScheduler, HpsmSettings hpsmSettings,
                                HpsmRepository hpsmRepository,
                                CmdbRepository cmdbRepository,
                                ChangeOrderRepository changeOrderRepository,
                                IncidentRepository incidentRepository,
                                HpsmClient hpsmClient) {
        super(taskScheduler, "Hpsm");

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

    private void collectApps(HpsmCollector collector) {
        clearAssignmentGroups();
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
            addAssignmentGroup(cmdb.getAssignmentGroup());
        }

        inValidCount = cleanUpOldCmdbItems(configurationItemNameList);

        LOG.info("Inserted Cmdb Item Count: " + insertCount);
        LOG.info("Updated Cmdb Item Count: " +  updatedCount);
        LOG.info("Invalid Cmdb Item Count: " +  inValidCount);

    }

    private void collectChangeOrders(HpsmCollector collector) {
        List<ChangeOrder> changeList;
        List<String> changeIdList = new ArrayList<>();

        int updatedCount = 0;
        int insertCount = 0;

        for(String assignmentGroup:getAssignmentGroups()) {

            changeList = hpsmClient.getChangeOrders(assignmentGroup);

            for (ChangeOrder changeOrder : changeList) {

                int changeOrderDays = hpsmSettings.getChangeOrderDays();

                Date nowDate = new Date();

                Calendar cal = Calendar.getInstance();
                cal.setTime(nowDate);
                cal.add(Calendar.DATE, -changeOrderDays);
                Date previousDate = cal.getTime();

                SimpleDateFormat dateFormat = new SimpleDateFormat(CHANGE_ORDER_DATE_FORMAT);

                Date dateEntered = null;

                try {
                    if (changeOrder != null && changeOrder.getDateEntered() != null) {
                        dateEntered = dateFormat.parse(changeOrder.getDateEntered());
                    }
                } catch (ParseException e) {
                    // Shouldn't happen
                    LOG.info("Unable to parse DateEntered for : " + changeOrder.getChangeID());
                }

                boolean changeInRange = (dateEntered != null && previousDate.getTime() < dateEntered.getTime());

                if (changeInRange) {
                    String changeId = changeOrder.getChangeID();
                    ChangeOrder changeDbItem = changeOrderRepository.findByChangeID(changeId);
                    changeIdList.add(changeId);
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
            }
        }

        LOG.info("Inserted ChangeOrder Item Count: " + insertCount);
        LOG.info("Updated ChangeOrder Item Count: " +  updatedCount);

    }

    private void collectIncidents(HpsmCollector collector) {

        long lastExecuted = collector.getLastExecuted();
        long incidentCount = incidentRepository.count();

        List<Incident> incidentList;
        List<String> incidentItemNameList = new ArrayList<>();

        int updatedCount = 0;
        int insertCount = 0;

        for(String assignmentGroup:getAssignmentGroups()) {
            hpsmClient.setLastExecuted(lastExecuted);
            hpsmClient.setIncidentCount(incidentCount);
            incidentList = hpsmClient.getIncidents(assignmentGroup);

            for (Incident incident : incidentList) {

                String incidentId = incident.getIncidentID();
                Incident incidentDbItem = incidentRepository.findByIncidentID(incidentId);
                incidentItemNameList.add(incidentId);
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
        }
        LOG.info("Inserted Incident Item Count: " + insertCount);
        LOG.info("Updated Incident Item Count: " + updatedCount);
    }

    @Override
    public void collect(HpsmCollector collector) {
        long start = System.currentTimeMillis();
        logBanner("Starting...");
        collectApps(collector);
        collectChangeOrders(collector);
        collectIncidents(collector);

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

    private void clearAssignmentGroups(){
        assignmentGroups.clear();
    }

    private void addAssignmentGroup(String assignmentGroup){
        assignmentGroups.add(assignmentGroup);
    }

    private List<String> getAssignmentGroups(){
        return new ArrayList<>(assignmentGroups);
    }

}
