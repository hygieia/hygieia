package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.AwsStatus;
import com.capitalone.dashboard.request.AwsStatusDataCreateRequest;
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
     * @param awsStatusDataCreateRequest the dat for the aws status request
     * @return AwsStatus
     */
    AwsStatus create(ObjectId dashboardId, AwsStatusDataCreateRequest awsStatusDataCreateRequest);

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
