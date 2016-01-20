package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.capitalone.dashboard.model.Dashboard;

/**
 * {@link Dashboard} repository.
 */
public interface DashboardRepository extends PagingAndSortingRepository<Dashboard, ObjectId> {
	
	List<Dashboard> findByOwner(String owner);
	List<Dashboard> findByTitle(String title);

	@Query(value="{'widgets.options.teams.collectorItemId': ?0 }")
	List<Dashboard> findProductDashboardsByTeamDashboardCollectorItemId(String teamDashboardCollectorItemId);

}
