package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.CloudInstance;
import com.capitalone.dashboard.model.NameValue;
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
     */
    Collection<CloudInstance> getInstanceDetails(ObjectId componentId);
    CloudInstance getInstanceDetails(String instanceId);
    Collection<CloudInstance> getInstanceDetails(List<String> instanceId);
    Collection<CloudInstance> getInstanceDetailsByTags(List<NameValue> tags);


    /**
     *     Instance Aggregated Data by
     *          (a) componentId - for UI mostly
     *          (b) List of instance Ids
     *          (d) List of Tags
     */
    CloudInstanceAggregatedResponse getInstanceAggregatedData(ObjectId componentId);
    CloudInstanceAggregatedResponse getInstanceAggregatedData(List<String> instanceIds);
    CloudInstanceAggregatedResponse getInstanceAggregatedDataByTags(List<NameValue> tags);


}
