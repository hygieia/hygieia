package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Incident;
import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for {@link Incident} data.
 */

public interface IncidentRepository extends CrudRepository<Incident, ObjectId> {
    Incident findByIncidentID(String incidentID);
}
