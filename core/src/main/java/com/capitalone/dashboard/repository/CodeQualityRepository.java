package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.CodeQuality;

import java.util.List;

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
     * @param timestamp timestamp
     * @return a {@link CodeQuality}
     */
    CodeQuality findByCollectorItemIdAndTimestamp(ObjectId collectorItemId, long timestamp);
    
    List<CodeQuality> findByCollectorItemIdAndVersionOrderByTimestampDesc (ObjectId collectorItemId,String version);

    List<CodeQuality> findByCollectorItemIdAndNameAndVersionOrderByTimestampDesc (ObjectId collectorItemId,String name,String version);
    
    List<CodeQuality> findByCollectorItemIdOrderByTimestampDesc (ObjectId collectorItemId);
    
    List<CodeQuality> findByNameAndVersion(String name,String version);

    List<CodeQuality> findByNameAndVersionOrderByTimestampDesc(String name,String version);

    List<CodeQuality> findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(ObjectId collectorItemId, long beginDate, long endDate);
}
