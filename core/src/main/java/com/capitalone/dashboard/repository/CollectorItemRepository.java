package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.CollectorItem;
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
    CollectorItem findByOptionsAndDeployedApplicationName(ObjectId collectorId, String applicationName);
    @Query(value="{'options.jobName' : ?2, 'niceName' : ?1, 'collectorId' : ?0}")
    CollectorItem findByCollectorIdNiceNameAndJobName(ObjectId collectorId, String niceName, String jobName);
}
