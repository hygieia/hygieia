package com.capitalone.dashboard.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.FeatureCollector;
import com.capitalone.dashboard.model.Project;
import com.capitalone.dashboard.model.UpdateResult;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.util.FeatureCollectorConstants;


/**
 * Collects {@link FeatureCollector} data from feature content source system.
 */
@Component
public class FeatureCollectorTask extends CollectorTask<FeatureCollector> {
	private static final Logger LOGGER = LoggerFactory.getLogger(FeatureCollectorTask.class);

	private final FeatureCollectorRepository featureCollectorRepository;
	private final FeatureSettings featureSettings;
	private final FeatureService featureService;

	/**
	 * Default constructor for the collector task. This will construct this
	 * collector task with all repository, scheduling, and settings
	 * configurations custom to this collector.
	 *
	 * @param taskScheduler
	 *            A task scheduler artifact
	 * @param teamRepository
	 *            The repository being use for feature collection
	 * @param featureSettings
	 *            The settings being used for feature collection from the source
	 *            system
	 * @throws HygieiaException
	 */
	@Autowired
	public FeatureCollectorTask(TaskScheduler taskScheduler, FeatureCollectorRepository featureCollectorRepository,
			FeatureSettings featureSettings, FeatureService featureService) throws HygieiaException {
		super(taskScheduler, FeatureCollectorConstants.GITLAB);
		this.featureCollectorRepository = featureCollectorRepository;
		this.featureSettings = featureSettings;
		this.featureService = featureService;
	}

	/**
	 * Accessor method for the collector prototype object
	 */
	@Override
	public FeatureCollector getCollector() {
		return FeatureCollector.prototype();
	}

	/**
	 * Accessor method for the collector repository
	 */
	@Override
	public BaseCollectorRepository<FeatureCollector> getCollectorRepository() {
		return featureCollectorRepository;
	}

	/**
	 * Accessor method for the current chronology setting, for the scheduler
	 */
	@Override
	public String getCron() {
		return featureSettings.getCron();
	}

	/**
	 * The collection action. This is the task which will run on a schedule to
	 * gather data from the feature content source system and update the
	 * repository with retrieved data.
	 */
	@Override
	public void collect(FeatureCollector collector) {
		logBanner("Starting...");
		Long startTime = System.currentTimeMillis();
		
		Set<Project> projects = featureService.getProjectsToUpdate(collector.getId());
		List<Future<UpdateResult>> updateIssuesFutures = updateIssuesForProjects(collector, projects);
		logResults(updateIssuesFutures, startTime);
	}
    
    private List<Future<UpdateResult>> updateIssuesForProjects(FeatureCollector collector, Set<Project> projects) {
        List<Future<UpdateResult>> updateIssuesFutures = new ArrayList<>();
        for (Project project : projects) {
            updateIssuesFutures.add(featureService.updateIssuesForProject(collector, project));
        }
        return updateIssuesFutures;
    }

	private void logResults(List<Future<UpdateResult>> futures, long startTime) {
		UpdateResult result = new UpdateResult(0, 0);
		futures.forEach(future -> {
			try {
				result.add(future.get());
			} catch (InterruptedException | ExecutionException e) {
				LOGGER.error(e.getMessage());
			}
		});

		log("Issues Added/Updated", startTime, result.getItemsAdded());
		log("Issues Deleted", startTime, result.getItemsDeleted());
	}

}
