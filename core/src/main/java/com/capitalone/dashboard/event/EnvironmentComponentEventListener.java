package com.capitalone.dashboard.event;


import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.*;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;

import java.util.*;

@org.springframework.stereotype.Component
public class EnvironmentComponentEventListener extends HygieiaMongoEventListener<EnvironmentComponent> {
    private static final Logger LOG = LoggerFactory.getLogger(BuildEventListener.class);

    private DashboardRepository dashboardRepository;
    private CollectorItemRepository collectorItemRepository;
    private ComponentRepository componentRepository;
    private EnvironmentComponentRepository environmentComponentRepository;
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
        this.environmentComponentRepository = environmentComponentRepository;
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
            PipelineStageType pipelineStageType = getPipelineStage(dashboard, environmentComponent);
            Pipeline pipeline = getOrCreatePipeline(dashboardPair);
            Map<PipelineCommit, PipelineCommit> pipelineCommitMap = buildPipelineCommitPipelineCommitMap(pipeline);

            for (SCM scm : changeSet) {
                PipelineCommit pipelineCommit = findOrCreatePipelineCommit(pipeline, pipelineCommitMap, scm);
                //// TODO: 1/27/16 verify: is this the right date?
                pipelineCommit.updateCurrentStage(pipelineStageType, environmentComponent.getAsOfDate());
            }
            pipelineRepository.save(pipeline);
        }

    }

    private PipelineStageType getPipelineStage(Dashboard dashboard, EnvironmentComponent environmentComponent){
        PipelineStageType pipelineStageType = null;
        for(Widget widget : dashboard.getWidgets()) {
            if(widget.getName().equalsIgnoreCase("pipeline")){
                Map<String, String> mappings = (Map)widget.getOptions().get("mappings");
                if(mappings == null ||mappings.size() < 1)
                {
                    return null;
                }

                for(Map.Entry entry : mappings.entrySet()){
                    if(entry.getValue().equals(environmentComponent.getEnvironmentName()))
                    {
                        pipelineStageType = PipelineStageType.fromString((String)entry.getKey());
                    }
                }
            }
        }
        return pipelineStageType;
    }

    //// TODO: 1/27/16 Verify: is the logic here correct?
    private List<SCM> getScmChangeSetForEnvironmentComponent(EnvironmentComponent environmentComponent){
        List<SCM> changeSet = new ArrayList<>();
        List<BinaryArtifact> artifacts = (List)binaryArtifactRepository.findByArtifactNameAndArtifactVersion(environmentComponent.getComponentName(), environmentComponent.getComponentVersion());
        for(BinaryArtifact artifact : artifacts){
            changeSet.addAll(artifact.getSourceChangeSet());
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
