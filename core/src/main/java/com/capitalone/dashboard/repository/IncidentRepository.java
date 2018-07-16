package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Incident;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Repository for {@link Incident} data.
 */

public interface IncidentRepository extends MongoRepository<Incident, ObjectId> {
    Incident findByIncidentID(String incidentID);

    @Query("{ 'severity' : {$in : ?0} }")
    List<Incident> findBySeverity(String[] severityValues);

    @Query(value = "{'severity' : {$in : ?0}, 'openTime' : {$gt : ?1, $lt : ?2}}")
    Page<Incident> findIncidentsBySeverityAndOpenTimeBetween (String[] severityValues, long startDate, long endDate, Pageable pageable);

    @Query(value = "{'severity' : {$in : ?0}, 'openTime' : {$gt : ?1, $lt : ?2}}", count = true)
    long countIncidentsBySeverityAndOpenTimeBetween (String[] severityValues, long startDate, long endDate);

    @Query("{ 'collectorItemId' : {$in : ?0} }")
    List<Incident> findByCollectorItemId(List<ObjectId> collectorItemIds);
}
