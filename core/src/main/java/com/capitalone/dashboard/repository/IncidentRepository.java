package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Incident;
import org.bson.types.ObjectId;
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

    @Query("{ 'collectorItemId' : {$in : ?0} }")
    List<Incident> findByCollectorItemId(List<ObjectId> collectorItemIds);
}
