package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.AppdynamicsApplication;
import com.capitalone.dashboard.model.AppdynamicsCollector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Performance;
import com.capitalone.dashboard.repository.AppDynamicsApplicationRepository;
import com.capitalone.dashboard.repository.AppdynamicsCollectorRepository;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.PerformanceRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class AppdynamicsCollectorTask extends CollectorTask<AppdynamicsCollector> {


    private final AppdynamicsCollectorRepository appdynamicsCollectorRepository;
    private final AppDynamicsApplicationRepository appDynamicsApplicationRepository;
    private final PerformanceRepository performanceRepository;
    private final AppdynamicsClient appdynamicsClient;
    private final AppdynamicsSettings appdynamicsSettings;
    private final ComponentRepository dbComponentRepository;


    @Autowired
    public AppdynamicsCollectorTask(TaskScheduler taskScheduler,
                                    AppdynamicsCollectorRepository appdynamicsCollectorRepository,
                                    AppDynamicsApplicationRepository appDynamicsApplicationRepository,
                                    PerformanceRepository performanceRepository,
                                    AppdynamicsSettings appdynamicsSettings,
                                    AppdynamicsClient appdynamicsClient,
                                    ComponentRepository dbComponentRepository) {
        super(taskScheduler, "Appdynamics");
        this.appdynamicsCollectorRepository = appdynamicsCollectorRepository;
        this.appDynamicsApplicationRepository = appDynamicsApplicationRepository;
        this.performanceRepository = performanceRepository;
        this.appdynamicsSettings = appdynamicsSettings;
        this.appdynamicsClient = appdynamicsClient;
        this.dbComponentRepository = dbComponentRepository;
    }

    @Override
    public AppdynamicsCollector getCollector() {
        return AppdynamicsCollector.prototype(appdynamicsSettings.getAccess());
    }

    @Override
    public BaseCollectorRepository<AppdynamicsCollector> getCollectorRepository() {
        return appdynamicsCollectorRepository;
    }

    @Override
    public String getCron() {
        return appdynamicsSettings.getCron();
    }

    @Override
    public void collect(AppdynamicsCollector collector) {

        // long start = System.currentTimeMillis();
        refreshData(enabledProjects(collector));

/*
       Set<ObjectId> udId = new HashSet<>();
        udId.add(collector.getId());
        List<AppdynamicsApplication> existingProjects = appDynamicsApplicationRepository.findByCollectorIdIn(udId);
        List<AppdynamicsApplication> latestProjects = new ArrayList<>();
        clean(collector, existingProjects);

        for (String instanceUrl : collector.getAppdynamicsServers()) {
            logBanner(instanceUrl);

            List<AppdynamicsApplication> projects = appdynamicsClient.getApplications(appdynamicsSettings.getAccess());
            latestProjects.addAll(projects);

            int projSize = ((projects != null) ? projects.size() : 0);
            log("Fetched projects   " + projSize, start);

            addNewProjects(projects, existingProjects, collector);

            refreshData(enabledProjects(collector, instanceUrl));

            log("Finished", start);
        }
        deleteUnwantedJobs(latestProjects, existingProjects, collector);

        clean(collector.);*/
    }


	/**
	 * Clean up unused sonar collector items
	 *
	 * @param collector

	 */

    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts") // agreed PMD, fixme
    private void clean(AppdynamicsCollector collector, List<AppdynamicsApplication> existingProjects) {
        Set<ObjectId> uniqueIDs = new HashSet<>();
        for (com.capitalone.dashboard.model.Component comp : dbComponentRepository
                .findAll()) {
            if (comp.getCollectorItems() != null && !comp.getCollectorItems().isEmpty()) {
                List<CollectorItem> itemList = comp.getCollectorItems().get(
                        CollectorType.CodeQuality);
                if (itemList != null) {
                    for (CollectorItem ci : itemList) {
                        if (ci != null && ci.getCollectorId().equals(collector.getId())) {
                            uniqueIDs.add(ci.getId());
                        }
                    }
                }
            }
        }
        List<AppdynamicsApplication> stateChangeJobList = new ArrayList<>();
        Set<ObjectId> udId = new HashSet<>();
        udId.add(collector.getId());
        for (AppdynamicsApplication job : existingProjects) {
            // collect the jobs that need to change state : enabled vs disabled.
            if ((job.isEnabled() && !uniqueIDs.contains(job.getId())) ||  // if it was enabled but not on a dashboard
                    (!job.isEnabled() && uniqueIDs.contains(job.getId()))) { // OR it was disabled and now on a dashboard
                job.setEnabled(uniqueIDs.contains(job.getId()));
                stateChangeJobList.add(job);
            }
        }
        if (!CollectionUtils.isEmpty(stateChangeJobList)) {
            appDynamicsApplicationRepository.save(stateChangeJobList);
        }
    }


    /*  private void deleteUnwantedJobs(List<AppdynamicsApplication> latestProjects, List<AppdynamicsApplication> existingProjects, AppdynamicsCollector collector) {
          List<AppdynamicsApplication> deleteJobList = new ArrayList<>();

          // First delete collector items that are not supposed to be collected anymore because the servers have moved(?)
          for (AppdynamicsApplication job : existingProjects) {
              if (job.isPushed()) continue; // do not delete jobs that are being pushed via API
              if (!collector.getAppdynamicsServers().contains(job.getAppUrl()) ||
                      (!job.getCollectorId().equals(collector.getId())) ||
                      (!latestProjects.contains(job))) {
                  deleteJobList.add(job);
              }
          }
          if (!CollectionUtils.isEmpty(deleteJobList)) {
              appDynamicsApplicationRepository.delete(deleteJobList);
          }
      }
  */
    private void refreshData(List<AppdynamicsApplication> sonarProjects) {
        long start = System.currentTimeMillis();
        int count = 0;

        for (AppdynamicsApplication project : sonarProjects) {
            Performance performance = appdynamicsClient.getPerformanceMetrics(project, appdynamicsSettings.getAccess());
            if (performance != null && isNewQualityData(project, performance)) {
                performance.setCollectorItemId(project.getId());
                performanceRepository.save(performance);
                count++;
            }
        }
        log("Updated", start, count);
    }

    private List<AppdynamicsApplication> enabledProjects(AppdynamicsCollector collector) {
        return appDynamicsApplicationRepository.findEnabledAppdynamicsApplications(collector.getId());
    }

    private void addNewProjects(List<AppdynamicsApplication> projects, List<AppdynamicsApplication> existingProjects, AppdynamicsCollector collector) {
        long start = System.currentTimeMillis();
        int count = 0;
        List<AppdynamicsApplication> newProjects = new ArrayList<>();
        for (AppdynamicsApplication project : projects) {
            if (!existingProjects.contains(project)) {
                project.setCollectorId(collector.getId());
                project.setEnabled(false);
                project.setDescription(project.getAppName());
                newProjects.add(project);
                count++;
            }
        }
        //save all in one shot
        if (!CollectionUtils.isEmpty(newProjects)) {
            appDynamicsApplicationRepository.save(newProjects);
        }
        log("New projects", start, count);
    }

    @SuppressWarnings("unused")
	private boolean isNewProject(AppdynamicsCollector collector, AppdynamicsApplication application) {
        return appDynamicsApplicationRepository.findAppdynamicsApplicationByCollectorIdAndAppID(
                collector.getId(), application.getAppID()) == null;
    }

    private boolean isNewQualityData(AppdynamicsApplication project, Performance performance) {
        return performanceRepository.findByCollectorItemIdAndTimestamp(
                project.getId(), performance.getTimestamp()) == null;
    }
}
