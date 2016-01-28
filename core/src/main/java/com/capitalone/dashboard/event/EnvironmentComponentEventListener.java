package com.capitalone.dashboard.event;


import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("PMD")
@org.springframework.stereotype.Component
public class EnvironmentComponentEventListener extends HygieiaMongoEventListener<EnvironmentComponent> {

    private DashboardRepository dashboardRepository;
    private CollectorItemRepository collectorItemRepository;
    private ComponentRepository componentRepository;
    private BinaryArtifactRepository binaryArtifactRepository;
    private PipelineRepository pipelineRepository;
    private CollectorRepository collectorRepository;

    @Autowired
    public EnvironmentComponentEventListener(DashboardRepository dashboardRepository,
                              CollectorItemRepository collectorItemRepository,
                              ComponentRepository componentRepository,
                              EnvironmentComponentRepository environmentComponentRepository,
                              BinaryArtifactRepository binaryArtifactRepository,
                              PipelineRepository pipelineRepository,
                              CollectorRepository collectorRepository) {

        this.dashboardRepository = dashboardRepository;
        this.collectorItemRepository = collectorItemRepository;
        this.componentRepository = componentRepository;
        this.binaryArtifactRepository = binaryArtifactRepository;
        this.pipelineRepository = pipelineRepository;
        this.collectorRepository = collectorRepository;
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
            CollectorItem teamDashboardCollectorItem = getTeamDashboardCollectorItem(dashboard);
            AbstractMap.SimpleEntry<Dashboard, CollectorItem> dashboardPair = new AbstractMap.SimpleEntry(dashboard, teamDashboardCollectorItem);
            Pipeline pipeline = getOrCreatePipeline(dashboardPair);

            for (SCM scm : changeSet) {
                pipeline.addCommit(environmentComponent.getEnvironmentName(), new PipelineCommit(scm, environmentComponent.getAsOfDate()));
            }
            pipelineRepository.save(pipeline);
        }

    }


    //// TODO: 1/27/16 Verify: is the logic here correct?
    private List<SCM> getScmChangeSetForEnvironmentComponent(EnvironmentComponent environmentComponent){
        List<SCM> changeSet = new ArrayList<>();
        List<BinaryArtifact> artifacts = (List)binaryArtifactRepository.findByArtifactNameAndArtifactVersion(environmentComponent.getComponentName(), environmentComponent.getComponentVersion());
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


    @Override
    protected CollectorItemRepository getCollectorItemRepository() {
        return this.collectorItemRepository;
    }

    @Override
    protected CollectorRepository getCollectorRepository() {
        return this.collectorRepository;
    }

    @Override
    protected PipelineRepository getPipelineRepository() {
        return this.pipelineRepository;
    }
}
