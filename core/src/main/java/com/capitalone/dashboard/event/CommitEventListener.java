package com.capitalone.dashboard.event;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;

import java.util.*;

@org.springframework.stereotype.Component
public class CommitEventListener extends AbstractMongoEventListener<Commit> {

    private final ComponentRepository componentRepository;
    private final DashboardRepository dashboardRepository;
    private final CollectorRepository collectorRepository;
    private final CollectorItemRepository collectorItemRepository;
    private final PipelineRepository pipelineRepository;
    private final BuildRepository buildRepository;

    @Autowired
    public CommitEventListener(ComponentRepository componentRepository,
                               DashboardRepository dashboardRepository,
                               CollectorRepository collectorRepository,
                               CollectorItemRepository collectorItemRepository,
                               PipelineRepository pipelineRepository,
                               BuildRepository buildRepository) {
        this.componentRepository = componentRepository;
        this.dashboardRepository = dashboardRepository;
        this.collectorRepository = collectorRepository;
        this.collectorItemRepository = collectorItemRepository;
        this.pipelineRepository = pipelineRepository;
        this.buildRepository = buildRepository;
    }

    @Override
    public void onAfterSave(AfterSaveEvent<Commit> event) {
        Commit commit = event.getSource();

        // For each TeamDashboard associated with this commit...
        for (Map.Entry<CollectorItem, Dashboard> entry : getTeamDashboards(commit).entrySet()) {
            processCommit(commit, entry.getKey(), entry.getValue());
        }

    }

    private void processCommit(Commit commit, CollectorItem teamDashboardItem, Dashboard teamDashboard) {
        // Get the Pipeline object associated with this Dashboard
        Pipeline pipeline = pipelineRepository.findByCollectorItemId(teamDashboardItem.getId());
        if (pipeline == null) {
            pipeline = new Pipeline();
            pipeline.setCollectorItemId(teamDashboardItem.getId());
        }

        // Add this Commit to the Commit stage if it is not associated with a successful Build
        if (pipelineCommitNotFound(pipeline, commit) && !successfulBuild(teamDashboard, commit)) {
            PipelineCommit pipelineCommit = new PipelineCommit(commit);
            pipelineCommit.updateCurrentStage(PipelineStageType.Commit, commit.getScmCommitTimestamp());
            pipeline.getCommits().add(pipelineCommit);
        }

        pipelineRepository.save(pipeline);
    }

    private boolean successfulBuild(Dashboard teamDashboard, Commit commit) {
        // Get the Build CollectorItems for this Team Dashboard
        List<CollectorItem> buildCollectorItems = teamDashboard.getApplication().getComponents().get(0).getCollectorItems(CollectorType.Build);
        List<ObjectId> ids = new ArrayList<>();
        for (CollectorItem buildCollectorItem : buildCollectorItems) {
            ids.add(buildCollectorItem.getId());
        }

        // Find all Builds that reference this Commit by revision number
        List<String> revisionNumbers = Collections.singletonList(commit.getScmRevisionNumber());
        for (Build build : buildRepository.findBuildsForRevisionNumbersAndBuildCollectorItemIds(revisionNumbers, ids)) {
            // If any are successful, return true
            if (build.getBuildStatus().equals(BuildStatus.Success)) {
                return true;
            }
        }

        // No successful builds
        return false;
    }

    private boolean pipelineCommitNotFound(Pipeline pipeline, Commit pipelineCommit) {
        return !pipeline.getCommits().contains(new PipelineCommit(pipelineCommit));
    }

    private Map<CollectorItem, Dashboard> getTeamDashboards(Commit commit) {
        Map<CollectorItem, Dashboard> productDashboards = new HashMap<>();

        // Get the list of components that reference this SCM repo
        List<Component> components = componentRepository.findBySCMCollectorItemId(commit.getCollectorItemId());

        // TODO - From here down could be a utility method that accepts List<Component>

        // Find the team dashboards that reference that component
        List<Dashboard> teamDashboards = dashboardRepository.findByApplicationComponents(components);

        // Get the Product Collector
        Collector productCollector = collectorRepository.findByName("Product");

        // Find the Product collector items associated with these team dashboards
        for (Dashboard teamDashboard : teamDashboards) {
            CollectorItem item = collectorItemRepository.findTeamDashboardCollectorItemsByCollectorIdAndDashboardId(
                    productCollector.getId(),
                    teamDashboard.getId().toString());
            if (item != null && item.isEnabled()) {
                productDashboards.put(item, teamDashboard);
            }
        }

        return productDashboards;
    }
}
