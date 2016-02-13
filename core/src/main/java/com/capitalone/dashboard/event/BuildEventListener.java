package com.capitalone.dashboard.event;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;

import java.util.Iterator;
import java.util.List;

@org.springframework.stereotype.Component
public class BuildEventListener extends HygieiaMongoEventListener<Build> {

    private final DashboardRepository dashboardRepository;
    private final ComponentRepository componentRepository;
    private final BuildRepository buildRepository;

    @Autowired
    public BuildEventListener(DashboardRepository dashboardRepository,
                              CollectorItemRepository collectorItemRepository,
                              ComponentRepository componentRepository,
                              PipelineRepository pipelineRepository,
                              CollectorRepository collectorRepository,
                              BuildRepository buildRepository) {
        super(collectorItemRepository, pipelineRepository, collectorRepository);
        this.dashboardRepository = dashboardRepository;
        this.componentRepository = componentRepository;
        this.buildRepository = buildRepository;
    }

    @Override
    public void onAfterSave(AfterSaveEvent<Build> event) {
        Build build = event.getSource();
        //if a build is successful, process it
        if(build.getBuildStatus().equals(BuildStatus.Success)){
            processBuild(event.getSource());
        }
        else if(build.getBuildStatus().equals(BuildStatus.Failure)){
            processFailedBuild(event.getSource());
        }
    }

    /**
     * If the build has failed, find the pipelines of the dashboards referencing the build and add the failed build to
     * the failed builds bucket on the pipeline
     * @param failedBuild
     */
    private void processFailedBuild(Build failedBuild){
        List<Dashboard> teamDashboardsReferencingBuild = findAllDashboardsForBuild(failedBuild);
        for(Dashboard teamDashboard : teamDashboardsReferencingBuild){
            Pipeline pipeline = getOrCreatePipeline(teamDashboard);
            pipeline.addFailedBuild(failedBuild);
            pipelineRepository.save(pipeline);
        }
    }

    /**
     * Find all dashboards referencing the build and then then for each commit in the changeset of the build (as per jenkins)
     * add the commit to the pipeline for the dashboard
     * @param build
     */
    private void processBuild(Build build){
        List<Dashboard> teamDashboardsReferencingBuild = findAllDashboardsForBuild(build);

        //for every team dashboard referencing the build, find the pipeline, put this commit in the build stage
        for(Dashboard teamDashboard : teamDashboardsReferencingBuild){
            Pipeline pipeline = getOrCreatePipeline(teamDashboard);
            for(SCM scm : build.getSourceChangeSet()){
                PipelineCommit commit = new PipelineCommit(scm, build.getTimestamp());
                pipeline.addCommit(PipelineStageType.Build.name(), commit);
            }

            boolean hasFailedBuilds = !pipeline.getFailedBuilds().isEmpty();
            processPreviousFailedBuilds(build, pipeline);
            pipelineRepository.save(pipeline);
            if(hasFailedBuilds){
                buildRepository.save(build);
            }
        }
    }

    /**
     * Iterate over failed builds, if the failed build collector item id matches the successful builds collector item id
     * take all the commits from the changeset of the failed build and add them to the pipeline and also to the changeset
     * of the successful build.  Then remove the failed build from the collection after it has been processed.
     * @param successfulBuild
     * @param pipeline
     */
    private void processPreviousFailedBuilds(Build successfulBuild, Pipeline pipeline){

        if(!pipeline.getFailedBuilds().isEmpty()) {
            Iterator<Build> failedBuilds = pipeline.getFailedBuilds().iterator();

            while (failedBuilds.hasNext()) {
                Build b = failedBuilds.next();
                if (b.getCollectorItemId().equals(successfulBuild.getCollectorItemId())) {
                    for (SCM scm : b.getSourceChangeSet()) {
                        PipelineCommit failedBuildCommit = new PipelineCommit(scm, successfulBuild.getTimestamp());
                        pipeline.addCommit(PipelineStageType.Build.name(), failedBuildCommit);
                        successfulBuild.getSourceChangeSet().add(scm);
                    }
                    failedBuilds.remove();

                }
            }
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
        return dashboardRepository.findByApplicationComponentsIn(components);
    }
}
