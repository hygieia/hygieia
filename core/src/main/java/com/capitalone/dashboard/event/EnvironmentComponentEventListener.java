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
        List<SCM> changeSet = getScmChangeSetForEnvironmentComponent(environmentComponent);
        List<Dashboard> dashboards = findTeamDashboardsForEnvironmentComponent(environmentComponent);

        for (Dashboard dashboard : dashboards) {
            Pipeline pipeline = getOrCreatePipeline(dashboard);

            for (SCM scm : changeSet) {
                //todo: this needs to be fixed...
                PipelineCommit commit = new PipelineCommit(scm);
                pipeline.addCommit(environmentComponent.getEnvironmentName(), commit);
            }
            pipelineRepository.save(pipeline);
        }

    }


    //// TODO: 1/27/16 Verify: is the logic here correct?
    private List<SCM> getScmChangeSetForEnvironmentComponent(EnvironmentComponent environmentComponent){
        List<SCM> changeSet = new ArrayList<>();
        Iterable<BinaryArtifact> artifacts = binaryArtifactRepository.findByArtifactNameAndArtifactVersion(environmentComponent.getComponentName(), environmentComponent.getComponentVersion());
        for(BinaryArtifact artifact : artifacts){
        }
        return changeSet;
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
