package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.NexusIQApplication;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface NexusIQApplicationRepository extends BaseCollectorItemRepository<NexusIQApplication> {

    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, options.applicationId : ?2}")
    NexusIQApplication findNexusIQApplication(ObjectId collectorId, String instanceUrl, String applicationId);

    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, enabled: true}")
    List<NexusIQApplication> findEnabledApplications(ObjectId collectorId, String instanceUrl);
}
