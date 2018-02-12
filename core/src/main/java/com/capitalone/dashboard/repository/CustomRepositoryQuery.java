package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;


public interface CustomRepositoryQuery {
    List<CollectorItem> findCollectorItemsBySubsetOptions(ObjectId id, Map<String, Object> allOptions, Map<String, Object> uniqueOptions);
    List<Component> findComponents(Collector collector);
    List<Component> findComponents(CollectorType collectorType);
    List<Component> findComponents(Collector collector, CollectorItem collectorItem);
    List<Component> findComponents(ObjectId collectorId, CollectorType collectorType, CollectorItem collectorItem);
    List<Component> findComponents(ObjectId collectorId, CollectorType collectorType, ObjectId collectorItemId);
    Page<CollectorItem> findByCollectorIdInAndJobNameContainingAndNiceNameContainingAllIgnoreCase(List<ObjectId> collectorId, String jobName, String niceName, Pageable pageable);
    Page<CollectorItem> findByCollectorIdInAndJobNameContainingIgnoreCase(List<ObjectId> collectorId, String jobName,Pageable pageable);
}
