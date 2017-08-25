package com.capitalone.dashboard.event;

import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Pipeline;
import com.capitalone.dashboard.model.PipelineCommit;
import com.capitalone.dashboard.model.PipelineStage;
import com.capitalone.dashboard.model.SCM;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.PipelineRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.capitalone.dashboard.util.PipelineUtils.isMoveCommitToBuild;
import static com.capitalone.dashboard.util.PipelineUtils.processPreviousFailedBuilds;

@org.springframework.stereotype.Component
public class BuildEventListener extends HygieiaMongoEventListener<Build> {
    private final DashboardRepository dashboardRepository;
    private final ComponentRepository componentRepository;
    private final BuildRepository buildRepository;
    private final CommitRepository commitRepository;

    @Autowired
    public BuildEventListener(DashboardRepository dashboardRepository,
                              CollectorItemRepository collectorItemRepository,
                              ComponentRepository componentRepository,
                              PipelineRepository pipelineRepository,
                              CollectorRepository collectorRepository,
                              BuildRepository buildRepository, CommitRepository commitRepository) {
        super(collectorItemRepository, pipelineRepository, collectorRepository);
        this.dashboardRepository = dashboardRepository;
        this.componentRepository = componentRepository;
        this.buildRepository = buildRepository;
        this.commitRepository = commitRepository;
    }

    @Override
    public void onAfterSave(AfterSaveEvent<Build> event) {
        Build build = event.getSource();
        //if a build is successful, process it
        if (build.getBuildStatus().equals(BuildStatus.Success)) {
            processBuild(event.getSource());
        } else if (build.getBuildStatus().equals(BuildStatus.Failure)) {
            processFailedBuild(event.getSource());
        }
    }

    /**
     * If the build has failed, find the pipelines of the dashboards referencing the build and add the failed build to
     * the failed builds bucket on the pipeline
     *
     * @param failedBuild
     */
    private void processFailedBuild(Build failedBuild) {
        List<Dashboard> teamDashboardsReferencingBuild = findAllDashboardsForBuild(failedBuild);
        for (Dashboard teamDashboard : teamDashboardsReferencingBuild) {
            Pipeline pipeline = getOrCreatePipeline(teamDashboard);
            pipeline.addFailedBuild(failedBuild);
            pipelineRepository.save(pipeline);
        }
    }

    /**
     * Find all dashboards referencing the build and then then for each commit in the changeset of the build (as per jenkins)
     * add the commit to the pipeline for the dashboard
     *
     * @param build
     */
    private void processBuild(Build build) {
        List<Dashboard> teamDashboardsReferencingBuild = findAllDashboardsForBuild(build);

        //for every team dashboard referencing the build, find the pipeline, put this commit in the build stage
        for (Dashboard teamDashboard : teamDashboardsReferencingBuild) {
            Pipeline pipeline = getOrCreatePipeline(teamDashboard);

            for (SCM scm : build.getSourceChangeSet()) {
                // we want to use the build start time since the timestamp was just the time that the collector ran
                PipelineCommit commit = new PipelineCommit(scm, build.getStartTime());
                pipeline.addCommit(PipelineStage.BUILD.getName(), commit);
            }

            processPreviousFailedBuilds(build, pipeline);


            /**
             * If some build events are missed, here is an attempt to move commits to the build stage
             * This also takes care of the problem with Jenkins first build change set being empty.
             *
             * Logic:
             * If the build start time is after the scm commit, move the commit to build stage. Match the repo at the very least.
             */
            Map<String, PipelineCommit> commitStageCommits = pipeline.getCommitsByEnvironmentName(PipelineStage.COMMIT.getName());
            Map<String, PipelineCommit> buildStageCommits = pipeline.getCommitsByEnvironmentName(PipelineStage.BUILD.getName());
            for (String rev : commitStageCommits.keySet()) {
                PipelineCommit commit = commitStageCommits.get(rev);
                if ((commit.getScmCommitTimestamp() < build.getStartTime()) && !buildStageCommits.containsKey(rev) && isMoveCommitToBuild(build, commit, commitRepository)) {
                    pipeline.addCommit(PipelineStage.BUILD.getName(), commit);
                }
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
    private List<Dashboard> findAllDashboardsForBuild(Build build) {
        List<Dashboard> dashboards = new ArrayList<>();
        if (build == null || build.getCollectorItemId() == null) {
            //return an empty list if the build is not associated with a Dashboard
            return dashboards;
        }
        CollectorItem buildCollectorItem = collectorItemRepository.findOne(build.getCollectorItemId());
        if(buildCollectorItem != null) {
            List<Component> components = componentRepository.findByBuildCollectorItemId(buildCollectorItem.getId());
            if (!components.isEmpty()) {
                //return an empty list if the build is not associated with a Dashboard
                dashboards = dashboardRepository.findByApplicationComponentsIn(components);
            }
        }
        return dashboards;
    }


    private CollectorItem getCollectorItem(ObjectId id) {
        return collectorItemRepository.findOne(id);
    }

    private Collector getCollector(ObjectId id) {
        return collectorRepository.findOne(id);
    }
}
