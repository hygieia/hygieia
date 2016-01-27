package com.capitalone.dashboard.event;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.*;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;

import java.util.*;

@org.springframework.stereotype.Component
public class BuildEventListener extends HygieiaMongoEventListener<Build> {
    private static final Logger LOG = LoggerFactory.getLogger(BuildEventListener.class);

    private DashboardRepository dashboardRepository;
    private CollectorItemRepository collectorItemRepository;
    private ComponentRepository componentRepository;
    private EnvironmentComponentRepository environmentComponentRepository;
    private BinaryArtifactRepository binaryArtifactRepository;
    private PipelineRepository pipelineRepository;
    private CollectorRepository collectorRepository;

    @Autowired
    public BuildEventListener(DashboardRepository dashboardRepository,
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
    public void onAfterSave(AfterSaveEvent<Build> event) {
        super.onAfterSave(event);
        LOG.debug("Build saved: " + event.getSource().getNumber());
        processBuild(event.getSource());
    }


    private void processBuild(Build build){
        List<Dashboard> teamDashboardsReferencingBuild = findAllDashboardsForBuild(build);

        for(Dashboard teamDashboard : teamDashboardsReferencingBuild){
            CollectorItem teamDashboardCollectorItem = getTeamDashboardCollectorItem(teamDashboard);

            boolean deployed = isBuildDeployed(build, teamDashboardCollectorItem);
            if(!deployed){
                AbstractMap.SimpleEntry<Dashboard, CollectorItem> dashboardPair = new AbstractMap.SimpleEntry(teamDashboard, teamDashboardCollectorItem);
                updatePipelinesForBuild(dashboardPair, build);
            }
        }
    }



    /**
     *
     * @param build
     * @param teamDashboardCollectorItem
     * @return
     */
    private boolean isBuildDeployed(Build build, CollectorItem teamDashboardCollectorItem) {
        boolean deployed = false;
        for(BinaryArtifact artifact : findAllBinaryArtifactsForBuild(build))
        {
            List<EnvironmentComponent> environmentComponentsForArtifact = environmentComponentRepository
                    .findDeployedByCollectorItemIdAndComponentNameAndComponentVersion(teamDashboardCollectorItem.getId(),
                            artifact.getArtifactName(), artifact.getArtifactVersion());
            if(environmentComponentsForArtifact != null && !environmentComponentsForArtifact.isEmpty()){
                deployed = true;
                break;
            }
        }
        return deployed;
    }

    /**
     * Updates the dashboard's pipeline in respect to the build
     *
     * @param dashboard
     * @param build
     */
    private void updatePipelinesForBuild(AbstractMap.SimpleEntry<Dashboard, CollectorItem> dashboard, Build build){
        Pipeline pipeline = getOrCreatePipeline(dashboard);
        Map<PipelineCommit, PipelineCommit> pipelineCommitMap = buildPipelineCommitPipelineCommitMap(pipeline);

        for(SCM scm : build.getSourceChangeSet()){
            PipelineCommit pipelineCommit = findOrCreatePipelineCommit(pipeline, pipelineCommitMap, scm);
            pipelineCommit.updateCurrentStage(PipelineStageType.Build, build.getTimestamp());
        }
        pipelineRepository.save(pipeline);
    }

    private List<BinaryArtifact> findAllBinaryArtifactsForBuild(Build build){
        return (List)binaryArtifactRepository.findByCollectorItemId(build.getCollectorItemId());
    }

    /**
     * Finds all of the dashboards for a given build way of the build by:
     * 1. Get collector item id for the build
     * 2. Get the build components referencing the build collectoritem
     * 3. Find all dashboards whose application references the build components
     *
     * @param build
     * @return
     */
    private List<Dashboard> findAllDashboardsForBuild(Build build){
        CollectorItem buildCollectorItem = collectorItemRepository.findOne(build.getCollectorItemId());
        List<Component> components = componentRepository.findByBuildCollectorItemId(buildCollectorItem.getId());
        List<ObjectId> buildComponentObjectIds = new ArrayList<>();
        for(Component c : components){
            buildComponentObjectIds.add(c.getId());
        }
        return dashboardRepository.findDashboardsByApplicationComponentIds(buildComponentObjectIds);
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
