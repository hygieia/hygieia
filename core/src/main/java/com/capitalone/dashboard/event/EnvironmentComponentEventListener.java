package com.capitalone.dashboard.event;


import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.*;
import com.google.common.collect.Lists;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("PMD")
@org.springframework.stereotype.Component
public class EnvironmentComponentEventListener extends HygieiaMongoEventListener<EnvironmentComponent> {

    private final DashboardRepository dashboardRepository;
    private final ComponentRepository componentRepository;
    private final BinaryArtifactRepository binaryArtifactRepository;

    @Autowired
    public EnvironmentComponentEventListener(DashboardRepository dashboardRepository,
                              CollectorItemRepository collectorItemRepository,
                              ComponentRepository componentRepository,
                              EnvironmentComponentRepository environmentComponentRepository,
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

    private void processEnvironmentComponent(EnvironmentComponent environmentComponent) {
        List<Dashboard> dashboards = findTeamDashboardsForEnvironmentComponent(environmentComponent);

        for (Dashboard dashboard : dashboards) {
            Pipeline pipeline = getOrCreatePipeline(dashboard);
            addCommitsToEnvironmentStage(environmentComponent, pipeline);
            pipelineRepository.save(pipeline);
        }

    }

    private void addCommitsToEnvironmentStage(EnvironmentComponent environmentComponent, Pipeline pipeline){
        //find all artifacts by name, only ones that matter are between last artifact and this
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

        List<BinaryArtifact> sortedArtifacts = Lists.newArrayList(artifacts);
        Collections.sort(sortedArtifacts, BinaryArtifact.TIMESTAMP_COMPATOR);

        for(BinaryArtifact artifact : sortedArtifacts){
            for(SCM scm : artifact.getBuildInfo().getSourceChangeSet()){
                PipelineCommit commit = new PipelineCommit(scm);
                commit.addNewPipelineProcessedTimestamp(environmentComponent.getEnvironmentName(), environmentComponent.getAsOfDate());
                pipeline.addCommit(environmentComponent.getEnvironmentName(), commit);
            }
        }
        BinaryArtifact lastArtifact = sortedArtifacts.get(sortedArtifacts.size() - 1);
        currentStage.setLastArtifact(lastArtifact);

    }

    private List<Dashboard> findTeamDashboardsForEnvironmentComponent(EnvironmentComponent environmentComponent){
        List<Dashboard> dashboards;
        CollectorItem deploymentCollectorItem = collectorItemRepository.findOne(environmentComponent.getCollectorItemId());
        List<Component> components = componentRepository.findByDeployCollectorItemId(deploymentCollectorItem.getId());
        dashboards = dashboardRepository.findByApplicationComponentsIn(components);
        return dashboards;
    }
}
