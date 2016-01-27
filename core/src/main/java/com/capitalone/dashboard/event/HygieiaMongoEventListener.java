package com.capitalone.dashboard.event;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.PipelineRepository;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class HygieiaMongoEventListener<T> extends AbstractMongoEventListener<T> {



    private Collector getProductCollector(){
        List<Collector> productCollectors = getCollectorRepository().findByCollectorType(CollectorType.Product);
        if(productCollectors.isEmpty()){
            return null;
        }
        return productCollectors.get(0);
    }

    protected CollectorItem getTeamDashboardCollectorItem(Dashboard teamDashboard) {
        ObjectId productCollectorId = getProductCollector().getId();
        ObjectId dashboardId = teamDashboard.getId();
        return getCollectorItemRepository().findTeamDashboardCollectorItemsByCollectorIdAndDashboardId(productCollectorId, dashboardId.toString());
    }

    /**
     * Finds a SCM in a pipeline by way of the pipelineCommitMap
     *
     * @param pipeline
     * @param pipelineCommitMap
     * @param scm
     * @return
     */
    protected PipelineCommit findOrCreatePipelineCommit(Pipeline pipeline, Map<PipelineCommit, PipelineCommit> pipelineCommitMap, SCM scm) {
        PipelineCommit pipelineCommit = pipelineCommitMap.get(new PipelineCommit(scm));
        if(pipelineCommit == null){
            pipelineCommit = new PipelineCommit(scm);
            pipeline.getCommits().add(pipelineCommit);
        }
        return pipelineCommit;
    }

    /**
     * Builds a map of commits for a given pipeline.  Allows for quickly finding/updating commits
     * @param pipeline
     * @return
     */
    protected Map<PipelineCommit, PipelineCommit> buildPipelineCommitPipelineCommitMap(Pipeline pipeline) {
        Map<PipelineCommit, PipelineCommit> pipelineCommitMap= new HashMap<>();
        for (PipelineCommit pc : pipeline.getCommits()) {
            pipelineCommitMap.put(pc, pc);
        }
        return pipelineCommitMap;
    }


    protected Pipeline getOrCreatePipeline(AbstractMap.SimpleEntry<Dashboard, CollectorItem> dashboard) {
        Pipeline pipeline = getPipelineRepository().findByCollectorItemId(dashboard.getValue().getId());
        if(pipeline == null){
            pipeline = new Pipeline();
            pipeline.setName(dashboard.getKey().getTitle());
            pipeline.setCollectorItemId(dashboard.getValue().getId());
        }
        return pipeline;
    }


    protected abstract CollectorItemRepository getCollectorItemRepository();
    protected abstract CollectorRepository getCollectorRepository();
    protected abstract PipelineRepository getPipelineRepository();


}
