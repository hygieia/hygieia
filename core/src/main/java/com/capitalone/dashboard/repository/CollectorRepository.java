package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Collector;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * A {@link Collector} repository
 */
public interface CollectorRepository extends BaseCollectorRepository<Collector> {

    List<Collector> findById(ObjectId id);
}
