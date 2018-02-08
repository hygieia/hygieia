package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Owner;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	List<Dashboard> findByOwnersAndTypeContainingIgnoreCase(Owner owner, String type);

	List<Dashboard> findByTitle(String title);
	Dashboard findByTitleAndType(String title, DashboardType type);

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

	Page<Dashboard> findAll(Pageable page);
	Page<Dashboard> findAllByTypeContainingIgnoreCase(String type,Pageable pageable);

	Page<Dashboard> findAllByTitleContainingIgnoreCase(String name, Pageable pageable);
	Page<Dashboard> findAllByTypeContainingIgnoreCaseAndTitleContainingIgnoreCase(String type, String title, Pageable pageable);

	List<Dashboard> findAllByTitleContainingIgnoreCase(String name);
	List<Dashboard> findAllByTypeContainingIgnoreCaseAndTitleContainingIgnoreCase(String type, String title);

	long count();
	long countByTypeContainingIgnoreCase(String type);

	Page<Dashboard> findByOwners(Owner owner, Pageable pageable);
	Page<Dashboard> findByOwnersAndTypeContainingIgnoreCase(Owner owner, String type, Pageable pageable);

	List<Dashboard> findByOwnersAndTitleContainingIgnoreCase(Owner owner, String name);
	List<Dashboard> findByOwnersAndTypeContainingIgnoreCaseAndTitleContainingIgnoreCase(Owner owner, String type, String title);

	Page<Dashboard> findByOwnersAndTitleContainingIgnoreCase(Owner owner, String title, Pageable pageable);
	Page<Dashboard> findByOwnersAndTypeContainingIgnoreCaseAndTitleContainingIgnoreCase(Owner owner, String type, String title, Pageable pageable);

	Dashboard findDashboardByConfigurationItemBusServNameAndConfigurationItemBusAppName(String configurationItemBusServName, String configurationItemBusAppName);
}
