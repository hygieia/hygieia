package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;

public interface TeamItemRepository extends BaseCollectorItemRepository<ScopeOwnerCollectorItem> {
	
    @Query(value="{ 'collectorId' : ?0, enabled: true}")
    List<ScopeOwnerCollectorItem> findEnabledTeams(ObjectId collectorId);

}
