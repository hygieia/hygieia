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

    private void processBuild(Build build){
        List<Dashboard> teamDashboardsReferencingBuild = findAllDashboardsForBuild(build);

        for(Dashboard teamDashboard : teamDashboardsReferencingBuild){
            CollectorItem teamDashboardCollectorItem = getTeamDashboardCollectorItem(teamDashboard);

            boolean deployed = isBuildDeployed(build, teamDashboardCollectorItem);
            if(!deployed){
                AbstractMap.SimpleEntry dashboardPair = new AbstractMap.SimpleEntry(teamDashboard, teamDashboardCollectorItem);
                updatePipelinesForBuild(dashboardPair, build);
            }
        }
    }

    private CollectorItem getTeamDashboardCollectorItem(Dashboard teamDashboard) {
        ObjectId productCollectorId = getProductCollector().getId();
        ObjectId dashboardId = teamDashboard.getId();
        return collectorItemRepository.findTeamDashboardCollectorItemsByCollectorIdAndDashboardId(productCollectorId, dashboardId.toString());
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

    /**
     * Updates the dashboard's pipeline in respect to the build
     *
     * @param dashboard
     * @param build
     */
    private void updatePipelinesForBuild(AbstractMap.SimpleEntry<Dashboard, CollectorItem> dashboard, Build build){
        Pipeline pipeline = getOrCreatePipeline(dashboard);
        Map<PipelineCommit, PipelineCommit> pipelineCommitMap = buildPipelineCommitPipelineCommitMap(pipeline);

        for(SCM scm : build.getSourceChangeSet()){
            PipelineCommit pipelineCommit = findOrCreatePipelineCommit(pipeline, pipelineCommitMap, scm);
            pipelineCommit.updateCurrentStage(PipelineStageType.Build, build.getTimestamp());
        }
        pipelineRepository.save(pipeline);
    }

    /**
     * Finds a SCM in a pipeline by way of the pipelineCommitMap
     *
     * @param pipeline
     * @param pipelineCommitMap
     * @param scm
     * @return
     */
    private PipelineCommit findOrCreatePipelineCommit(Pipeline pipeline, Map<PipelineCommit, PipelineCommit> pipelineCommitMap, SCM scm) {
        PipelineCommit pipelineCommit = pipelineCommitMap.get(new PipelineCommit(scm));
        if(pipelineCommit == null){
            pipelineCommit = new PipelineCommit(scm);
            pipeline.getCommits().add(pipelineCommit);
        }
        return pipelineCommit;
    }

    private Pipeline getOrCreatePipeline(AbstractMap.SimpleEntry<Dashboard, CollectorItem> dashboard) {
        Pipeline pipeline = pipelineRepository.findByCollectorItemId(dashboard.getValue().getId());
        if(pipeline == null){
            pipeline = new Pipeline();
            pipeline.setName(dashboard.getKey().getTitle());
            pipeline.setCollectorItemId(dashboard.getValue().getId());
        }
        return pipeline;
    }

    /**
     * Builds a map of commits for a given pipeline.  Allows for quickly finding/updating commits
     * @param pipeline
     * @return
     */
    private Map<PipelineCommit, PipelineCommit> buildPipelineCommitPipelineCommitMap(Pipeline pipeline) {
        Map<PipelineCommit, PipelineCommit> pipelineCommitMap= new HashMap<>();
        for (PipelineCommit pc : pipeline.getCommits()) {
            pipelineCommitMap.put(pc, pc);
        }
        return pipelineCommitMap;
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
}
