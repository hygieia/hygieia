package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.XLDeployApplication;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Repository for {@link XLDeployApplication}s.
 */
public interface XLDeployApplicationRepository extends BaseCollectorItemRepository<XLDeployApplication> {

    /**
     * Find a {@link XLDeployApplication} by XLDeploy instance URL and XLDeploy application id.
     *
     * @param collectorId ID of the {@link com.capitalone.dashboard.model.XLDeployCollector}
     * @param instanceUrl XLDeploy instance URL
     * @param applicationId XLDeploy application ID
     * @return a {@link XLDeployApplication} instance
     */
    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, options.applicationId : ?2}")
    XLDeployApplication findXLDeployApplication(ObjectId collectorId, String instanceUrl, String applicationId);

    /**
     * Finds all {@link XLDeployApplication}s for the given instance URL.
     *
     * @param collectorId ID of the {@link com.capitalone.dashboard.model.XLDeployCollector}
     * @param instanceUrl XLDeploy instance URl
     * @return list of {@link XLDeployApplication}s
     */
    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, enabled: true}")
    List<XLDeployApplication> findEnabledApplications(ObjectId collectorId, String instanceUrl);
}
