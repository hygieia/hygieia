package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.BambooJob;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface BambooJobRepository extends BaseCollectorItemRepository<BambooJob> {

    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, options.jobName : ?2}")
    BambooJob findBambooJob(ObjectId collectorId, String instanceUrl, String jobName);

    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, enabled: true}")
    List<BambooJob> findEnabledBambooJobs(ObjectId collectorId, String instanceUrl);
}
