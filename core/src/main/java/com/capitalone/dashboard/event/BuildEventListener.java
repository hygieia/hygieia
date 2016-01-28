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

        //for every team dashboard referencing the build, find the pipeline, put this commit in the build stage
        for(Dashboard teamDashboard : teamDashboardsReferencingBuild){
            CollectorItem teamDashboardCollectorItem = getTeamDashboardCollectorItem(teamDashboard);
            Pipeline pipeline = getOrCreatePipeline(new AbstractMap.SimpleEntry<Dashboard, CollectorItem>(teamDashboard, teamDashboardCollectorItem));
            for(SCM scm : build.getSourceChangeSet()){
                //// TODO: 1/28/16 should this timestamp potentially be the begin or end timestamp?
                pipeline.addCommit(PipelineStageType.Build.name(), new PipelineCommit(scm, build.getTimestamp()));
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
