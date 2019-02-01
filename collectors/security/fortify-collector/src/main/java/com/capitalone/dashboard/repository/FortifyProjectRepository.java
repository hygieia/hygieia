package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import com.capitalone.dashboard.model.FortifyProject;

public interface FortifyProjectRepository extends BaseCollectorItemRepository<FortifyProject>{

    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, enabled: true}")
	List<FortifyProject> findEnabledProjects(ObjectId id, String instanceUrl);

}
