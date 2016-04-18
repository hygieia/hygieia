package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.CloudVirtualNetwork;
import com.capitalone.dashboard.model.NameValue;
import com.capitalone.dashboard.response.CloudVirtualNetworkAggregatedResponse;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.List;


public interface CloudVirtualNetworkService {
    //Virtual Network Services
    //Upsert Virtual Network
    List<ObjectId> upsertVirtualNetwork(List<CloudVirtualNetwork> virtualNetwork);

    /**
     *     Virtual Network Details by
     *          (a) componentId - for UI mostly
     *          (b) virtualNetworkId
     *          (c) List of virtualNetworkIds
     *          (d) List of Tags
     */
    Collection<CloudVirtualNetwork> getVirtualNetworkDetails (Object componentId);
    CloudVirtualNetwork getVirtualNetworkDetails(String virtualNetworkId);
    Collection<CloudVirtualNetwork> getVirtualNetworkDetails(List<String> virtualNetworkId);
    Collection<CloudVirtualNetwork> getVirtualNetworkDetailsByTags (List<NameValue> tags);

    /**
     *     Virtual Network Aggregated Data by
     *          (a) componentId - for UI mostly
     *          (b) subnetId
     *          (c) List of subnet Ids
     *          (d) List of Tags
     */
    CloudVirtualNetworkAggregatedResponse getVirtualNetworkAggregated(ObjectId componentId);
    CloudVirtualNetworkAggregatedResponse getVirtualNetworkAggregatedByTags(List<NameValue> tags);



}
