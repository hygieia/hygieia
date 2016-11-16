package com.capitalone.dashboard.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.FeatureCollector;
import com.capitalone.dashboard.model.GitlabProject;
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
    private final FeatureUpdateService featureUpdateService;

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
			FeatureSettings featureSettings, FeatureUpdateService featureUpdateService) throws HygieiaException {
		super(taskScheduler, FeatureCollectorConstants.GITLAB);
		this.featureCollectorRepository = featureCollectorRepository;
		this.featureSettings = featureSettings;
		this.featureUpdateService = featureUpdateService;
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
       	Long startTime = System.currentTimeMillis();
       	
        featureUpdateService.updateSelectableTeams();
        	
        //Update Project info for enabled teams
        List<GitlabProject> projects = featureUpdateService.updateProjectsForEnabledTeams(collector.getId());
        
        //Update Issues
        List<Future<String>> futures = new ArrayList<>();
        for(GitlabProject project : projects) {
        	futures.add(featureUpdateService.updateIssuesForProject(project));
        }
        waitForCompletion(futures);
        
        Long elapsedTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime);
        LOGGER.info("Feature data collection finished in {} seconds.", elapsedTime);
    }

	private void waitForCompletion(List<Future<String>> futures) {
		boolean isDone = false;
        while (!isDone) {
        	for(Future<String> future : futures) {
        		if(!future.isDone()) {
        			try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						LOGGER.error(e.getMessage());;
					}
        			break;
        		}
        		isDone = true;
        	}
        }
	}

}
