package com.capitalone.dashboard.repository;

import org.bson.types.ObjectId;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.CloudComputeAggregatedData;
import com.capitalone.dashboard.model.Feature;

public interface CloudAggregatedDataRepository extends CrudRepository<CloudComputeAggregatedData, ObjectId>, QueryDslPredicateExecutor<Feature> {

}
