package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import com.capitalone.dashboard.model.CollectorItem;

public interface JobRepository<T extends CollectorItem> extends BaseCollectorItemRepository<T> {
    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, options.jobName : ?2}")
    T findJob(ObjectId collectorId, String instanceUrl, String jobName);

    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, enabled: true}")
    List<T> findEnabledJobs(ObjectId collectorId, String instanceUrl);
}
