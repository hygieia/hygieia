package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Pipeline;
import org.bson.types.ObjectId;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PipelineRepository extends CrudRepository<Pipeline, ObjectId>, QueryDslPredicateExecutor<Pipeline> {

    Pipeline findByCollectorItemId(ObjectId collectorItemId);

    List<Pipeline> findByCollectorItemIdIn(List<ObjectId> collectorItemIds);

}
