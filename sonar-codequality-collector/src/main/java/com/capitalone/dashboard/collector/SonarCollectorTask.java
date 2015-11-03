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

import org.apache.commons.lang3.StringUtils;
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
    private static final Log LOG = LogFactory.getLog(SonarCollectorTask.class);

    private final SonarCollectorRepository sonarCollectorRepository;
    private final SonarProjectRepository sonarProjectRepository;
    private final CodeQualityRepository codeQualityRepository;
    private final SonarClient sonarClient;
    private final SonarSettings sonarSettings;
    private final ComponentRepository dbComponentRepository;
    private final int CLEANUP_INTERVAL = 3600000;

    @Autowired
    public SonarCollectorTask(TaskScheduler taskScheduler,
                              SonarCollectorRepository sonarCollectorRepository,
                              SonarProjectRepository sonarProjectRepository,
                              CodeQualityRepository codeQualityRepository,
                              SonarSettings sonarSettings,
                              SonarClient sonarClient,
                              ComponentRepository dbComponentRepository) {
        super(taskScheduler, "Sonar");
        this.sonarCollectorRepository = sonarCollectorRepository;
        this.sonarProjectRepository = sonarProjectRepository;
        this.codeQualityRepository = codeQualityRepository;
        this.sonarSettings = sonarSettings;
        this.sonarClient = sonarClient;
        this.dbComponentRepository = dbComponentRepository;
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
        long start = System.currentTimeMillis();

		// Clean up every hour
		if ((start - collector.getLastExecuted()) > CLEANUP_INTERVAL) {
			clean(collector);
		}
        for (String instanceUrl : collector.getSonarServers()) {
            logInstanceBanner(instanceUrl);



            List<SonarProject> projects = sonarClient.getProjects(instanceUrl);
            int projSize = ((projects != null) ? projects.size() : 0);
            log("Fetched projects   " + projSize , start);

            addNewProjects(projects, collector);

            refreshData(enabledProjects(collector, instanceUrl));

            log("Finished", start);
        }
    }


	/**
	 * Clean up unused sonar collector items
	 *
	 * @param collector
	 *            the {@link HudsonCollector}
	 */

	private void clean(SonarCollector collector) {
		Set<ObjectId> uniqueIDs = new HashSet<>();
		for (com.capitalone.dashboard.model.Component comp : dbComponentRepository
				.findAll()) {
			if (comp.getCollectorItems() != null && !comp.getCollectorItems().isEmpty()) {
				List<CollectorItem> itemList = comp.getCollectorItems().get(
						CollectorType.CodeQuality);
				if (itemList != null) {
					for (CollectorItem ci : itemList) {
						if (ci != null && ci.getCollectorId().equals(collector.getId())){
							uniqueIDs.add(ci.getId());
						}
					}
				}
			}
		}
		List<SonarProject> jobList = new ArrayList<>();
		Set<ObjectId> udId = new HashSet<>();
		udId.add(collector.getId());
		for (SonarProject job : sonarProjectRepository.findByCollectorIdIn(udId)) {
			if (job != null) {
				job.setEnabled(uniqueIDs.contains(job.getId()));
				jobList.add(job);
			}
		}
		sonarProjectRepository.save(jobList);
	}

    private void refreshData(List<SonarProject> sonarProjects) {
        long start = System.currentTimeMillis();
        int count = 0;

        for (SonarProject project : sonarProjects) {
            CodeQuality codeQuality = sonarClient.currentCodeQuality(project);
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
