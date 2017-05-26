package com.capitalone.dashboard.repository;

import java.util.Collection;
import java.util.List;

import com.capitalone.dashboard.model.BinaryArtifact;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Owner;

/**
 * {@link Dashboard} repository.
 */
public interface DashboardRepository extends PagingAndSortingRepository<Dashboard, ObjectId> {
	
	List<Dashboard> findByOwner(String owner);
	List<Dashboard> findByOwners(Owner owner);
	List<Dashboard> findByTitle(String title);

    List<Dashboard> findByApplicationComponentsIn(Collection<Component> components);

	@Query(value="{'type': {$in : [null, 'Team']}}")
	List<Dashboard> findTeamDashboards();

	@Query(value="{'widgets.options.teams.collectorItemId': ?0 }")
	List<Dashboard> findProductDashboardsByTeamDashboardCollectorItemId(String teamDashboardCollectorItemId);

	Iterable<Dashboard> findAllByConfigurationItemAppObjectId(ObjectId appObjectId);
	Iterable<Dashboard> findAllByConfigurationItemComponentObjectId(ObjectId compObjectId);
	Iterable<Dashboard> findAllByConfigurationItemComponentObjectIdAndConfigurationItemAppObjectId(ObjectId compObjectId, ObjectId appObjectId);

}
