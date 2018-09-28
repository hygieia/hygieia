package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.path.PathBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Map;

/**
 * {@link Component} repository.
 */
public interface ComponentRepository extends CrudRepository<Component, ObjectId>,QueryDslPredicateExecutor<Component> {


    @Query(value = "{'collectorItems.SCM._id': ?0}")
    List<Component> findBySCMCollectorItemId(ObjectId scmCollectorItemId);

    @Query(value="{'collectorItems.Build._id': ?0}")
    List<Component> findByBuildCollectorItemId(ObjectId buildCollectorItemId);

    @Query(value="{'collectorItems.Deployment._id': ?0}")
    List<Component> findByDeployCollectorItemId(ObjectId deployCollectorItemId);

    @Query(value="{'collectorItems.Incident.enabled' : ?0}")
    List<Component> findByIncidentCollectorItems(boolean enabled);


    default List<Component> findByCollectorTypeAndItemIdIn(CollectorType collectorType, List<ObjectId> collectorItemIds) {
        BooleanBuilder builder = new BooleanBuilder();
        PathBuilder<Component> path = new PathBuilder<>(Component.class, "components");
        builder.and(path.get("collectorItems", Map.class).get(collectorType.toString(),List.class).get("id", ObjectId.class).in(collectorItemIds));
        return (List<Component>) findAll(builder.getValue());
    }
}