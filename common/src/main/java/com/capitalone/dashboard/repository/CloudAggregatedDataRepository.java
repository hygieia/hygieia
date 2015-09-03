package com.capitalone.dashboard.repository;

import org.bson.types.ObjectId;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.CloudAggregatedData;
import com.capitalone.dashboard.model.Feature;

public interface CloudAggregatedDataRepository extends CrudRepository<CloudAggregatedData, ObjectId>, QueryDslPredicateExecutor<Feature> {

}
