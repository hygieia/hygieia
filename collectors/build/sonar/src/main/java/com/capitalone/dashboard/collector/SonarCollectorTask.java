package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.SonarCollector;
import com.capitalone.dashboard.model.SonarProject;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.SonarCollectorRepository;
import com.capitalone.dashboard.repository.SonarProjectRepository;
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
public class SonarCollectorTask extends CollectorTask<SonarCollector> {
    @SuppressWarnings({ "PMD.UnusedPrivateField", "unused" })
    private static final Log LOG = LogFactory.getLog(SonarCollectorTask.class);

    private final SonarCollectorRepository sonarCollectorRepository;
    private final SonarProjectRepository sonarProjectRepository;
    private final CodeQualityRepository codeQualityRepository;
    private final SonarClientSelector sonarClientSelector;
    private final SonarSettings sonarSettings;
    private final ComponentRepository dbComponentRepository;

    @Autowired
    public SonarCollectorTask(TaskScheduler taskScheduler,
                              SonarCollectorRepository sonarCollectorRepository,
                              SonarProjectRepository sonarProjectRepository,
                              CodeQualityRepository codeQualityRepository,
                              SonarSettings sonarSettings,
                              SonarClientSelector sonarClientSelector,
                              ComponentRepository dbComponentRepository) {
        super(taskScheduler, "Sonar");
        this.sonarCollectorRepository = sonarCollectorRepository;
        this.sonarProjectRepository = sonarProjectRepository;
        this.codeQualityRepository = codeQualityRepository;
        this.sonarSettings = sonarSettings;
        this.sonarClientSelector = sonarClientSelector;
        this.dbComponentRepository = dbComponentRepository;
    }

    @Override
    public SonarCollector getCollector() {
        return SonarCollector.prototype(sonarSettings.getServers(), sonarSettings.getVersions(), sonarSettings.getMetrics());
    }

    @Override
    public BaseCollectorRepository<SonarCollector> getCollectorRepository() {
        return sonarCollectorRepository;
    }

    @Override
    public String getCron() {
        return sonarSettings.getCron();
    }

    @Override
    public void collect(SonarCollector collector) {
        long start = System.currentTimeMillis();

        Set<ObjectId> udId = new HashSet<>();
        udId.add(collector.getId());
        List<SonarProject> existingProjects = sonarProjectRepository.findByCollectorIdIn(udId);
        List<SonarProject> latestProjects = new ArrayList<>();
        clean(collector, existingProjects);

        if (!CollectionUtils.isEmpty(collector.getSonarServers())) {
            
            for (int i = 0; i < collector.getSonarServers().size(); i++) {

                String instanceUrl = collector.getSonarServers().get(i);
                Double version = collector.getSonarVersions().get(i);
                String metrics = collector.getSonarMetrics().get(i);

                logBanner(instanceUrl);
                SonarClient sonarClient = sonarClientSelector.getSonarClient(version);
                List<SonarProject> projects = sonarClient.getProjects(instanceUrl);
                latestProjects.addAll(projects);

                int projSize = ((CollectionUtils.isEmpty(projects)) ? 0 : projects.size());
                log("Fetched projects   " + projSize, start);

                addNewProjects(projects, existingProjects, collector);

                refreshData(enabledProjects(collector, instanceUrl), sonarClient,metrics);

                log("Finished", start);
            }
        }
        deleteUnwantedJobs(latestProjects, existingProjects, collector);
    }


	/**
	 * Clean up unused sonar collector items
	 *
	 * @param collector
	 *            the {@link SonarCollector}
	 */

    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts") // agreed PMD, fixme
    private void clean(SonarCollector collector, List<SonarProject> existingProjects) {
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
        List<SonarProject> stateChangeJobList = new ArrayList<>();
        Set<ObjectId> udId = new HashSet<>();
        udId.add(collector.getId());
        for (SonarProject job : existingProjects) {
            // collect the jobs that need to change state : enabled vs disabled.
            if ((job.isEnabled() && !uniqueIDs.contains(job.getId())) ||  // if it was enabled but not on a dashboard
                    (!job.isEnabled() && uniqueIDs.contains(job.getId()))) { // OR it was disabled and now on a dashboard
                job.setEnabled(uniqueIDs.contains(job.getId()));
                stateChangeJobList.add(job);
            }
        }
        if (!CollectionUtils.isEmpty(stateChangeJobList)) {
            sonarProjectRepository.save(stateChangeJobList);
        }
    }


    private void deleteUnwantedJobs(List<SonarProject> latestProjects, List<SonarProject> existingProjects, SonarCollector collector) {
        List<SonarProject> deleteJobList = new ArrayList<>();

        // First delete collector items that are not supposed to be collected anymore because the servers have moved(?)
        for (SonarProject job : existingProjects) {
            if (job.isPushed()) continue; // do not delete jobs that are being pushed via API
            if (!collector.getSonarServers().contains(job.getInstanceUrl()) ||
                    (!job.getCollectorId().equals(collector.getId())) ||
                    (!latestProjects.contains(job))) {
                deleteJobList.add(job);
            }
        }
        if (!CollectionUtils.isEmpty(deleteJobList)) {
            sonarProjectRepository.delete(deleteJobList);
        }
    }

    private void refreshData(List<SonarProject> sonarProjects, SonarClient sonarClient, String metrics) {
        long start = System.currentTimeMillis();
        int count = 0;

        for (SonarProject project : sonarProjects) {
            CodeQuality codeQuality = sonarClient.currentCodeQuality(project, metrics);
            if (codeQuality != null && isNewQualityData(project, codeQuality)) {
                codeQuality.setCollectorItemId(project.getId());
                codeQualityRepository.save(codeQuality);
                count++;
            }
        }
        log("Updated", start, count);
    }

    private List<SonarProject> enabledProjects(SonarCollector collector, String instanceUrl) {
        return sonarProjectRepository.findEnabledProjects(collector.getId(), instanceUrl);
    }

    private void addNewProjects(List<SonarProject> projects, List<SonarProject> existingProjects, SonarCollector collector) {
        long start = System.currentTimeMillis();
        int count = 0;
        List<SonarProject> newProjects = new ArrayList<>();
        for (SonarProject project : projects) {
            if (!existingProjects.contains(project)) {
                project.setCollectorId(collector.getId());
                project.setEnabled(false);
                project.setDescription(project.getProjectName());
                newProjects.add(project);
                count++;
            }
        }
        //save all in one shot
        if (!CollectionUtils.isEmpty(newProjects)) {
            sonarProjectRepository.save(newProjects);
        }
        log("New projects", start, count);
    }

    @SuppressWarnings("unused")
	private boolean isNewProject(SonarCollector collector, SonarProject application) {
        return sonarProjectRepository.findSonarProject(
                collector.getId(), application.getInstanceUrl(), application.getProjectId()) == null;
    }

    private boolean isNewQualityData(SonarProject project, CodeQuality codeQuality) {
        return codeQualityRepository.findByCollectorItemIdAndTimestamp(
                project.getId(), codeQuality.getTimestamp()) == null;
    }
}
