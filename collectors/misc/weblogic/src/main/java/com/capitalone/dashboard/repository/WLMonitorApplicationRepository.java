package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import com.capitalone.dashboard.model.WLMonitorCollectorItem;

/**
 * Repository for {@link WLMonitorCollectorItem}s.
 */
public interface WLMonitorApplicationRepository extends BaseCollectorItemRepository<WLMonitorCollectorItem> {

	@Query(value="{ 'collectorId' : ?0}")
    List<WLMonitorCollectorItem> findAllApps(ObjectId collectorId);
	
	@Query(value="{ 'collectorId' : ?0, options.environmentName : ?1}")
	WLMonitorCollectorItem findVmonitorApplication(ObjectId collectorId, String environmentName);
	
	@Query(value="{ 'collectorId' : ?0, enabled: true}")
    List<WLMonitorCollectorItem> findEnabledApplications(ObjectId collectorId);

	@Query(value="{ 'collectorId' : ?0, description: ?1}")
	WLMonitorCollectorItem findVmonitorApplicationByCollectorIdAndDesc(ObjectId id, String desc);
	
}
