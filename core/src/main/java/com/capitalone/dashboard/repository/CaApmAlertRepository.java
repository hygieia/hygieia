package com.capitalone.dashboard.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.CaApm;

/**
 * Repository for {@link CaApm} data.
 */
public interface CaApmAlertRepository extends CrudRepository<CaApm, ObjectId>, QueryDslPredicateExecutor<CaApm> {
	@Query(value="{'manModuleName' : ?0}") 
	Iterable<CaApm> getAlertsByManageModuleName(String manModuleName);
}
