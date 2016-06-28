package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.client.JiraClient;
import com.capitalone.dashboard.client.project.ProjectDataClientImpl;
import com.capitalone.dashboard.client.story.StoryDataClientImpl;
import com.capitalone.dashboard.client.team.TeamDataClientImpl;
import com.capitalone.dashboard.model.FeatureCollector;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.ScopeRepository;
import com.capitalone.dashboard.repository.ScopeOwnerRepository;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import com.capitalone.dashboard.util.CoreFeatureSettings;
import com.capitalone.dashboard.util.FeatureSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

/**
 * Collects {@link FeatureCollector} data from feature content source system.
 * 
 * @author KFK884
 */
@Component
public class FeatureCollectorTask extends CollectorTask<FeatureCollector> {
	private static final Logger LOGGER = LoggerFactory.getLogger(FeatureCollectorTask.class);
	
	private final CoreFeatureSettings coreFeatureSettings;
	private final FeatureRepository featureRepository;
	private final ScopeOwnerRepository teamRepository;
	private final ScopeRepository projectRepository;
	private final FeatureCollectorRepository featureCollectorRepository;
	private final FeatureSettings featureSettings;
	private final JiraClient jiraClient;

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
	 */
	@Autowired
	public FeatureCollectorTask(CoreFeatureSettings coreFeatureSettings,
			TaskScheduler taskScheduler, FeatureRepository featureRepository,
			ScopeOwnerRepository teamRepository, ScopeRepository projectRepository,
			FeatureCollectorRepository featureCollectorRepository, FeatureSettings featureSettings,
			JiraClient jiraClient) {
		super(taskScheduler, FeatureCollectorConstants.JIRA);
		this.featureCollectorRepository = featureCollectorRepository;
		this.teamRepository = teamRepository;
		this.projectRepository = projectRepository;
		this.featureRepository = featureRepository;
		this.coreFeatureSettings = coreFeatureSettings;
		this.featureSettings = featureSettings;
		this.jiraClient = jiraClient;
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
		logBanner(featureSettings.getJiraBaseUrl());
		int count = 0;

		try {
			long teamDataStart = System.currentTimeMillis();
			TeamDataClientImpl teamData = new TeamDataClientImpl(this.featureCollectorRepository,
					this.featureSettings, this.teamRepository, jiraClient);
			count = teamData.updateTeamInformation();
			log("Team Data", teamDataStart, count);
	
			long projectDataStart = System.currentTimeMillis();
			ProjectDataClientImpl projectData = new ProjectDataClientImpl(this.featureSettings,
					this.projectRepository, this.featureCollectorRepository, jiraClient);
			count = projectData.updateProjectInformation();
			log("Project Data", projectDataStart, count);
	
			long storyDataStart = System.currentTimeMillis();
			StoryDataClientImpl storyData = new StoryDataClientImpl(this.coreFeatureSettings,
					this.featureSettings, this.featureRepository, this.featureCollectorRepository, jiraClient);
			count = storyData.updateStoryInformation();
			
			log("Story Data", storyDataStart, count);
			log("Finished", teamDataStart);
		} catch (Exception e) {
			// catch exception here so we don't blow up the collector completely
			LOGGER.error("Failed to collect jira information", e);
		}
	}
}
