package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.HudsonJob;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface HudsonJobRepository extends BaseCollectorItemRepository<HudsonJob> {

    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, options.jobName : ?2}")
    HudsonJob findHudsonJob(ObjectId collectorId, String instanceUrl, String jobName);

    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, enabled: true}")
    List<HudsonJob> findEnabledHudsonJobs(ObjectId collectorId, String instanceUrl);
}
