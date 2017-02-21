package com.capitalone.dashboard.collector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.capitalone.dashboard.gitlab.model.GitlabProject;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.FeatureCollector;
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
		Collection<GitlabProject> projects = featureService.getEnabledProjects(collector.getId());

		ListenableFuture<UpdateResult> updateTeamsFuture = featureService.updateSelectableTeams(collector.getId());
		updateTeamsFuture.addCallback(createCallback("Teams Added", "Teams Deleted", startTime));

		ListenableFuture<UpdateResult> updateProjectsFuture = featureService.updateProjects(collector.getId());
		updateProjectsFuture.addCallback(createCallback("Projects Added", "Projects Deleted", startTime));

		List<Future<UpdateResult>> updateIssuesFutures = new ArrayList<>();
		for (GitlabProject project : projects) {
			updateIssuesFutures.add(featureService.updateIssuesForProject(collector.getId(), collector.getLastExecuted(), project));
		}
		logResults(updateIssuesFutures, startTime);
	}

	private ListenableFutureCallback<UpdateResult> createCallback(String addedText, String deletedText,
			Long startTime) {
		return new ListenableFutureCallback<UpdateResult>() {

			@Override
			public void onSuccess(UpdateResult result) {
				log(addedText, startTime, result.getItemsAdded());
				log(deletedText, startTime, result.getItemsDeleted());
			}

			@Override
			public void onFailure(Throwable ex) {
				log(ex.getMessage());
			}

		};
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
