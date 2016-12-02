package com.capitalone.dashboard.event;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Component
public class CommitEventListener extends HygieiaMongoEventListener<Commit> {

    private final ComponentRepository componentRepository;
    private final DashboardRepository dashboardRepository;

    @Autowired
    public CommitEventListener(ComponentRepository componentRepository,
                               DashboardRepository dashboardRepository,
                               CollectorRepository collectorRepository,
                               CollectorItemRepository collectorItemRepository,
                               PipelineRepository pipelineRepository) {
        super(collectorItemRepository, pipelineRepository, collectorRepository);
        this.componentRepository = componentRepository;
        this.dashboardRepository = dashboardRepository;
    }

    @Override
    public void onAfterSave(AfterSaveEvent<Commit> event) {
        Commit commit = event.getSource();

        // Add the commit to all pipelines associated with the team dashboards
        // this commit is part of. But only if there is a build collector item
        // configured on that dashboard. Otherwise, the commit will be orphaned
        // in the commit stage.
        findAllDashboardsForCommit(commit)
                .stream()
                .filter(this::dashboardHasBuildCollector)
                .forEach(teamDashboard -> {
                    if (CommitType.New.equals(commit.getType())) {
                        PipelineCommit pipelineCommit = new PipelineCommit(commit, commit.getScmCommitTimestamp());
                        Pipeline pipeline = getOrCreatePipeline(teamDashboard);
                        pipeline.addCommit(PipelineStageType.Commit.name(), pipelineCommit);
                        pipelineRepository.save(pipeline);
                    }
                });
    }

    /**
     * Finds all dashboards for a commit by way of the SCM collector item id of the dashboard that is tied to the commit
     * @param commit
     * @return
     */
    private List<Dashboard> findAllDashboardsForCommit(Commit commit){
        if (commit.getCollectorItemId() == null) return new ArrayList<>();
        CollectorItem commitCollectorItem = collectorItemRepository.findOne(commit.getCollectorItemId());
        List<Component> components = componentRepository.findBySCMCollectorItemId(commitCollectorItem.getId());
        return dashboardRepository.findByApplicationComponentsIn(components);
    }

    /**
     * Returns true if the provided dashboard has a build CollectorItem registered.
     *
     * @param teamDashboard a team Dashboard
     * @return true if build CollectorItem found
     */
    private boolean dashboardHasBuildCollector(Dashboard teamDashboard) {
        return teamDashboard.getApplication().getComponents()
                .stream()
                .anyMatch(c -> {
                    List<CollectorItem> buildCollectorItems = c.getCollectorItems(CollectorType.Build);
                    return buildCollectorItems != null && !buildCollectorItems.isEmpty();
                });
    }

}
