package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.model.TeamInventory;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository for {@link Scope}.
 */
public interface TeamInventoryRepository extends CrudRepository<TeamInventory, ObjectId>,
		QueryDslPredicateExecutor<TeamInventory> {

	@Query(value = "{ 'collectorId' : ?0 }")
	List<TeamInventory> findByCollectorId(ObjectId collectorId);

	TeamInventory findByNameAndTeamId(String name, String teamId);

}
