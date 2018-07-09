package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Incident;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IncidentUpdatesRepository {
    @Autowired
    private MongoOperations operations;

    public void save (Incident incident, String collectionName) {
        operations.save(incident, collectionName);
    }

    public void dropCollection (String collectionName) {
        operations.dropCollection(collectionName);
    }

    public List<Incident> fetchIncidents(String collectionName, Pageable pageable) {
        Query query = new Query().with(pageable);

        List<Incident> incidentList = operations.find(query, Incident.class, collectionName);
        return incidentList;
    }

    public long count(String collectionName) {
        BasicQuery basicQuery = new BasicQuery("{}");
        return operations.count(basicQuery, collectionName);
    }
}
