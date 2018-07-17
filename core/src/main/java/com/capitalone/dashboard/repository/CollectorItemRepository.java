package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.CollectorItem;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value="{ 'collectorId' : ?0, options.url : {$regex : '^?1$', $options: 'i'}, options.branch : {$regex : '^?2$', $options: 'i'}, enabled : ?3}")
    CollectorItem findRepoByUrlAndBranch(ObjectId collectorId, String url, String branch, boolean enabled);

    @Query(value="{ 'collectorId': { $in: ?0 }, ?1 : {$regex : '.*?2.*', $options: 'i'}}")
    Page<CollectorItem> findByCollectorIdAndSearchField(List<ObjectId> collectorId, String searchField, String searchFieldValue, Pageable pageable);

    @Query(value="{ 'collectorId': { $in: ?0 }, ?1 : {$regex : '.*?2.*', $options: 'i'} ,  ?3 : {$regex : '.*?4.*', $options: 'i'}}")
    Page<CollectorItem> findByCollectorIdAndSearchFields(List<ObjectId> collectorId,String searchField1, String searchFieldValue1, String searchField2,String searchFieldValue2,  Pageable pageable);

    List<CollectorItem> findByDescription(String description);

}
