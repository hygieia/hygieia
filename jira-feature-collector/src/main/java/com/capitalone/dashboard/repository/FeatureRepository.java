package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Feature;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository for {@link Feature}.
 */
public interface FeatureRepository extends CrudRepository<Feature, ObjectId>,
		QueryDslPredicateExecutor<Feature> {
	@Query(value = "{ $query: { 'collectorId' : ?0, 'changeDate' : {$gt: ?1}}, $orderby: { 'changeDate' :-1 }}", fields = "{'changeDate' : 1, '_id' : 0}")
	List<Feature> getFeatureMaxChangeDate(ObjectId collectorId,
			String lastChangeDate);

	@Query(value = "{ $query: {'sId' : ?0},{'sId' : 1}}")
	List<Feature> getFeatureIdById(String sId);
}
