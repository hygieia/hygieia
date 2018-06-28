package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.RallyProject;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface RallyProjectRepository extends BaseCollectorItemRepository<RallyProject> {

    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, options.projectId : ?2}")
    RallyProject findRallyProject(ObjectId collectorId, String instanceUrl, String projectId);

    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, enabled: true}")
    List<RallyProject> findEnabledProjects(ObjectId collectorId, String instanceUrl);
    
    @Query(value="{'options.projectId' :?0}")
    List<RallyProject> findByProjectCollectorItemId(String projectId);
}

