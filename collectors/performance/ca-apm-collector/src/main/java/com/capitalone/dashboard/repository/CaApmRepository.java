package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import com.capitalone.dashboard.model.CaApmCollectorItem;

/**
 * Repository for {@link CaApmCollectorItem}s.
 */
public interface CaApmRepository extends BaseCollectorItemRepository<CaApmCollectorItem> {

	@Query(value = "{ 'collectorId' : ?0, options.manModuleName : ?1, options.domainName : ?2}")
	CaApmCollectorItem findModule(ObjectId collectorId, String manModuleName, String domainName);

	@Query(value = "{ 'collectorId' : ?0, enabled: true}")
	List<CaApmCollectorItem> findEnabledModules(ObjectId collectorId);

}
