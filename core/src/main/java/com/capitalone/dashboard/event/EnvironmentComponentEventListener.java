package com.capitalone.dashboard.event;


import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.*;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;

import java.util.Collections;
import java.util.List;

@org.springframework.stereotype.Component
public class EnvironmentComponentEventListener extends HygieiaMongoEventListener<EnvironmentComponent> {

    private final DashboardRepository dashboardRepository;
    private final ComponentRepository componentRepository;
    private final BinaryArtifactRepository binaryArtifactRepository;

    @Autowired
    public EnvironmentComponentEventListener(DashboardRepository dashboardRepository,
                              CollectorItemRepository collectorItemRepository,
                              ComponentRepository componentRepository,
                              BinaryArtifactRepository binaryArtifactRepository,
                              PipelineRepository pipelineRepository,
                              CollectorRepository collectorRepository) {
        super(collectorItemRepository, pipelineRepository, collectorRepository);
        this.dashboardRepository = dashboardRepository;
        this.componentRepository = componentRepository;
        this.binaryArtifactRepository = binaryArtifactRepository;
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
    private void addCommitsToEnvironmentStage(EnvironmentComponent environmentComponent, Pipeline pipeline){
        EnvironmentStage currentStage = getOrCreateEnvironmentStage(pipeline, environmentComponent.getEnvironmentName());

        Iterable<BinaryArtifact> artifacts;
        BinaryArtifact oldLastArtifact = currentStage.getLastArtifact();
        if(oldLastArtifact != null){
            Long lastArtifactTimestamp = oldLastArtifact != null ? oldLastArtifact.getTimestamp() : null;
            artifacts = binaryArtifactRepository.findByArtifactNameAndTimestampGreaterThan(environmentComponent.getComponentName(), lastArtifactTimestamp);
        }
        else{
            artifacts = binaryArtifactRepository.findByArtifactName(environmentComponent.getComponentName());
        }

        /**
         * Sort the artifacts by timestamp and iterate through each artifact, getting their changesets and adding them to the bucket
         */
        List<BinaryArtifact> sortedArtifacts = Lists.newArrayList(artifacts);
        Collections.sort(sortedArtifacts, BinaryArtifact.TIMESTAMP_COMPARATOR);

        for(BinaryArtifact artifact : sortedArtifacts){
            for(SCM scm : artifact.getBuildInfo().getSourceChangeSet()){
                PipelineCommit commit = new PipelineCommit(scm, environmentComponent.getAsOfDate());
                pipeline.addCommit(environmentComponent.getEnvironmentName(), commit);
            }
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
}
