package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import com.capitalone.dashboard.model.CoverityProject;

public interface CoverityProjectRepository extends BaseCollectorItemRepository<CoverityProject>{

    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, enabled: true}")
	List<CoverityProject> findEnabledProjects(ObjectId id, String instanceUrl);

}
