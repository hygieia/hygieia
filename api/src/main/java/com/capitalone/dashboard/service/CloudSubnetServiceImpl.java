package com.capitalone.dashboard.service;

import com.capitalone.dashboard.config.collector.CloudConfig;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CloudSubNetworkRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.CloudInstanceListRefreshRequest;
import com.capitalone.dashboard.request.CloudSubnetCreateRequest;
import com.capitalone.dashboard.response.CloudSubNetworkAggregatedResponse;
import com.capitalone.dashboard.util.HygieiaUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

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
    public Collection<String> refreshSubnets(CloudInstanceListRefreshRequest request) {

        Collection<CloudSubNetwork> existing = cloudSubNetworkRepository.findByAccountNumber(request.getAccountNumber());
        Set<CloudSubNetwork> toDelete = new HashSet<>();
        Set<String> deletedIds = new HashSet<>();
        if (CollectionUtils.isEmpty(request.getInstanceIds()) || CollectionUtils.isEmpty(existing))
            return new ArrayList<>();

        for (CloudSubNetwork ci : existing) {
            if (!request.getInstanceIds().contains(ci.getSubnetId())) {
                toDelete.add(ci);
                deletedIds.add(ci.getSubnetId());
            }
        }
        if (CollectionUtils.isEmpty(toDelete)) {
            cloudSubNetworkRepository.delete(toDelete);
        }
        return deletedIds;
    }

    private CloudSubNetwork createSubnetworkObject (CloudSubnetCreateRequest request) {
        CloudSubNetwork subnet = new CloudSubNetwork();
        subnet.setAvailableIPCount(request.getAvailableIPCount());
        subnet.setDefaultForZone(request.isDefaultForZone());
        subnet.setState(request.getState());
        subnet.setVirtualNetworkId(request.getVirtualNetworkId());
        subnet.setZone(request.getZone());
        subnet.setCidrBlock(request.getCidrBlock());
        subnet.setCidrCount(request.getCidrCount());
        subnet.setCreationDate(request.getCreationDate());
        subnet.setLastUpdateDate(request.getLastUpdateDate());
        subnet.setSubnetId(request.getSubnetId());
        subnet.setUsedIPCount(request.getUsedIPCount());
        subnet.getTags().addAll(request.getTags());
        subnet.setIpUsage(request.getIpUsage());
        return subnet;
    }

    @Override
    public List<String> upsertSubNetwork(List<CloudSubnetCreateRequest> subnets) {
        List<String> objectIds = new ArrayList<>();
        for (CloudSubnetCreateRequest csn : subnets) {
            CloudSubNetwork newObject = createSubnetworkObject(csn);
            CloudSubNetwork existing = getSubNetworkDetailsBySubnetId(csn.getSubnetId());
            if (existing == null) {
                CloudSubNetwork sn = cloudSubNetworkRepository.save(newObject);
                objectIds.add(sn.getId().toString());
            } else {
                try {
                    HygieiaUtils.mergeObjects(existing, newObject);
                    cloudSubNetworkRepository.save(existing);
                    objectIds.add(existing.getId().toString());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.error("Error saving cloud subnet information info for subnetId: " + csn.getSubnetId(), e);
                }
            }
        }
        return objectIds;
    }

    @Override
    public Collection<CloudSubNetwork> getSubNetworkDetailsByComponentId(String componentIdString) {
        return getSubNetworkDetails(getCollectorItem(new ObjectId(componentIdString)));
    }

    @Override
    public CloudSubNetwork getSubNetworkDetailsBySubnetId(String subnetId) {
        return cloudSubNetworkRepository.findBySubnetId(subnetId);
    }

    @Override
    public Collection<CloudSubNetwork> getSubNetworkDetailsBySubnetIds(List<String> subnetIds) {
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
    public CloudSubNetworkAggregatedResponse getSubNetworkAggregatedData(String componentIdString) {
        return new CloudSubNetworkAggregatedResponse();
    }

    @Override
    public CloudSubNetworkAggregatedResponse getSubNetworkAggregatedDataByTags(List<NameValue> tags) {
        return new CloudSubNetworkAggregatedResponse();
    }


}
