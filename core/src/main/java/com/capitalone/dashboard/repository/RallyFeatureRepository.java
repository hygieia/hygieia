package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.RallyFeature;

/**
 * Repository for {@link CodeQuality} data.
 */
public interface RallyFeatureRepository extends CrudRepository<RallyFeature, ObjectId>, QueryDslPredicateExecutor<RallyFeature> {

    /**
     * Finds the {@link Rallyfeature} data point at the given timestamp for a specific
     * {@link com.capitalone.dashboard.model.CollectorItem}.
     *
     * @param collectorItemId collector item id
     * @param timestamp timstamp
     * @return a {@link RallyFeature}
     */
    RallyFeature findByCollectorItemIdAndTimestamp(ObjectId collectorItemId, long timestamp);
    
    
    @Query(value="{'projectId' : ?0}")
    List<RallyFeature> findByIterationLists(String projectId);
  
    @Query(value="{'projectId' : ?0, options.iterationId : ?1}")
    RallyFeature findByRallyWidgetDetails(String projectId,String iterationId);
    
    @Query(value="{'collectorItemId' :?0}")
    List<RallyFeature> findByProjectIterationId(Object collectorItemId);
    
    List<RallyFeature> findByCollectorItemIdAndRemainingDaysNot(Object collectorItemId, int remainginDays);
    

	List<RallyFeature> findByProjectId(String projectId);
    
}

