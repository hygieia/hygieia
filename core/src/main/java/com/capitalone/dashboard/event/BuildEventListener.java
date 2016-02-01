package com.capitalone.dashboard.event;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;

import java.util.List;

@org.springframework.stereotype.Component
public class BuildEventListener extends HygieiaMongoEventListener<Build> {

    private final DashboardRepository dashboardRepository;
    private final ComponentRepository componentRepository;

    @Autowired
    public BuildEventListener(DashboardRepository dashboardRepository,
                              CollectorItemRepository collectorItemRepository,
                              ComponentRepository componentRepository,
                              PipelineRepository pipelineRepository,
                              CollectorRepository collectorRepository) {
        super(collectorItemRepository, pipelineRepository, collectorRepository);
        this.dashboardRepository = dashboardRepository;
        this.componentRepository = componentRepository;
    }

    @Override
    public void onAfterSave(AfterSaveEvent<Build> event) {
        Build build = event.getSource();
        if(build.getBuildStatus().equals(BuildStatus.Success)){
            processBuild(event.getSource());
        }
    }


    private void processBuild(Build build){
        List<Dashboard> teamDashboardsReferencingBuild = findAllDashboardsForBuild(build);

        //for every team dashboard referencing the build, find the pipeline, put this commit in the build stage
        for(Dashboard teamDashboard : teamDashboardsReferencingBuild){
            Pipeline pipeline = getOrCreatePipeline(teamDashboard);
            for(SCM scm : build.getSourceChangeSet()){
                PipelineCommit commit = new PipelineCommit(scm);
                commit.addNewPipelineProcessedTimestamp(PipelineStageType.Build.name(), build.getTimestamp());
                pipeline.addCommit(PipelineStageType.Build.name(), commit);
            }
            pipelineRepository.save(pipeline);
        }
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
        return dashboardRepository.findByApplicationComponents(components);
    }
}
