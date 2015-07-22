package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import com.capitalone.dashboard.model.TeamCollectorItem;

/**
 * CollectorItem repository for {@link TeamCollectorItem}.
 */
public interface TeamRepository extends
BaseCollectorItemRepository<TeamCollectorItem> {
	@Query(value="{ 'collectorId' : ?0, options.teamId : ?1, options.name : ?2}")
	TeamCollectorItem findTeamCollector(ObjectId collectorId, String teamId, String name);

    @Query(value="{ 'collectorId' : ?0, options.teamId : ?1, enabled: true}")
    List<TeamCollectorItem> findEnabledTeamCollectors(ObjectId collectorId, String teamId);

	@Query(value = "{ $query: { 'collectorId' : ?0, 'options.changeDate' : {$gt: ?1}, '_class' : 'com.capitalone.dashboard.model.TeamCollectorItem'}, $orderby: { 'options.changeDate' :-1 }}", fields="{'options.changeDate' : 1, '_id' : 0}")
	List<TeamCollectorItem> getTeamMaxChangeDate(ObjectId collectorId, String lastChangeDate);

	@Query(value = "{ $query: {'options.teamId' : ?0},{'options.teamId' : 1}}")
	List<TeamCollectorItem> getTeamIdById(String teamId);
}
