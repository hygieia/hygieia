package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Component;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * {@link Component} repository.
 */
public interface ComponentRepository extends CrudRepository<Component, ObjectId> {


    @Query(value = "{'collectorItems.SCM._id': ?0}")
    List<Component> findBySCMCollectorItemId(ObjectId scmCollectorItemId);

    @Query(value="{'collectorItems.Build._id': ?0}")
    List<Component> findByBuildCollectorItemId(ObjectId buildCollectorItemId);

    @Query(value="{'collectorItems.Deployment._id': ?0}")
    List<Component> findByDeployCollectorItemId(ObjectId deployCollectorItemId);
}
