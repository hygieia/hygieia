package com.capitalone.dashboard.service;

import com.capitalone.dashboard.config.collector.CloudConfig;
import com.capitalone.dashboard.model.CloudVirtualNetwork;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.NameValue;
import com.capitalone.dashboard.repository.CloudVirtualNetworkRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.response.CloudVirtualNetworkAggregatedResponse;
import com.capitalone.dashboard.util.HygieiaUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service

public class CloudVirtualNetworkServiceImpl implements CloudVirtualNetworkService {


    private final CloudVirtualNetworkRepository cloudVirtualNetworkRepository;

    private final ComponentRepository componentRepository;

    @Autowired
    public CloudVirtualNetworkServiceImpl(CloudVirtualNetworkRepository cloudVirtualNetworkRepository,
                                          ComponentRepository cloudConfigRepository) {
        this.cloudVirtualNetworkRepository = cloudVirtualNetworkRepository;
        this.componentRepository = cloudConfigRepository;
    }

    private CollectorItem getCollectorItem(ObjectId componentId) {
        Component component = componentRepository.findOne(componentId);
        if (CollectionUtils.isEmpty(component.getCollectorItems())) return null;
        return component.getCollectorItems().get(CollectorType.Cloud).get(0);
    }

    @Override
    public List<ObjectId> upsertVirtualNetwork(List<CloudVirtualNetwork> virtualNetworks) {
        List<ObjectId> objectIds = new ArrayList<>();
        for (CloudVirtualNetwork cv : virtualNetworks) {
            CloudVirtualNetwork existing = getVirtualNetworkDetails(cv.getVirtualNetworkId());
            if (existing == null) {
                CloudVirtualNetwork in = cloudVirtualNetworkRepository.save(cv);
                objectIds.add(in.getId());
            } else {
                try {
                    HygieiaUtils.mergeObjects(existing, cv);
                    cloudVirtualNetworkRepository.save(existing);
                    objectIds.add(existing.getId());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    //logger.error("Error saving cloud instance info for instanceID: " + ci.getSubnetId(), e);
                }
            }
        }
        return objectIds;
    }

    public Collection<CloudVirtualNetwork> getVirtualNetworkDetails (CollectorItem item) {
        Collection<CloudVirtualNetwork> vns = new HashSet<>();
        if ((item != null) && (item instanceof CloudConfig)) {
            CloudConfig config = (CloudConfig) item;
            vns.addAll(getVirtualNetworkDetailsByTags(config.getTags()));
        }
        return vns;
    }

    @Override
    public Collection<CloudVirtualNetwork> getVirtualNetworkDetails(Object componentId) {
        return null;
    }

    @Override
    public CloudVirtualNetwork getVirtualNetworkDetails(String virtualNetworkId) {
        return cloudVirtualNetworkRepository.findByVirtualNetworkId(virtualNetworkId);
    }

    @Override
    public Collection<CloudVirtualNetwork> getVirtualNetworkDetails(List<String> virtualNetworkId) {
        return null;
    }

    @Override
    public Collection<CloudVirtualNetwork> getVirtualNetworkDetailsByTags(List<NameValue> tags) {
        Set<CloudVirtualNetwork> subnets = new HashSet<>();
        for (NameValue nv : tags) {
            subnets.addAll(cloudVirtualNetworkRepository.findByTagNameAndValue(nv.getName(), nv.getValue()));
        }
        return subnets;
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
