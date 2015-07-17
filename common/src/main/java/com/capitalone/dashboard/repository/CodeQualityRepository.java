package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.CodeQuality;
import org.bson.types.ObjectId;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for {@link CodeQuality} data.
 */
public interface CodeQualityRepository extends CrudRepository<CodeQuality, ObjectId>, QueryDslPredicateExecutor<CodeQuality> {

    /**
     * Finds the {@link CodeQuality} data point at the given timestamp for a specific
     * {@link com.capitalone.dashboard.model.CollectorItem}.
     *
     * @param collectorItemId collector item id
     * @param timestamp timstamp
     * @return a {@link CodeQuality}
     */
    CodeQuality findByCollectorItemIdAndTimestamp(ObjectId collectorItemId, long timestamp);
}
