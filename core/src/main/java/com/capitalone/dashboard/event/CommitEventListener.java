package com.capitalone.dashboard.event;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;

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

        for (Dashboard teamDashboard : findAllDashboardsForCommit(commit)) {
            PipelineCommit pipelineCommit = new PipelineCommit(commit);
            pipelineCommit.addNewPipelineProcessedTimestamp(PipelineStageType.Commit.name(), commit.getScmCommitTimestamp());

            Pipeline pipeline = getOrCreatePipeline(teamDashboard);
            pipeline.addCommit(PipelineStageType.Commit.name(), pipelineCommit);
            pipelineRepository.save(pipeline);
        }

    }

    private List<Dashboard> findAllDashboardsForCommit(Commit commit){
        CollectorItem commitCollectorItem = collectorItemRepository.findOne(commit.getCollectorItemId());
        List<Component> components = componentRepository.findBySCMCollectorItemId(commitCollectorItem.getId());
        return dashboardRepository.findByApplicationComponentsIn(components);
    }

}
