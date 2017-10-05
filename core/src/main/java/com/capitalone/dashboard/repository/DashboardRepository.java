package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Owner;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.List;

/**
 * {@link Dashboard} repository.
 */
public interface DashboardRepository extends PagingAndSortingRepository<Dashboard, ObjectId> {
	
	List<Dashboard> findByOwner(String owner);
	List<Dashboard> findByOwners(Owner owner);
	List<Dashboard> findByTitle(String title);
	Dashboard findByTitleAndType(String title, String type);

    List<Dashboard> findByApplicationComponentsIn(Collection<Component> components);

	@Query(value="{'type': {$in : [null, 'Team']}}")
	List<Dashboard> findTeamDashboards();

	@Query(value="{'widgets.options.teams.collectorItemId': ?0 }")
	List<Dashboard> findProductDashboardsByTeamDashboardCollectorItemId(String teamDashboardCollectorItemId);

	Iterable<Dashboard> findAllByConfigurationItemBusServObjectId(ObjectId appObjectId);
	Iterable<Dashboard> findAllByConfigurationItemBusServObjectIdIn(Iterable<ObjectId> busServiceObjectIdList);
	Iterable<Dashboard> findAllByConfigurationItemBusAppObjectId(ObjectId compObjectId);
	Iterable<Dashboard> findAllByConfigurationItemBusServObjectIdAndConfigurationItemBusAppObjectId(ObjectId appObjectId, ObjectId compObjectId);

	Dashboard findByConfigurationItemBusServObjectIdAndConfigurationItemBusAppObjectId(ObjectId appObjectId, ObjectId compObjectId);

}
