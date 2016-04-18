package com.capitalone.dashboard.service;

import com.capitalone.dashboard.config.collector.CloudConfig;
import com.capitalone.dashboard.model.CloudSubNetwork;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.NameValue;
import com.capitalone.dashboard.repository.CloudSubNetworkRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.response.CloudSubNetworkAggregatedResponse;
import com.capitalone.dashboard.util.HygieiaUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public class CloudSubnetServiceImpl implements CloudSubnetService {
    private static final Log logger = LogFactory
            .getLog(CloudSubnetServiceImpl.class);

    private final CloudSubNetworkRepository cloudSubNetworkRepository;
    private final ComponentRepository componentRepository;

    @Autowired
    public CloudSubnetServiceImpl(CloudSubNetworkRepository cloudSubNetworkRepository,
                                  ComponentRepository cloudConfigRepository) {
        this.cloudSubNetworkRepository = cloudSubNetworkRepository;
        this.componentRepository = cloudConfigRepository;
    }


    private CollectorItem getCollectorItem(ObjectId componentId) {
        Component component = componentRepository.findOne(componentId);
        if (CollectionUtils.isEmpty(component.getCollectorItems())) return null;
        return component.getCollectorItems().get(CollectorType.Cloud).get(0);
    }

    public Collection<CloudSubNetwork> getSubNetworkDetails(CollectorItem item) {
        Collection<CloudSubNetwork> subnets = new HashSet<>();
        if ((item != null) && (item instanceof CloudConfig)) {
            CloudConfig config = (CloudConfig) item;
            subnets.addAll(getSubNetworkDetailsByTags(config.getTags()));
        }
        return subnets;
    }

    @Override
    public List<ObjectId> upsertSubNetwork(List<CloudSubNetwork> subnets){
        List<ObjectId> objectIds = new ArrayList<>();
        for (CloudSubNetwork ci : subnets) {
            CloudSubNetwork existing = getSubNetworkDetails(ci.getSubnetId());
            if (existing == null) {
                CloudSubNetwork in = cloudSubNetworkRepository.save(ci);
                objectIds.add(in.getId());
            } else {
                try {
                    HygieiaUtils.mergeObjects(existing, ci);
                    cloudSubNetworkRepository.save(existing);
                    objectIds.add(existing.getId());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.error("Error saving cloud instance info for instanceID: " + ci.getSubnetId(), e);
                }
            }
        }
        return objectIds;
    }

    @Override
    public Collection<CloudSubNetwork> getSubNetworkDetails(ObjectId componentId) {
        return getSubNetworkDetails(getCollectorItem(componentId));
    }

    @Override
    public CloudSubNetwork getSubNetworkDetails(String subnetId) {
        return cloudSubNetworkRepository.findBySubnetId(subnetId);
    }

    @Override
    public Collection<CloudSubNetwork> getSubNetworkDetails(List<String> subnetIds) {
        return cloudSubNetworkRepository.findBySubnetIdIn(subnetIds);
    }

    @Override
    public Collection<CloudSubNetwork> getSubNetworkDetailsByTags(List<NameValue> tags) {
        Set<CloudSubNetwork> subnets = new HashSet<>();
        for (NameValue nv : tags) {
            subnets.addAll(cloudSubNetworkRepository.findByTagNameAndValue(nv.getName(), nv.getValue()));
        }
        return subnets;
    }

    @Override
    public CloudSubNetworkAggregatedResponse getSubNetworkAggregatedData(ObjectId componentId) {
        return new CloudSubNetworkAggregatedResponse();
    }

    @Override
    public CloudSubNetworkAggregatedResponse getSubNetworkAggregatedDataByTags(List<NameValue> tags) {
        return new CloudSubNetworkAggregatedResponse();
    }


}
