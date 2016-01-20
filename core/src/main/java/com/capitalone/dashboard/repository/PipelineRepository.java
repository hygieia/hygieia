package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Pipeline;
import org.bson.types.ObjectId;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface PipelineRepository extends CrudRepository<Pipeline, ObjectId>, QueryDslPredicateExecutor<Pipeline> {

    Pipeline findByCollectorItemId(ObjectId collectorItemId);
}
