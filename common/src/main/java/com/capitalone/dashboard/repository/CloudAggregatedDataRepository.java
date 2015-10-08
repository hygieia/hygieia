package com.capitalone.dashboard.repository;

import org.bson.types.ObjectId;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.CloudComputeAggregatedData;

public interface CloudAggregatedDataRepository extends
		CrudRepository<CloudComputeAggregatedData, ObjectId>,
		QueryDslPredicateExecutor<CloudComputeAggregatedData>{

//	@Query(value = "{ cottectorItemId:  ?0 }")
	CloudComputeAggregatedData findByCollectorItemId(ObjectId collectorItemId);

}
