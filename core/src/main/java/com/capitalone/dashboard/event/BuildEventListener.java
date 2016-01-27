package com.capitalone.dashboard.event;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.*;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;

import java.util.*;

@org.springframework.stereotype.Component
public class BuildEventListener extends AbstractMongoEventListener<Build> {
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

    private Collector getProductCollector(){
        List<Collector> productCollectors = collectorRepository.findByCollectorType(CollectorType.Product);
        if(productCollectors.isEmpty()){
            return null;
        }
        return productCollectors.get(0);
    }

    private List<SCM> processBuild(Build build){
        List<SCM> allCommits = new ArrayList<>();

        List<Dashboard> teamDashboardsReferencingBuild = findAllDashboardsForBuild(build);

        for(Dashboard teamDashboard : teamDashboardsReferencingBuild){
            CollectorItem teamDashboardCollectorItem = collectorItemRepository
                    .findTeamDashboardCollectorItemsByCollectorIdAndDashboardId(getProductCollector().getId(),
                            teamDashboard.getId().toString());
            boolean wasDeployed = false;
            for(BinaryArtifact artifact : findAllBinaryArtifactsForBuild(build))
            {
                List<EnvironmentComponent> environmentComponentsForArtifact = environmentComponentRepository
                        .findDeployedByCollectorItemIdAndComponentNameAndComponentVersion(teamDashboardCollectorItem.getId(),
                                artifact.getArtifactName(), artifact.getArtifactVersion());
                if(environmentComponentsForArtifact != null && !environmentComponentsForArtifact.isEmpty()){
                    wasDeployed = true;
                    break;
                }
            }
            if(!wasDeployed){
                AbstractMap.SimpleEntry dashboardPair = new AbstractMap.SimpleEntry(teamDashboard, teamDashboardCollectorItem);
                updatePipelinesForBuild(dashboardPair, build);
            }

        }

        return allCommits;
    }

    private void updatePipelinesForBuild(AbstractMap.SimpleEntry<Dashboard, CollectorItem> dashboard, Build build){
        Pipeline pipeline = pipelineRepository.findByCollectorItemId(dashboard.getValue().getId());
        if(pipeline == null){
            pipeline = new Pipeline();
            pipeline.setName(dashboard.getKey().getTitle());
            pipeline.setCollectorItemId(dashboard.getValue().getId());
        }
        Map<PipelineCommit, PipelineCommit> pipelineCommitMap= new HashMap<>();
        for (PipelineCommit pc : pipeline.getCommits()) {
            pipelineCommitMap.put(pc, pc);
        }
        for(SCM scm : build.getSourceChangeSet()){
            PipelineCommit pipelineCommit = pipelineCommitMap.get(new PipelineCommit(scm));
            if(pipelineCommit == null){
                pipelineCommit = new PipelineCommit(scm);
                pipeline.getCommits().add(pipelineCommit);
            }
            pipelineCommit.updateCurrentStage(PipelineStageType.Build, build.getTimestamp());
        }
        pipelineRepository.save(pipeline);
    }


    private List<BinaryArtifact> findAllBinaryArtifactsForBuild(Build build){
        return (List)binaryArtifactRepository.findByCollectorItemId(build.getCollectorItemId());
    }

    private List<Dashboard> findAllDashboardsForBuild(Build build){
        CollectorItem buildCollectorItem = collectorItemRepository.findOne(build.getCollectorItemId());
        List<Component> components = componentRepository.findByBuildCollectorItemId(buildCollectorItem.getId());
        List<ObjectId> buildComponentObjectIds = new ArrayList<>();
        for(Component c : components){
            buildComponentObjectIds.add(c.getId());
        }
        return dashboardRepository.findDashboardsByApplicationComponentIds(buildComponentObjectIds);
    }
}
