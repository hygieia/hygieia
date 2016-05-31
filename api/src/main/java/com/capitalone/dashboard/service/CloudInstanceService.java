package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CloudInstance;
import com.capitalone.dashboard.model.CloudInstanceHistory;
import com.capitalone.dashboard.model.NameValue;
import com.capitalone.dashboard.request.CloudInstanceCreateRequest;
import com.capitalone.dashboard.request.CloudInstanceListRefreshRequest;

import java.util.Collection;
import java.util.List;

public interface CloudInstanceService {
    //Instance Services

    Collection<String> refreshInstances(CloudInstanceListRefreshRequest request);

    //Instance Upsert
    List<String> upsertInstance(List<CloudInstanceCreateRequest> instance) throws HygieiaException;

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
     *     Instance History Aggregated Data by account
     */
    Collection<CloudInstanceHistory> getInstanceHistoryByAccount(String account);
}
