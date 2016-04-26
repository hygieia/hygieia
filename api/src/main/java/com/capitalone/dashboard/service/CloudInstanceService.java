package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.CloudInstance;
import com.capitalone.dashboard.model.NameValue;
import com.capitalone.dashboard.request.CloudInstanceAggregateRequest;
import com.capitalone.dashboard.request.CloudInstanceListRefreshRequest;
import com.capitalone.dashboard.response.CloudInstanceAggregatedResponse;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.List;

public interface CloudInstanceService {
    //Instance Services

    Collection<String> refreshInstances(CloudInstanceListRefreshRequest request);

    //Instance Upsert
    List<ObjectId> upsertInstance(List<CloudInstance> instance);

    /**
     *     Instance Details by
     *          (a) componentId - for UI mostly
     *          (b) instanceId
     *          (c) List of instance Ids
     *          (d) List of Tags
     *          (e) Account Number
     */
    Collection<CloudInstance> getInstanceDetailsByComponentId(String componentId);
    CloudInstance getInstanceDetailsByInstanceId(String instanceId);
    Collection<CloudInstance> getInstanceDetailsByInstanceIds(List<String> instanceId);
    Collection<CloudInstance> getInstanceDetailsByTags(List<NameValue> tags);
    Collection<CloudInstance> getInstanceDetailsByAccount(String accountNumber);


    /**
     *     Instance Aggregated Data by
     *          (a) componentId - for UI mostly
     *          (b) Custom request object
     *          (d) List of Tags
     */
    CloudInstanceAggregatedResponse getInstanceAggregatedData(String componentId);
    CloudInstanceAggregatedResponse getInstanceAggregatedData(CloudInstanceAggregateRequest request);
}
