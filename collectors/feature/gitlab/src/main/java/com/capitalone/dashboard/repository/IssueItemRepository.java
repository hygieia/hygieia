package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.Feature;

public interface IssueItemRepository extends CrudRepository<Feature, ObjectId> {

	@Query(value = "{'collectorId' : ?0, 'sTeamName' : ?1, 'sProjectName' : ?2}")
	List<Feature> getFeaturesByCollectorAndTeamNameAndProjectName(ObjectId collectorId, String team, String project);
}
