package com.capitalone.dashboard.collector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.FeatureCollector;
import com.capitalone.dashboard.model.GitlabTeam;
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
    private final GitlabClient gitlabClient;
    private final FeatureDataClient featureDataClient;
    private final FeatureSettings featureSettings;

    /**
     * Default constructor for the collector task. This will construct this
     * collector task with all repository, scheduling, and settings
     * configurations custom to this collector.
     *
     * @param taskScheduler   A task scheduler artifact
     * @param teamRepository  The repository being use for feature collection
     * @param featureSettings The settings being used for feature collection from the source
     *                        system
     * @throws HygieiaException
     */
	@Autowired
	public FeatureCollectorTask(TaskScheduler taskScheduler, FeatureCollectorRepository featureCollectorRepository,
			GitlabClient gitlabClient, FeatureDataClient featureDataClient, FeatureSettings featureSettings)
			throws HygieiaException {
		super(taskScheduler, FeatureCollectorConstants.GITLAB);
		this.featureCollectorRepository = featureCollectorRepository;
		this.gitlabClient = gitlabClient;
		this.featureDataClient = featureDataClient;
		this.featureSettings = featureSettings;
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
        LOGGER.info("Starting Feature collection...");

        	
        //Update Team Info
        GitlabTeam[] teams = gitlabClient.getTeams();
        featureDataClient.updateTeams(teams);
        	
        	//Update Project Info
        	
        	//Update Story Info



        LOGGER.info("Feature Data Collection Finished");

    }

}
