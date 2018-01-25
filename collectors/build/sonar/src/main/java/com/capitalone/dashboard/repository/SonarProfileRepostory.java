package com.capitalone.dashboard.repository;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import com.capitalone.dashboard.model.CollectorItemConfigHistory;
import com.capitalone.dashboard.model.ConfigHistOperationType;

public interface SonarProfileRepostory extends CollItemConfigHistoryRepository {
	
	@Query(value="{ 'collectorItemId' : ?0, userID : ?1, operation: ?2, timestamp : ?3}")
	List<CollectorItemConfigHistory> findProfileConfigChanges(ObjectId collectorId,String authorLogin,ConfigHistOperationType operation,long timestamp);

}
