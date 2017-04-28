package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.CollectorItem;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

/**
 * A {@link CollectorItem} repository
 */
public interface CollectorItemRepository extends BaseCollectorItemRepository<CollectorItem> {

    //// FIXME: 1/20/16 I really hate this dashboard specific method in the collectoritem repository, should we move the dashboardcollectoritem repository into core?
    @Query(value="{'options.dashboardId': ?1, 'collectorId': ?0 }")
    CollectorItem findTeamDashboardCollectorItemsByCollectorIdAndDashboardId(ObjectId collectorId, String dashboardId);
    @Query(value="{'options.applicationName' : ?1, 'collectorId' : ?0}")
    List<CollectorItem> findByOptionsAndDeployedApplicationName(ObjectId collectorId, String applicationName);

    // FIXME: 3/1/16 Really need to refactor this. Do not want collector specific lookups here.
    @Query(value="{'options.jobName' : ?2, 'niceName' : ?1, 'collectorId' : ?0}")
    CollectorItem findByCollectorIdNiceNameAndJobName(ObjectId collectorId, String niceName, String jobName);
    @Query(value="{'options.projectId' : ?2, 'niceName' : ?1, 'collectorId' : ?0}")
    CollectorItem findByCollectorIdNiceNameAndProjectId(ObjectId collectorId, String niceName, String projectId);
}
