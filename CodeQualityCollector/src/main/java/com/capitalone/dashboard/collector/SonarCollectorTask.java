package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.SonarCollectorRepository;
import com.capitalone.dashboard.repository.SonarProjectRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SonarCollectorTask extends CollectorTask<SonarCollector> {
    private static final Log LOG = LogFactory.getLog(SonarCollectorTask.class);

    private final SonarCollectorRepository sonarCollectorRepository;
    private final SonarProjectRepository sonarProjectRepository;
    private final CodeQualityRepository codeQualityRepository;
    private final SonarClient sonarClient;
    private final SonarSettings sonarSettings;

    @Autowired
    public SonarCollectorTask(TaskScheduler taskScheduler,
                              SonarCollectorRepository sonarCollectorRepository,
                              SonarProjectRepository sonarProjectRepository,
                              CodeQualityRepository codeQualityRepository,
                              SonarSettings sonarSettings,
                              SonarClient sonarClient) {
        super(taskScheduler, "Sonar");
        this.sonarCollectorRepository = sonarCollectorRepository;
        this.sonarProjectRepository = sonarProjectRepository;
        this.codeQualityRepository = codeQualityRepository;
        this.sonarSettings = sonarSettings;
        this.sonarClient = sonarClient;
    }

    @Override
    public SonarCollector getCollector() {
        return SonarCollector.prototype(sonarSettings.getServers());
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
        for (String instanceUrl : collector.getSonarServers()) {
            logInstanceBanner(instanceUrl);

            long start = System.currentTimeMillis();

            List<SonarProject> projects = sonarClient.getProjects(instanceUrl);
            log("Fetched projects", start);

            addNewProjects(projects, collector);

            refreshData(enabledProjects(collector, instanceUrl));

            log("Finished", start);
        }
    }

    private void refreshData(List<SonarProject> sonarProjects) {
        long start = System.currentTimeMillis();
        int count = 0;

        for (SonarProject project : sonarProjects) {
            CodeQuality codeQuality = sonarClient.currentCodeQuality(project);
            if ((codeQuality != null) && isNewQualityData(project, codeQuality)) {
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

    private void addNewProjects(List<SonarProject> projects, SonarCollector collector) {
        long start = System.currentTimeMillis();
        int count = 0;

        for (SonarProject project : projects) {

            if (isNewProject(collector, project)) {
                project.setCollectorId(collector.getId());
                project.setEnabled(false);
                project.setDescription(project.getProjectName());
                sonarProjectRepository.save(project);
                count++;
            }
        }
        log("New projects", start, count);
    }

    private boolean isNewProject(SonarCollector collector, SonarProject application) {
        return sonarProjectRepository.findSonarProject(
                collector.getId(), application.getInstanceUrl(), application.getProjectId()) == null;
    }

    private boolean isNewQualityData(SonarProject project, CodeQuality codeQuality) {
        return codeQualityRepository.findByCollectorItemIdAndTimestamp(
                project.getId(), codeQuality.getTimestamp()) == null;
    }

    private void log(String marker, long start) {
        log(marker, start, null);
    }

    private void log(String text, long start, Integer count) {
        long end = System.currentTimeMillis();
        String elapsed = ((end - start) / 1000) + "s";
        String token2 = "";
        String token3;
        if (count == null) {
            token3 = StringUtils.leftPad(elapsed, 30 - text.length());
        } else {
            String countStr = count.toString();
            token2 = StringUtils.leftPad(countStr, 20 - text.length() );
            token3 = StringUtils.leftPad(elapsed, 10 );
        }
        LOG.info(text + token2 + token3);
    }

    private void logInstanceBanner(String instanceUrl) {
        LOG.info("------------------------------");
        LOG.info(instanceUrl);
        LOG.info("------------------------------");
    }
}
