package com.capitalone.dashboard.event;

import com.capitalone.dashboard.model.BinaryArtifact;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.EnvironmentComponent;
import com.capitalone.dashboard.model.EnvironmentStage;
import com.capitalone.dashboard.model.Pipeline;
import com.capitalone.dashboard.model.PipelineCommit;
import com.capitalone.dashboard.model.PipelineStage;
import com.capitalone.dashboard.model.SCM;
import com.capitalone.dashboard.repository.BinaryArtifactRepository;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.JobRepository;
import com.capitalone.dashboard.repository.PipelineRepository;
import com.capitalone.dashboard.util.PipelineUtils;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Component
public class EnvironmentComponentEventListener extends HygieiaMongoEventListener<EnvironmentComponent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentComponentEventListener.class);

    private final DashboardRepository dashboardRepository;
    private final ComponentRepository componentRepository;
    private final BinaryArtifactRepository binaryArtifactRepository;
    private final BuildRepository buildRepository;
    private final JobRepository<?> jobRepository;
    private final CommitRepository commitRepository;

    @Autowired
    public EnvironmentComponentEventListener(DashboardRepository dashboardRepository,
                              CollectorItemRepository collectorItemRepository,
                              ComponentRepository componentRepository,
                              BinaryArtifactRepository binaryArtifactRepository,
                              PipelineRepository pipelineRepository,
                              CollectorRepository collectorRepository,
                              BuildRepository buildRepository,
                              JobRepository<?> jobRepository, CommitRepository commitRepository) {
        super(collectorItemRepository, pipelineRepository, collectorRepository);
        this.dashboardRepository = dashboardRepository;
        this.componentRepository = componentRepository;
        this.binaryArtifactRepository = binaryArtifactRepository;
        this.buildRepository = buildRepository;
        this.jobRepository = jobRepository;
        this.commitRepository = commitRepository;
    }

    @Override
    public void onAfterSave(AfterSaveEvent<EnvironmentComponent> event) {
        super.onAfterSave(event);

        EnvironmentComponent environmentComponent = event.getSource();
        if(!environmentComponent.isDeployed()){
            return;
        }

        processEnvironmentComponent(environmentComponent);
    }

    /**
     * For the environment component, find all team dashboards related to the environment component and add the
     * commits to the proper stage
     * @param environmentComponent
     */
    private void processEnvironmentComponent(EnvironmentComponent environmentComponent) {
        List<Dashboard> dashboards = findTeamDashboardsForEnvironmentComponent(environmentComponent);

        for (Dashboard dashboard : dashboards) {
            Pipeline pipeline = getOrCreatePipeline(dashboard);

        	if (LOGGER.isDebugEnabled()) {
        		LOGGER.debug("Attempting to update pipeline " + pipeline.getId());
        	}
            
            addCommitsToEnvironmentStage(environmentComponent, pipeline);
            pipelineRepository.save(pipeline);
        }

    }

    /**
     * Must first start by finding all artifacts that relate to an environment component based on the name, and potentially
     * the timestamp of the last time an artifact came through the environment.
     *
     * Multiple artifacts could have been built but never deployed.
     * @param environmentComponent
     * @param pipeline
     */
    @SuppressWarnings("PMD.NPathComplexity")
    private void addCommitsToEnvironmentStage(EnvironmentComponent environmentComponent, Pipeline pipeline){
        EnvironmentStage currentStage = getOrCreateEnvironmentStage(pipeline, environmentComponent.getEnvironmentName());
        String pseudoEnvName = environmentComponent.getEnvironmentName();
        if (LOGGER.isDebugEnabled()) {
        	LOGGER.debug("Attempting to find new artifacts to process for environment '" + environmentComponent.getEnvironmentName() + "'");
        }
        
        String artifactName = environmentComponent.getComponentName();
        String artifactExtension = null;
        int dotIdx = artifactName.lastIndexOf('.');
        if (dotIdx > 0) {
        	// If idx is 0 starts with a dot... in which case not an extension
            artifactExtension = artifactName.substring(dotIdx + 1);
            artifactName = artifactName.substring(0, dotIdx);

        }

        List<BinaryArtifact> artifacts = new ArrayList<>();
        BinaryArtifact oldLastArtifact = currentStage.getLastArtifact();
        if(oldLastArtifact != null){
            Long lastArtifactTimestamp = oldLastArtifact != null ? oldLastArtifact.getTimestamp() : null;
            artifacts.addAll(Lists.newArrayList(binaryArtifactRepository.findByArtifactNameAndArtifactExtensionAndTimestampGreaterThan(artifactName, artifactExtension, lastArtifactTimestamp)));
            
            // Backwards compatibility
            if (artifactExtension != null) {
	        	// In the past the extension was saved as part of the artifact name
	            artifacts.addAll(Lists.newArrayList(binaryArtifactRepository.findByArtifactNameAndArtifactExtensionAndTimestampGreaterThan(environmentComponent.getComponentName(), null, lastArtifactTimestamp)));
            }
        }
        else {
        	Map<String, Object> attributes = new HashMap<>();
        	attributes.put(BinaryArtifactRepository.ARTIFACT_NAME, artifactName);
        	attributes.put(BinaryArtifactRepository.ARTIFACT_EXTENSION, artifactExtension);
        	
        	artifacts.addAll(Lists.newArrayList(binaryArtifactRepository.findByAttributes(attributes)));
        	
        	// Backwards compatibility
        	if (artifactExtension != null) {
	        	// In the past the extension was saved as part of the artifact name
	        	attributes.clear();
	        	attributes.put(BinaryArtifactRepository.ARTIFACT_NAME, environmentComponent.getComponentName());
	        	attributes.put(BinaryArtifactRepository.ARTIFACT_EXTENSION, null);
	        	artifacts.addAll(Lists.newArrayList(binaryArtifactRepository.findByAttributes(attributes)));
        	}
        }

        /**
         * Sort the artifacts by timestamp and iterate through each artifact, getting their changesets and adding them to the bucket
         */
        List<BinaryArtifact> sortedArtifacts = Lists.newArrayList(artifacts);
        Collections.sort(sortedArtifacts, BinaryArtifact.TIMESTAMP_COMPARATOR);

        for(BinaryArtifact artifact : sortedArtifacts){
        	if (LOGGER.isDebugEnabled()) {
        		LOGGER.debug("Processing artifact " + artifact.getArtifactGroupId() + ":" + artifact.getArtifactName() + ":" + artifact.getArtifactVersion());
        	}
        	
        	Build build = artifact.getBuildInfo();
        	
        	if (build == null) {
        		// Attempt to get the build based on the artifact metadata information if possible
        		build = getBuildByMetadata(artifact);
        	}
        	
        	if (build != null) {
				for (SCM scm : build.getSourceChangeSet()) {
					PipelineCommit commit = new PipelineCommit(scm, environmentComponent.getAsOfDate());
					pipeline.addCommit(environmentComponent.getEnvironmentName(), commit);
				}
        	}
            PipelineUtils.processPreviousFailedBuilds(build, pipeline);
            /**
             * If some build events are missed, here is an attempt to move commits to the build stage
             * This also takes care of the problem with Jenkins first build change set being empty.
             *
             * Logic:
             * If the build start time is after the scm commit, move the commit to build stage. Match the repo at the very least.
             */
            Map<String, PipelineCommit> commitStageCommits = pipeline.getCommitsByEnvironmentName(PipelineStage.COMMIT.getName());
            Map<String, PipelineCommit> envStageCommits = pipeline.getCommitsByEnvironmentName(pseudoEnvName);
            for (String rev : commitStageCommits.keySet()) {
                PipelineCommit commit = commitStageCommits.get(rev);
                if ((commit.getScmCommitTimestamp() < build.getStartTime()) && !envStageCommits.containsKey(rev) && PipelineUtils.isMoveCommitToBuild(build, commit, commitRepository)) {
                    pipeline.addCommit(pseudoEnvName, commit);
                }
            }
            pipelineRepository.save(pipeline);

        }
        /**
         * Update last artifact on the pipeline
         */
        if(sortedArtifacts != null && !sortedArtifacts.isEmpty()){
            BinaryArtifact lastArtifact = sortedArtifacts.get(sortedArtifacts.size() - 1);
            currentStage.setLastArtifact(lastArtifact);
        }
    }

    /**
     * Attempts to find the build for the artifact based on the artifacts build metadata information.
     * 
     * @param artifact
     * @return
     */
    private Build getBuildByMetadata(BinaryArtifact artifact) {
    	Build build = null;
    	
    	// Note: in order to work properly both the artifact and the build must exist when this is run
    	// This shouldn't be a problem as they would exist by the time the component is deployed so
    	// long as the collector frequency allowed the information to be picked up
    	String jobName = null;
    	String buildNumber = null;
    	String instanceUrl = null;
    	
    	if (artifact.getMetadata() != null) {
    		jobName = artifact.getJobName();
    		buildNumber = artifact.getBuildNumber();
    		instanceUrl = artifact.getInstanceUrl();
    	}
    	
    	if (jobName != null && buildNumber != null && instanceUrl != null) {
        	List<Collector> buildCollectors = collectorRepository.findByCollectorType(CollectorType.Build);
        	List<ObjectId> collectorIds = Lists.newArrayList(Iterables.transform(buildCollectors, new ToCollectorId()));
        	
        	// Just in case more build collectors are added in the future that run together...
        	for (ObjectId buildCollectorId : collectorIds) {
            	CollectorItem jobCollectorItem = jobRepository.findJob(buildCollectorId, instanceUrl, jobName);
            	
            	if (jobCollectorItem == null) {
            		continue;
            	}
            	
            	build = buildRepository.findByCollectorItemIdAndNumber(jobCollectorItem.getId(), buildNumber);
            	
            	if (build != null) {
            		break;
            	}
        	}
    	} else {
    		LOGGER.debug("Artifact " + artifact.getId() + " is missing build information.");
    	}
    	
    	if (build == null) {
    		LOGGER.debug("Artifact " + artifact.getId() + " references build " + buildNumber + " in '" + instanceUrl + "' but no build with that information was found.");
    	}
    	
    	return build;
    }

    /**
     * Finds team dashboards for a given environment componentby way of the deploy collector item
     * @param environmentComponent
     * @return
     */
    private List<Dashboard> findTeamDashboardsForEnvironmentComponent(EnvironmentComponent environmentComponent){
        List<Dashboard> dashboards;
        CollectorItem deploymentCollectorItem = collectorItemRepository.findOne(environmentComponent.getCollectorItemId());
        List<Component> components = componentRepository.findByDeployCollectorItemId(deploymentCollectorItem.getId());
        dashboards = dashboardRepository.findByApplicationComponentsIn(components);
        return dashboards;
    }
    
    private static class ToCollectorId implements Function<Collector, ObjectId> {
        @Override
        public ObjectId apply(Collector input) {
            return input.getId();
        }
    }
}
