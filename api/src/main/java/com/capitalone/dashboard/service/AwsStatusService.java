package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.AwsStatus;
import org.bson.types.ObjectId;

import java.util.List;


public interface AwsStatusService {
    /**
     *
     * @return All registered services
     */
    Iterable<AwsStatus> all();

    /**
     * All statuses for a given dashboard.
     *
     * @param dashboardId unique id of dashboard.
     * @return awsStatuses
     */
    List<AwsStatus> dashboardAwsStatuses(ObjectId dashboardId);

    /**
     * A particular status.
     */
    AwsStatus get(ObjectId awsStatusId);

    /**
     * Create a new status for a given dashboard.
     *
     * @param dashboardId id of dashboard
     * @param name name of new status
     * @param url url of the new status to check
     * @return AwsStatus
     */
    AwsStatus create(ObjectId dashboardId, String name, String url);

    /**
     * Update an existing aws status.
     *
     * @param dashboardId id of Dashboard
     * @param awsStatus updated AwsStatus
     * @return AwsStatus
     */
    AwsStatus update(ObjectId dashboardId, AwsStatus awsStatus);

    /**
     * Delete an existing aws status.
     *
     * @param dashboardId id of Dashboard
     * @param awsStatusId id of awsStatus
     */
    void delete(ObjectId dashboardId, ObjectId awsStatusId);
}
