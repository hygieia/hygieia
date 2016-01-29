package com.capitalone.dashboard.event;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Component
public class CommitEventListener extends HygieiaMongoEventListener<Commit> {

    private final ComponentRepository componentRepository;
    private final DashboardRepository dashboardRepository;
    private final CollectorRepository collectorRepository;
    private final CollectorItemRepository collectorItemRepository;
    private final PipelineRepository pipelineRepository;

    @Autowired
    public CommitEventListener(ComponentRepository componentRepository,
                               DashboardRepository dashboardRepository,
                               CollectorRepository collectorRepository,
                               CollectorItemRepository collectorItemRepository,
                               PipelineRepository pipelineRepository) {
        this.componentRepository = componentRepository;
        this.dashboardRepository = dashboardRepository;
        this.collectorRepository = collectorRepository;
        this.collectorItemRepository = collectorItemRepository;
        this.pipelineRepository = pipelineRepository;
    }

    @Override
    public void onAfterSave(AfterSaveEvent<Commit> event) {
        Commit commit = event.getSource();

        // For each TeamDashboard associated with this commit...
        for (Map.Entry<CollectorItem, Dashboard> entry : getTeamDashboards(commit).entrySet()) {
            processCommit(commit, entry.getKey());
        }

    }

    private void processCommit(Commit commit, CollectorItem teamDashboardItem) {
        // Get the Pipeline object associated with this Dashboard
        Pipeline pipeline = pipelineRepository.findByCollectorItemId(teamDashboardItem.getId());
        if (pipeline == null) {
            pipeline = new Pipeline();
            pipeline.setCollectorItemId(teamDashboardItem.getId());
        }

        ///// TODO: 1/28/16 For all commits that come through, just add it to commit bucket
        PipelineCommit pipelineCommit = new PipelineCommit(commit);
        pipelineCommit.addNewPipelineProcessedTimestamp(PipelineStageType.Commit, commit.getScmCommitTimestamp());

        pipeline.addCommit(PipelineStageType.Commit.name(), pipelineCommit);

        pipelineRepository.save(pipeline);
    }


    private Map<CollectorItem, Dashboard> getTeamDashboards(Commit commit) {
        Map<CollectorItem, Dashboard> teamDashboardCollectorItemMap = new HashMap<>();

        // Get the list of components that reference this SCM repo
        List<Component> components = componentRepository.findBySCMCollectorItemId(commit.getCollectorItemId());

        // TODO - From here down could be a utility method that accepts List<Component>

        // Find the team dashboards that reference that component
        List<Dashboard> teamDashboards = dashboardRepository.findByApplicationComponents(components);

        // Find the Product collector items associated with these team dashboards
        for (Dashboard teamDashboard : teamDashboards) {
            CollectorItem item = getTeamDashboardCollectorItem(teamDashboard);
            if (item != null) {
                teamDashboardCollectorItemMap.put(item, teamDashboard);
            }
        }
        return teamDashboardCollectorItemMap;
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
