package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.Scope;

/**
 * Repository for {@link ScopeCollector}.
 */
public interface ScopeRepository extends CrudRepository<Scope, ObjectId>,
		QueryDslPredicateExecutor<Scope> {
	@Query(value = "{ 'collectorId' : ?0, 'changeDate' : {$gt: ?1}}", fields = "{'changeDate' : 1, '_id' : 0}")
	List<Scope> findTopByOrderByChangeDateDesc(ObjectId collectorId,
			String lastChangeDate);

	@Query(value = "{'pId' : ?0}", fields="{'pId' : 1}")
	List<Scope> getScopeIdById(String pId);

	@Query
	List<Scope> findByOrderByProjectPathDesc();

	@Query(value = "{'pId' : ?0 }")
	List<Scope> getScopeById(String pId);
}
