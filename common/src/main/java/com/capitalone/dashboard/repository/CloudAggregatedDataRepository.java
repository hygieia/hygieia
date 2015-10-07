package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.CloudComputeAggregatedData;
import com.capitalone.dashboard.model.Feature;

public interface CloudAggregatedDataRepository extends
		CrudRepository<CloudComputeAggregatedData, ObjectId> {

	@Query(value = "{ cottectorItemId:  ?0 }")
	CloudComputeAggregatedData getAggregatedData(ObjectId collectorItemId);

}
