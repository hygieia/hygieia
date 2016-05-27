package com.capitalone.dashboard.service;

import com.capitalone.dashboard.config.collector.CloudConfig;
import com.capitalone.dashboard.model.CloudSubNetwork;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.NameValue;
import com.capitalone.dashboard.repository.CloudSubNetworkRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.CloudInstanceListRefreshRequest;
import com.capitalone.dashboard.request.CloudSubnetCreateRequest;
import com.capitalone.dashboard.response.CloudSubNetworkAggregatedResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

    private CloudSubNetwork createSubnetworkObject(CloudSubnetCreateRequest request) {
        CloudSubNetwork subnet = new CloudSubNetwork();
        subnet.setAccountNumber(request.getAccountNumber());
        subnet.setAvailableIPCount(Integer.parseInt(request.getAvailableIPCount()));
        subnet.setDefaultForZone(Boolean.parseBoolean(request.getDefaultForZone()));
        subnet.setState(request.getState());
        subnet.setVirtualNetworkId(request.getVirtualNetworkId());
        subnet.setZone(request.getZone());
        subnet.setCidrBlock(request.getCidrBlock());
        subnet.setCidrCount(Integer.parseInt(request.getCidrCount()));
        subnet.setCreationDate(Long.parseLong(request.getCreationDate()));
        subnet.setLastUpdateDate(Long.parseLong(request.getLastUpdateDate()));
        subnet.setSubnetId(request.getSubnetId());
        subnet.setUsedIPCount(Integer.parseInt(request.getUsedIPCount()));
        subnet.getTags().addAll(request.getTags());
        subnet.setIpUsage(request.getIpUsage());
        subnet.setSubscribedIPCount(Integer.parseInt(request.getSubscribedIPCount()));
        subnet.setSubscribedIPUsage(request.getSubscribedIPUsage());
        return subnet;
    }

    private CloudSubNetwork updateSubnetworkObject(CloudSubnetCreateRequest request, CloudSubNetwork existing) {
        if (request.getAccountNumber() != null) existing.setAccountNumber(request.getAccountNumber());
        if (request.getAvailableIPCount() != null)
            existing.setAvailableIPCount(Integer.parseInt(request.getAvailableIPCount()));
        if (request.getDefaultForZone() != null)
            existing.setDefaultForZone(Boolean.parseBoolean(request.getDefaultForZone()));
        if (request.getState() != null) existing.setState(request.getState());
        if (request.getVirtualNetworkId() != null) existing.setVirtualNetworkId(request.getVirtualNetworkId());
        if (request.getZone() != null) existing.setZone(request.getZone());
        if (request.getCidrBlock() != null) existing.setCidrBlock(request.getCidrBlock());
        if (request.getCidrCount() != null) existing.setCidrCount(Integer.parseInt(request.getCidrCount()));
        if (request.getCreationDate() != null) existing.setCreationDate(Long.parseLong(request.getCreationDate()));
        if (request.getLastUpdateDate() != null)
            existing.setLastUpdateDate(Long.parseLong(request.getLastUpdateDate()));
        if (request.getSubnetId() != null) existing.setSubnetId(request.getSubnetId());
        if (request.getUsedIPCount() != null) existing.setUsedIPCount(Integer.parseInt(request.getUsedIPCount()));
        if (request.getIpUsage() != null) existing.setIpUsage(request.getIpUsage());
        if (request.getSubscribedIPCount() != null) existing.setSubscribedIPCount(Integer.parseInt(request.getSubscribedIPCount()));
        if (request.getSubscribedIPUsage() != null) existing.setSubscribedIPUsage(request.getSubscribedIPUsage());
        if (!CollectionUtils.isEmpty(request.getTags())) {
            existing.getTags().clear();
            existing.getTags().addAll(request.getTags());
        }
        existing.getTags().addAll(request.getTags());

        return existing;
    }

    @Override
    public List<String> upsertSubNetwork(List<CloudSubnetCreateRequest> subnets) {
        List<String> objectIds = new ArrayList<>();
        for (CloudSubnetCreateRequest csn : subnets) {
            CloudSubNetwork existing = getSubNetworkDetailsBySubnetId(csn.getSubnetId());
            if (existing == null) {
                CloudSubNetwork sn = cloudSubNetworkRepository.save(createSubnetworkObject(csn));
                objectIds.add(sn.getId().toString());
            } else {
                cloudSubNetworkRepository.save(updateSubnetworkObject(csn, existing));
                objectIds.add(existing.getId().toString());
            }
        }
        return objectIds;
    }

    @Override
    public Collection<CloudSubNetwork> getSubNetworkDetailsByComponentId(String componentIdString) {
        return getSubNetworkDetails(getCollectorItem(new ObjectId(componentIdString)));
    }

    @Override
    public Collection<CloudSubNetwork> getSubNetworkDetailsByAccount(String accountNumber) {
        return cloudSubNetworkRepository.findByAccountNumber(accountNumber);
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
