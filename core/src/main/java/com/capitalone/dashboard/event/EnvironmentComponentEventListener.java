package com.capitalone.dashboard.event;


import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;

import java.util.ArrayList;
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
        Iterable<BinaryArtifact> artifacts = binaryArtifactRepository.findByArtifactNameAndArtifactVersion(environmentComponent.getComponentName(), environmentComponent.getComponentVersion());
        for(BinaryArtifact artifact : artifacts){
            for(SCM scm : artifact.getBuildInfo().getSourceChangeSet()){
                PipelineCommit commit = new PipelineCommit(scm);
                commit.addNewPipelineProcessedTimestamp(environmentComponent.getEnvironmentName(), environmentComponent.getAsOfDate());
                pipeline.addCommit(environmentComponent.getEnvironmentName(), commit);
            }
        }
    }

    private List<Dashboard> findTeamDashboardsForEnvironmentComponent(EnvironmentComponent environmentComponent){
        List<Dashboard> dashboards;
        CollectorItem deploymentCollectorItem = collectorItemRepository.findOne(environmentComponent.getCollectorItemId());
        List<Component> components = componentRepository.findByDeployCollectorItemId(deploymentCollectorItem.getId());
        List<ObjectId> componentObjectIds = new ArrayList<>();

        for(Component c : components){
            componentObjectIds.add(c.getId());
        }
        dashboards = dashboardRepository.findDashboardsByApplicationComponentIds(componentObjectIds);
        return dashboards;
    }
}
