package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Monitor2;
import com.capitalone.dashboard.request.Monitor2DataCreateRequest;
import org.bson.types.ObjectId;

import java.util.List;


public interface Monitor2Service {
    /**
     *
     * @return All registered services
     */
    Iterable<Monitor2> all();

    /**
     * All statuses for a given dashboard.
     *
     * @param dashboardId unique id of dashboard.
     * @return awsStatuses
     */
    List<Monitor2> dashboardMonitor2es(ObjectId dashboardId);

    /**
     * A particular status.
     */
    Monitor2 get(ObjectId awsStatusId);

    /**
     * Create a new status for a given dashboard.
     *
     * @param dashboardId id of dashboard
     * @param awsStatusDataCreateRequest the dat for the aws status request
     * @return Monitor2
     */
    Monitor2 create(ObjectId dashboardId, Monitor2DataCreateRequest awsStatusDataCreateRequest);

    /**
     * Update an existing aws status.
     *
     * @param dashboardId id of Dashboard
     * @param awsStatus updated Monitor2
     * @return Monitor2
     */
    Monitor2 update(ObjectId dashboardId, Monitor2 awsStatus);

    /**
     * Delete an existing aws status.
     *
     * @param dashboardId id of Dashboard
     * @param awsStatusId id of awsStatus
     */
    void delete(ObjectId dashboardId, ObjectId awsStatusId);
}
