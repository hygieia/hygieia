package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * A {@link Collector} repository
 */
public interface CollectorRepository extends BaseCollectorRepository<Collector> {

    List<Collector> findById(ObjectId id);

    Collector findByCollectorTypeAndNameContainingIgnoreCase(CollectorType collectorType, String name);
}
