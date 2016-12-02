package com.capitalone.dashboard.event;

import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Pipeline;
import com.capitalone.dashboard.model.PipelineCommit;
import com.capitalone.dashboard.model.PipelineStageType;
import com.capitalone.dashboard.model.RepoBranch;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
                pipeline.addCommit(PipelineStageType.Build.name(), commit);
            }

            boolean hasFailedBuilds = !pipeline.getFailedBuilds().isEmpty();
            processPreviousFailedBuilds(build, pipeline);


            /**
             * If some build events are missed, here is an attempt to move commits to the build stage
             * This also takes care of the problem with Jenkins first build change set being empty.
             *
             * Logic:
             * If the build start time is after the scm commit, move the commit to build stage. Match the repo at the very least.
             */
            Map<String, PipelineCommit> commitStageCommits = pipeline.getCommitsByStage(PipelineStageType.Commit.name());
            Map<String, PipelineCommit> buildStageCommits = pipeline.getCommitsByStage(PipelineStageType.Build.name());
            for (String rev : commitStageCommits.keySet()) {
                PipelineCommit commit = commitStageCommits.get(rev);
                if ((commit.getScmCommitTimestamp() < build.getStartTime()) && !buildStageCommits.containsKey(rev) && isMoveCommitToBuild(build, commit)) {
                    pipeline.addCommit(PipelineStageType.Build.name(), commit);
                }
            }
            pipelineRepository.save(pipeline);
            if (hasFailedBuilds) {
                buildRepository.save(build);
            }
        }
    }


    private boolean isMoveCommitToBuild(Build build, SCM scm) {
        List<Commit> commitsFromRepo = getCommitsFromCommitRepo(scm);
        List<RepoBranch> codeReposFromBuild = build.getCodeRepos();
        Set<String> codeRepoUrlsFromCommits = new HashSet<>();
        for (Commit c : commitsFromRepo) {
            codeRepoUrlsFromCommits.add(getRepoNameOnly(c.getScmUrl()));
        }

        for (RepoBranch rb : codeReposFromBuild) {
            if (codeRepoUrlsFromCommits.contains(getRepoNameOnly(rb.getUrl()))) {
                return true;
            }
        }
        return false;
    }


    private String getRepoNameOnly(String url) {
        try {
            URL temp = new URL(url);
            return temp.getHost() + temp.getPath();
        } catch (MalformedURLException e) {
            return url;
        }
    }

    /**
     * Iterate over failed builds, if the failed build collector item id matches the successful builds collector item id
     * take all the commits from the changeset of the failed build and add them to the pipeline and also to the changeset
     * of the successful build.  Then remove the failed build from the collection after it has been processed.
     *
     * @param successfulBuild
     * @param pipeline
     */
    private void processPreviousFailedBuilds(Build successfulBuild, Pipeline pipeline) {

        if (!pipeline.getFailedBuilds().isEmpty()) {
            Iterator<Build> failedBuilds = pipeline.getFailedBuilds().iterator();

            while (failedBuilds.hasNext()) {
                Build b = failedBuilds.next();
                if (b.getCollectorItemId().equals(successfulBuild.getCollectorItemId())) {
                    for (SCM scm : b.getSourceChangeSet()) {
                        PipelineCommit failedBuildCommit = new PipelineCommit(scm, successfulBuild.getStartTime());
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
    private List<Dashboard> findAllDashboardsForBuild(Build build) {
        CollectorItem buildCollectorItem = collectorItemRepository.findOne(build.getCollectorItemId());
        List<Component> components = componentRepository.findByBuildCollectorItemId(buildCollectorItem.getId());
        return dashboardRepository.findByApplicationComponentsIn(components);
    }

    private List<Commit> getCommitsFromCommitRepo(SCM scm) {
        return commitRepository.findByScmRevisionNumber(scm.getScmRevisionNumber());
    }

    private CollectorItem getCollectorItem(ObjectId id) {
        return collectorItemRepository.findOne(id);
    }

    private Collector getCollector(ObjectId id) {
        return collectorRepository.findOne(id);
    }
}
