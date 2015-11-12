package com.capitalone.dashboard.repository;

import org.bson.types.ObjectId;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.CloudComputeData;

public interface CloudComputeDataRepository extends
		CrudRepository<CloudComputeData, ObjectId>,
		QueryDslPredicateExecutor<CloudComputeData>{

//	@Query(value = "{ cottectorItemId:  ?0 }")
	CloudComputeData findByCollectorItemId(ObjectId collectorItemId);

}
