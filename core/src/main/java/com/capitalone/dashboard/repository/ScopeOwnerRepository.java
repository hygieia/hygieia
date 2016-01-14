package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;

/**
 * CollectorItem repository for {@link ScopeOwnerCollectorItem}.
 */
public interface ScopeOwnerRepository extends BaseCollectorItemRepository<ScopeOwnerCollectorItem> {
	@Query(value = "{ 'collectorId' : ?0, 'options.teamId' : ?1, options.name : ?2, 'options.assetState': 'Active'}")
	ScopeOwnerCollectorItem findTeamCollector(ObjectId collectorId, String teamId, String name);

	@Query(value = "{ 'collectorId' : ?0, 'options.teamId' : ?1, enabled: true, 'options.assetState': 'Active'}")
	List<ScopeOwnerCollectorItem> findEnabledTeamCollectors(ObjectId collectorId, String teamId);

	/**
	 * This essentially returns the max change date from the collection, based
	 * on the last change date (or default delta change date property) available
	 * 
	 * @param collectorId
	 *            Collector ID of source system collector
	 * @param changeDate
	 *            Last available change date or delta begin date property
	 * @return A single Change Date value that is the maximum value of the
	 *         existing collection
	 */
	@Query(value = "{ 'collectorId' : ?0, 'options.changeDate' : {$gt: ?1}, '_class' : 'com.capitalone.dashboard.model.ScopeOwnerCollectorItem', 'options.assetState': 'Active'}")
	List<ScopeOwnerCollectorItem> findTopByChangeDateDesc(ObjectId collectorId, String changeDate);

	@Query(value = "{'options.teamId' : ?0}", fields = "{'options.teamId' : 1}")
	List<ScopeOwnerCollectorItem> getTeamIdById(String teamId);

	@Query(value = "{'options.assetState' : ?0}", delete = true)
	List<ScopeOwnerCollectorItem> delete(String assetState);
}
