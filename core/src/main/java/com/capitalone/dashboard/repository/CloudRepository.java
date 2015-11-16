package com.capitalone.dashboard.repository;

import org.bson.types.ObjectId;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.Cloud;
import com.capitalone.dashboard.model.CloudComputeData;

public interface CloudRepository extends
		CrudRepository<Cloud, ObjectId>,
		QueryDslPredicateExecutor<Cloud>{

//	@Query(value = "{ cottectorItemId:  ?0 }")
	Cloud findByCollectorItemId(ObjectId collectorItemId);

}
