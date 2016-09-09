package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.datafactory.versionone.VersionOneDataFactoryImpl;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.FeatureCollector;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.ScopeRepository;
import com.capitalone.dashboard.repository.ScopeOwnerRepository;
import com.capitalone.dashboard.util.FeatureCollectorConstants;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Collects {@link FeatureCollector} data from feature content source system.
 */
@Component
public class FeatureCollectorTask extends CollectorTask<FeatureCollector> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureCollectorTask.class);

    private final FeatureRepository featureRepository;
    private final ScopeOwnerRepository teamRepository;
    private final ScopeRepository projectRepository;
    private final FeatureCollectorRepository featureCollectorRepository;
    private final FeatureSettings featureSettings;
    private final VersionOneDataFactoryImpl v1Connection;

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
    public FeatureCollectorTask(TaskScheduler taskScheduler, FeatureRepository featureRepository,
                                ScopeOwnerRepository teamRepository, ScopeRepository projectRepository,
                                FeatureCollectorRepository featureCollectorRepository, FeatureSettings featureSettings)
            throws HygieiaException {
        super(taskScheduler, FeatureCollectorConstants.VERSIONONE);
        this.featureCollectorRepository = featureCollectorRepository;
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.featureRepository = featureRepository;
        this.featureSettings = featureSettings;
        this.v1Connection = connectToPersistentClient();
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

        try {
            TeamDataClient teamData = new TeamDataClient(this.featureCollectorRepository,
                    this.featureSettings, this.teamRepository, this.v1Connection);

            teamData.updateTeamInformation();

            ProjectDataClient projectData = new ProjectDataClient(this.featureSettings,
                    this.projectRepository, this.featureCollectorRepository, this.v1Connection);
            projectData.updateProjectInformation();

            StoryDataClient storyData = new StoryDataClient(this.featureSettings,
                    this.featureRepository, this.featureCollectorRepository, this.v1Connection);
            storyData.updateStoryInformation();
        } catch (HygieiaException he) {
            LOGGER.error("Error in collecting Version One Data: [" + he.getErrorCode() + "] "
                    + he.getMessage());
        }

        LOGGER.info("Feature Data Collection Finished");

    }

    private VersionOneDataFactoryImpl connectToPersistentClient() throws HygieiaException {
        Map<String, String> auth = new HashMap<>();

        if (StringUtils.isEmpty(featureSettings.getVersionOneAccessToken()) || StringUtils.isEmpty(featureSettings.getVersionOneBaseUri()))
            throw new HygieiaException("FAILED: VersionOne connection properties are not valid",
                    HygieiaException.INVALID_CONFIGURATION);

        auth.put("v1ProxyUrl", this.featureSettings.getVersionOneProxyUrl());
        auth.put("v1BaseUri", this.featureSettings.getVersionOneBaseUri());
        auth.put("v1AccessToken", this.featureSettings.getVersionOneAccessToken());

        return new VersionOneDataFactoryImpl(auth);
    }
}
