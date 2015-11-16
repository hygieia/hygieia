package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.Scope;

/**
 * Repository for {@link ProjectCollector}.
 */
public interface ProjectRepository extends CrudRepository<Scope, ObjectId>,
		QueryDslPredicateExecutor<Scope> {
	@Query(value = "{ $query: { 'collectorId' : ?0, 'changeDate' : {$gt: ?1}}, $orderby: { 'changeDate' :-1 }}", fields = "{'changeDate' : 1, '_id' : 0}")
	List<Scope> getProjectMaxChangeDate(ObjectId collectorId,
			String lastChangeDate);

	@Query(value = "{ $query: {'pId' : ?0},{'pId' : 1}}")
	List<Scope> getProjectIdById(String pId);
}
