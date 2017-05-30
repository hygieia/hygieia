package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;

import com.capitalone.dashboard.model.CollectorItem;

public interface WidgetRepository extends CollectorItemRepository {
    
    List<CollectorItem> findByCollectorIdAndEnabled(ObjectId collectorId, boolean enabled);

}
