package com.capitalone.dashboard.event;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.PipelineRepository;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;

import java.util.AbstractMap;
import java.util.List;

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

    protected Pipeline getOrCreatePipeline(AbstractMap.SimpleEntry<Dashboard, CollectorItem> dashboard) {
        Pipeline pipeline = getPipelineRepository().findByCollectorItemId(dashboard.getValue().getId());
        if(pipeline == null){
            pipeline = new Pipeline();
            pipeline.setCollectorItemId(dashboard.getValue().getId());
        }
        return pipeline;
    }

    protected abstract CollectorItemRepository getCollectorItemRepository();
    protected abstract CollectorRepository getCollectorRepository();
    protected abstract PipelineRepository getPipelineRepository();


}
