package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.UDeployApplication;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Repository for {@link UDeployApplication}s.
 */
public interface UDeployApplicationRepository extends BaseCollectorItemRepository<UDeployApplication> {

    /**
     * Find a {@link UDeployApplication} by UDeploy instance URL and UDeploy application id.
     *
     * @param collectorId ID of the {@link com.capitalone.dashboard.model.UDeployCollector}
     * @param instanceUrl UDeploy instance URL
     * @param applicationId UDeploy application ID
     * @return a {@link UDeployApplication} instance
     */
    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, options.applicationId : ?2}")
    UDeployApplication findUDeployApplication(ObjectId collectorId, String instanceUrl, String applicationId);

    /**
     * Finds all {@link UDeployApplication}s for the given instance URL.
     *
     * @param collectorId ID of the {@link com.capitalone.dashboard.model.UDeployCollector}
     * @param instanceUrl UDeploy instance URl
     * @return list of {@link UDeployApplication}s
     */
    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, enabled: true}")
    List<UDeployApplication> findEnabledApplications(ObjectId collectorId, String instanceUrl);
}
