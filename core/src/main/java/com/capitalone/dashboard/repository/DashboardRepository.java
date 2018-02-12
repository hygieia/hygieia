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
	List<Dashboard> findByTitle(String title);
	Dashboard findByTitleAndType(String title, DashboardType type);

    List<Dashboard> findByApplicationComponentsIn(Collection<Component> components);

	@Query(value="{'type': {$in : [null, 'Team']}}")
	List<Dashboard> findTeamDashboards();

	@Query(value="{'widgets.options.teams.collectorItemId': ?0 }")
	List<Dashboard> findProductDashboardsByTeamDashboardCollectorItemId(String teamDashboardCollectorItemId);

	Iterable<Dashboard> findAllByConfigurationItemBusServName(String configurationItem);

	Iterable<Dashboard> findAllByConfigurationItemBusAppName(String configurationItem);

	Iterable<Dashboard> findAllByConfigurationItemBusAppNameIn(List<String> configurationItemList);

	Iterable<Dashboard> findAllByConfigurationItemBusServNameAndConfigurationItemBusAppName(String appName, String compName);

	Dashboard findByConfigurationItemBusServNameIgnoreCaseAndConfigurationItemBusAppNameIgnoreCase(String appName, String compName);

	Page<Dashboard> findAll(Pageable page);

	Page<Dashboard> findAllByTitleContainingIgnoreCase(String name, Pageable pageable);

	List<Dashboard> findAllByTitleContainingIgnoreCase(String name);

	long count();

	Page<Dashboard> findByOwners(Owner owner, Pageable pageable);

	List<Dashboard> findByOwnersAndTitleContainingIgnoreCase(Owner owner, String name);

	Page<Dashboard> findByOwnersAndTitleContainingIgnoreCase(Owner owner, String name, Pageable pageable);

}
