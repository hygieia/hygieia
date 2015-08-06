package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.JenkinsJob;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 *
 */
public interface JenkinsCucumberTestJobRepository extends BaseCollectorItemRepository<JenkinsJob> {

    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, enabled: true}")
    List<JenkinsJob> findEnabledJenkinsJobs(ObjectId collectorId, String instanceUrl);

    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, options.jobName : ?2}")
    JenkinsJob findJenkinsJob(ObjectId collectorId, String instanceUrl, String jobName);
}
