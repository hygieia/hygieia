package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.CloudSubNetwork;
import com.capitalone.dashboard.model.NameValue;
import com.capitalone.dashboard.request.CloudInstanceListRefreshRequest;
import com.capitalone.dashboard.request.CloudSubnetCreateRequest;
import com.capitalone.dashboard.response.CloudSubNetworkAggregatedResponse;

import java.util.Collection;
import java.util.List;

public interface CloudSubnetService {




    //Subnetwork Service

    //Upsert Subnetwork
    Collection<String> refreshSubnets(CloudInstanceListRefreshRequest request);

    List<String> upsertSubNetwork(List<CloudSubnetCreateRequest> requests);
    /**
     *     Subnetwork Details by
     *          (a) componentId - for UI mostly
     *          (b) subnetId
     *          (c) List of subnet Ids
     *          (d) List of Tags
     */
    Collection<CloudSubNetwork> getSubNetworkDetailsByComponentId (String componentIdString);
    Collection<CloudSubNetwork> getSubNetworkDetailsByAccount(String accountNumber);
    CloudSubNetwork getSubNetworkDetailsBySubnetId(String subnetId);
    Collection<CloudSubNetwork> getSubNetworkDetailsBySubnetIds (List<String> subnetId);
    Collection<CloudSubNetwork> getSubNetworkDetailsByTags (List<NameValue> tags);

    /**
     *     Subnetwork Aggregated Data by
     *          (a) componentId - for UI mostly
     *          (b) subnetId
     *          (c) List of subnet Ids
     *          (d) List of Tags
     */
    CloudSubNetworkAggregatedResponse getSubNetworkAggregatedData (String componentIdString);
    CloudSubNetworkAggregatedResponse getSubNetworkAggregatedDataByTags (List<NameValue> tags);
}