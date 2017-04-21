package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import com.capitalone.dashboard.model.Scope;

public interface ProjectItemRepository extends ScopeRepository {
	
	@Query(value="{ 'collectorId' : ?0}")
	List<Scope> findScopeByCollectorId(ObjectId collectorId);

}
