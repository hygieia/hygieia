package com.capitalone.dashboard.collector;


import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.LibraryPolicyReport;
import com.capitalone.dashboard.model.LibraryPolicyResult;
import com.capitalone.dashboard.model.NexusIQApplication;
import com.capitalone.dashboard.model.NexusIQCollector;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.LibraryPolicyResultsRepository;
import com.capitalone.dashboard.repository.NexusIQApplicationRepository;
import com.capitalone.dashboard.repository.NexusIQCollectorRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
public class NexusIQCollectorTask extends CollectorTask<NexusIQCollector> {
    private final NexusIQCollectorRepository nexusIQCollectorRepository;
    private final NexusIQApplicationRepository nexusIQApplicationRepository;
    private final LibraryPolicyResultsRepository libraryPolicyResultsRepository;
    private final NexusIQClient nexusIQClient;
    private final NexusIQSettings nexusIQSettings;
    private final ComponentRepository dbComponentRepository;

    @Autowired
    public NexusIQCollectorTask(TaskScheduler taskScheduler,
                                NexusIQCollectorRepository nexusIQCollectorRepository,
                                NexusIQApplicationRepository nexusIQApplicationRepository,
                                LibraryPolicyResultsRepository libraryPolicyResultsRepository,
                                NexusIQSettings nexusIQSettings,
                                NexusIQClient nexusIQClient,
                                ComponentRepository dbComponentRepository) {
        super(taskScheduler, "NexusIQ");
        this.nexusIQCollectorRepository = nexusIQCollectorRepository;
        this.nexusIQApplicationRepository = nexusIQApplicationRepository;
        this.libraryPolicyResultsRepository = libraryPolicyResultsRepository;
        this.nexusIQSettings = nexusIQSettings;
        this.nexusIQClient = nexusIQClient;
        this.dbComponentRepository = dbComponentRepository;
    }

    @Override
    public NexusIQCollector getCollector() {
        return NexusIQCollector.prototype(nexusIQSettings.getServers());
    }

    @Override
    public BaseCollectorRepository<NexusIQCollector> getCollectorRepository() {
        return nexusIQCollectorRepository;
    }

    @Override
    public String getCron() {
        return nexusIQSettings.getCron();
    }

    @Override
    public void collect(NexusIQCollector collector) {
        long start = System.currentTimeMillis();

        Set<ObjectId> udId = new HashSet<>();
        udId.add(collector.getId());
        List<NexusIQApplication> existingApplications = nexusIQApplicationRepository.findByCollectorIdIn(udId);
        List<NexusIQApplication> latestApplications = new ArrayList<>();

        clean(collector, existingApplications);

        for (String instanceUrl : collector.getNexusIQServers()) {
            logBanner(instanceUrl);

            List<NexusIQApplication> applications = nexusIQClient.getApplications(instanceUrl);
            latestApplications.addAll(applications);

            log("Fetched projects   " + applications.size(), start);

            addNewApplications(applications, existingApplications, collector);

            applications = enabledApplications(collector, instanceUrl);

//            For testing only.
//            applications = getSavedApplcations(collector, instanceUrl);
            refreshData(applications);

            log("Finished", start);
        }
        deleteUnwantedJobs(latestApplications, existingApplications, collector);
    }

    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts") // agreed PMD, fixme
    private void clean(NexusIQCollector collector, List<NexusIQApplication> nexusIQApplications) {
        Set<ObjectId> uniqueIDs = new HashSet<>();
        for (com.capitalone.dashboard.model.Component comp : dbComponentRepository
                .findAll()) {
            if (comp.getCollectorItems() != null && !comp.getCollectorItems().isEmpty()) {
                List<CollectorItem> itemList = comp.getCollectorItems().get(
                        CollectorType.LibraryPolicy);
                if (itemList != null) {
                    for (CollectorItem ci : itemList) {
                        if (ci != null && ci.getCollectorId().equals(collector.getId())) {
                            uniqueIDs.add(ci.getId());
                        }
                    }
                }
            }
        }
        List<NexusIQApplication> stateChangeAppList = new ArrayList<>();
        Set<ObjectId> udId = new HashSet<>();
        udId.add(collector.getId());
        for (NexusIQApplication app : nexusIQApplications) {
            // collect the jobs that need to change state : enabled vs disabled.
            if ((app.isEnabled() && !uniqueIDs.contains(app.getId())) ||  // if it was enabled but not on a dashboard
                    (!app.isEnabled() && uniqueIDs.contains(app.getId()))) { // OR it was disabled and now on a dashboard
                app.setEnabled(uniqueIDs.contains(app.getId()));
                stateChangeAppList.add(app);
            }
        }
        if (!CollectionUtils.isEmpty(stateChangeAppList)) {
            nexusIQApplicationRepository.save(stateChangeAppList);
        }
    }


    private void deleteUnwantedJobs(List<NexusIQApplication> latestApplications, List<NexusIQApplication> existingApplications, NexusIQCollector collector) {
        List<NexusIQApplication> deleteJobList = new ArrayList<>();

        // First delete collector items that are not supposed to be collected anymore because the servers have moved(?)
        for (NexusIQApplication application : existingApplications) {
            if (application.isPushed()) continue; // do not delete jobs that are being pushed via API
            if (!collector.getNexusIQServers().contains(application.getInstanceUrl()) ||
                    (!application.getCollectorId().equals(collector.getId())) ||
                    (!latestApplications.contains(application))) {
                deleteJobList.add(application);
            }
        }
        if (!CollectionUtils.isEmpty(deleteJobList)) {
            nexusIQApplicationRepository.delete(deleteJobList);
        }
    }

    private void refreshData(List<NexusIQApplication> applications) {
        long start = System.currentTimeMillis();
        int count = 0;
        for (NexusIQApplication app : applications) {
            List<LibraryPolicyReport> reports = nexusIQClient.getApplicationReport(app);
            if (CollectionUtils.isEmpty(reports)) continue;
            boolean appUpdated = false;
            for (LibraryPolicyReport report : reports) {
                if (report.getStage().equalsIgnoreCase("build")) {
                    if (isNewReport(app, report.getEvaluationDate())) {

                        LibraryPolicyResult lpr = nexusIQClient.getDetailedReport(report.getReportDataUrl());

                        if (lpr == null) continue;

                        lpr.setCollectorItemId(app.getId());
                        lpr.setReportUrl(report.getReportUIUrl());
                        lpr.setEvaluationTimestamp(report.getEvaluationDate());
                        lpr.setTimestamp(System.currentTimeMillis());
                        libraryPolicyResultsRepository.save(lpr);
                        appUpdated = true;
                        count++;
                    }
                }
            }
            if (appUpdated) {
                app.setLastUpdated(System.currentTimeMillis());
            }

        }
        log("Updated", start, count);
    }


    private List<NexusIQApplication> enabledApplications(NexusIQCollector collector, String instanceUrl) {
        return nexusIQApplicationRepository.findEnabledApplications(collector.getId(), instanceUrl);
    }

    private List<NexusIQApplication> getSavedApplcations(NexusIQCollector collector, String instanceUrl) {
        Iterable<NexusIQApplication> iterable = nexusIQApplicationRepository.findAll();
        List<NexusIQApplication> returnList = new ArrayList<>();
        iterable.forEach(returnList::add);
        return returnList;
    }

    private void addNewApplications(List<NexusIQApplication> applications, List<NexusIQApplication> existingApplications, NexusIQCollector collector) {
        long start = System.currentTimeMillis();
        int count = 0;
        List<NexusIQApplication> newApplications = new ArrayList<>();
        for (NexusIQApplication application : applications) {
            if (!existingApplications.contains(application)) {
                application.setCollectorId(collector.getId());
                application.setEnabled(true);
                application.setLastUpdated(System.currentTimeMillis());
                newApplications.add(application);
                count++;
            }
        }
        //save all in one shot
        if (!CollectionUtils.isEmpty(newApplications)) {
            nexusIQApplicationRepository.save(newApplications);
        }
        log("New projects", start, count);
    }

    private boolean isNewReport(NexusIQApplication application, long newReportTime) {
        LibraryPolicyResult oldResult = libraryPolicyResultsRepository.findByCollectorItemId(application.getId());
        return (oldResult == null) ||  (oldResult.getEvaluationTimestamp() < newReportTime);
    }
}
