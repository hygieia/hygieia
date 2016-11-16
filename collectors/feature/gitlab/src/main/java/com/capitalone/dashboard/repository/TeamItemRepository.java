package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;

public interface TeamItemRepository extends ScopeOwnerRepository {
	
    @Query(value="{ 'collectorId' : ?0, enabled: true}")
    List<ScopeOwnerCollectorItem> findEnabledTeams(ObjectId collectorId);

}
