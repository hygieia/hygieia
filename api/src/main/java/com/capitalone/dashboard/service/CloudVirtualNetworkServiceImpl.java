package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.CloudVirtualNetwork;
import com.capitalone.dashboard.model.NameValue;
import com.capitalone.dashboard.repository.CloudVirtualNetworkRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.response.CloudVirtualNetworkAggregatedResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class CloudVirtualNetworkServiceImpl implements CloudVirtualNetworkService {
    private static final Log logger = LogFactory
            .getLog(CloudVirtualNetworkServiceImpl.class);

    private final CloudVirtualNetworkRepository cloudVirtualNetworkRepository;
    private final ComponentRepository componentRepository;

    @Autowired
    public CloudVirtualNetworkServiceImpl(CloudVirtualNetworkRepository cloudVirtualNetworkRepository,
                                          ComponentRepository cloudConfigRepository) {
        this.cloudVirtualNetworkRepository = cloudVirtualNetworkRepository;
        this.componentRepository = cloudConfigRepository;
    }

    @Override
    public List<ObjectId> upsertVirtualNetwork(List<CloudVirtualNetwork> virtualNetwork) {
        return null;
    }

    @Override
    public Collection<CloudVirtualNetwork> getVirtualNetworkDetails(Object componentId) {
        return null;
    }

    @Override
    public CloudVirtualNetwork getVirtualNetworkDetails(String virtualNetworkId) {
        return null;
    }

    @Override
    public Collection<CloudVirtualNetwork> getVirtualNetworkDetails(List<String> virtualNetworkId) {
        return null;
    }

    @Override
    public Collection<CloudVirtualNetwork> getVirtualNetworkDetailsByTags(List<NameValue> tags) {
        return null;
    }

    @Override
    public CloudVirtualNetworkAggregatedResponse getVirtualNetworkAggregated(ObjectId componentId) {
        return null;
    }

    @Override
    public CloudVirtualNetworkAggregatedResponse getVirtualNetworkAggregatedByTags(List<NameValue> tags) {
        return null;
    }
}
