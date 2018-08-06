package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Incident;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository for {@link Incident} data.
 */

public interface IncidentRepository extends CrudRepository<Incident, ObjectId> {
    Incident findByIncidentID(String incidentID);

    @Query("{ 'severity' : { $in : ?0 } }")
    List<Incident> findBySeverity(String[] severityValues);
}
