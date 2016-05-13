package com.capitalone.dashboard.service;

import com.capitalone.dashboard.config.collector.CloudConfig;
import com.capitalone.dashboard.model.CloudInstance;
import com.capitalone.dashboard.model.CloudInstanceHistory;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.NameValue;
import com.capitalone.dashboard.repository.CloudInstanceHistoryRepository;
import com.capitalone.dashboard.repository.CloudInstanceRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.CloudInstanceCreateRequest;
import com.capitalone.dashboard.request.CloudInstanceListRefreshRequest;
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
public class CloudInstanceServiceImpl implements CloudInstanceService {
    private static final Log logger = LogFactory
            .getLog(CloudInstanceServiceImpl.class);

    private final CloudInstanceRepository cloudInstanceRepository;
    private final CloudInstanceHistoryRepository cloudInstanceHistoryRepository;
    private final ComponentRepository componentRepository;

    @Autowired
    public CloudInstanceServiceImpl(CloudInstanceRepository cloudInstanceRepository,
                                    CloudInstanceHistoryRepository cloudInstanceHistoryRepository,
                                    ComponentRepository cloudConfigRepository) {
        this.cloudInstanceRepository = cloudInstanceRepository;
        this.cloudInstanceHistoryRepository = cloudInstanceHistoryRepository;
        this.componentRepository = cloudConfigRepository;
    }

    public Collection<CloudInstance> getInstanceDetails(CollectorItem item) {
        Collection<CloudInstance> instances = new HashSet<>();
        if ((item != null) && (item instanceof CloudConfig)) {
            CloudConfig config = (CloudConfig) item;
            instances.addAll(getInstanceDetailsByTags(config.getTags()));
        }
        return instances;
    }

    private CollectorItem getCollectorItem(ObjectId componentId) {
        Component component = componentRepository.findOne(componentId);
        if (CollectionUtils.isEmpty(component.getCollectorItems())) return null;
        return component.getCollectorItems().get(CollectorType.Cloud).get(0);
    }

    @Override
    public Collection<CloudInstance> getInstanceDetailsByComponentId(String componentIdString) {
        return getInstanceDetails(getCollectorItem(new ObjectId(componentIdString)));
    }

    @Override
    public CloudInstance getInstanceDetailsByInstanceId(String instanceId) {
        return cloudInstanceRepository.findByInstanceId(instanceId);
    }

    @Override
    public Collection<CloudInstance> getInstanceDetailsByInstanceIds(List<String> instanceIds) {
        return cloudInstanceRepository.findByInstanceIdIn(instanceIds);
    }

    @Override
    public Collection<CloudInstance> getInstanceDetailsByTags(List<NameValue> tags) {
        Set<CloudInstance> instances = new HashSet<>();
        for (NameValue nv : tags) {
            instances.addAll(cloudInstanceRepository.findByTagNameAndValue(nv.getName(), nv.getValue()));
        }
        return instances;
    }

    @Override
    public Collection<CloudInstance> getInstanceDetailsByAccount(String accountNumber) {
        return cloudInstanceRepository.findByAccountNumber(accountNumber);
    }


    @Override
    public Collection<CloudInstanceHistory> getInstanceHistoryByAccount(String account) {
        return cloudInstanceHistoryRepository.findByAccountNumber(account);
    }

    @Override
    public Collection<String> refreshInstances(CloudInstanceListRefreshRequest request) {
        Collection<CloudInstance> existing = cloudInstanceRepository.findByAccountNumber(request.getAccountNumber());
        Set<CloudInstance> toDelete = new HashSet<>();
        Set<String> deletedIds = new HashSet<>();
        if (CollectionUtils.isEmpty(request.getInstanceIds()) || CollectionUtils.isEmpty(existing))
            return new ArrayList<>();

        for (CloudInstance ci : existing) {
            if (!request.getInstanceIds().contains(ci.getInstanceId())) {
                toDelete.add(ci);
                deletedIds.add(ci.getInstanceId());
            }
        }
        if (CollectionUtils.isEmpty(toDelete)) {
            cloudInstanceRepository.delete(toDelete);
        }
        return deletedIds;
    }

    private CloudInstance createCloudInstanceObject(CloudInstanceCreateRequest request) {
        //Anything null or resulting in parsing error will be thrown back to caller.
        CloudInstance instance = new CloudInstance();
        instance.setAccountNumber(request.getAccountNumber());
        instance.setRootDeviceName(request.getRootDeviceName());
        instance.setCpuUtilization(Double.parseDouble(request.getCpuUtilization()));
        instance.setVirtualNetworkId(request.getVirtualNetworkId());
        instance.setSubnetId(request.getSubnetId());
        instance.setStatus(request.getStatus());
        instance.setAge(Integer.parseInt(request.getAge()));
        instance.setDiskRead(Double.parseDouble(request.getDiskRead()));
        instance.setDiskWrite(Double.parseDouble(request.getDiskWrite()));
        instance.setImageApproved(Boolean.parseBoolean(request.getImageApproved()));
        instance.setImageId(request.getImageId());
        instance.setImageExpirationDate(Long.parseLong(request.getImageExpirationDate()));
        instance.setInstanceId(request.getInstanceId());
        instance.setInstanceOwner(request.getInstanceOwner());
        instance.setInstanceType(request.getInstanceType());
        instance.setLastAction(request.getLastAction());
        instance.setMonitored(Boolean.parseBoolean(request.getIsMonitored()));
        instance.setNetworkIn(Double.parseDouble(request.getNetworkIn()));
        instance.setNetworkOut(Double.parseDouble(request.getNetworkOut()));
        instance.setLastUpdatedDate(Long.parseLong(request.getLastUpdatedDate()));
        instance.setPrivateDns(request.getPrivateDns());
        instance.setPublicIp(request.getPublicIp());
        instance.setStopped(Boolean.parseBoolean(request.getIsStopped()));
        instance.setTagged(Boolean.parseBoolean(request.getIsTagged()));
        instance.getTags().addAll(request.getTags());
        instance.getSecurityGroups().addAll(request.getSecurityGroups());
        instance.setAutoScaleName(request.getAutoScaleName());
        return instance;
    }


    private CloudInstance updateCloudInstanceObject(CloudInstanceCreateRequest request, CloudInstance existing) {
        //Anything null or resulting in parsing error will be thrown back to caller.
        if (request.getAccountNumber() != null) existing.setAccountNumber(request.getAccountNumber());
        if (request.getRootDeviceName() != null) existing.setRootDeviceName(request.getRootDeviceName());
        if (request.getCpuUtilization() != null)
            existing.setCpuUtilization(Double.parseDouble(request.getCpuUtilization()));
        if (request.getVirtualNetworkId() != null) existing.setVirtualNetworkId(request.getVirtualNetworkId());
        if (request.getSubnetId() != null) existing.setSubnetId(request.getSubnetId());
        if (request.getStatus() != null) existing.setStatus(request.getStatus());
        if (request.getAge() != null) existing.setAge(Integer.parseInt(request.getAge()));
        if (request.getDiskRead() != null) existing.setDiskRead(Double.parseDouble(request.getDiskRead()));
        if (request.getDiskWrite() != null) existing.setDiskWrite(Double.parseDouble(request.getDiskWrite()));
        if (request.getImageApproved() != null)
            existing.setImageApproved(Boolean.parseBoolean(request.getImageApproved()));
        if (request.getImageId() != null) existing.setImageId(request.getImageId());
        if (request.getImageExpirationDate() != null)
            existing.setImageExpirationDate(Long.parseLong(request.getImageExpirationDate()));
        if (request.getInstanceId() != null) existing.setInstanceId(request.getInstanceId());
        if (request.getInstanceOwner() != null) existing.setInstanceOwner(request.getInstanceOwner());
        if (request.getInstanceType() != null) existing.setInstanceType(request.getInstanceType());
        if (request.getLastAction() != null) existing.setLastAction(request.getLastAction());
        if (request.getIsMonitored() != null) existing.setMonitored(Boolean.parseBoolean(request.getIsMonitored()));
        if (request.getNetworkIn() != null) existing.setNetworkIn(Double.parseDouble(request.getNetworkIn()));
        if (request.getNetworkOut() != null) existing.setNetworkOut(Double.parseDouble(request.getNetworkOut()));
        if (request.getLastUpdatedDate() != null)
            existing.setLastUpdatedDate(Long.parseLong(request.getLastUpdatedDate()));
        if (request.getPrivateDns() != null) existing.setPrivateDns(request.getPrivateDns());
        if (request.getPublicIp() != null) existing.setPublicIp(request.getPublicIp());
        if (request.getIsStopped() != null) existing.setStopped(Boolean.parseBoolean(request.getIsStopped()));
        if (request.getIsTagged() != null) existing.setTagged(Boolean.parseBoolean(request.getIsTagged()));
        if (request.getAutoScaleName() != null) existing.setAutoScaleName(request.getAutoScaleName());
        if (!CollectionUtils.isEmpty(request.getTags())) {
            existing.getTags().clear();
            existing.getTags().addAll(request.getTags());
        }
        if (!CollectionUtils.isEmpty(request.getSecurityGroups())) {
            existing.getSecurityGroups().clear();
            existing.getSecurityGroups().addAll(request.getSecurityGroups());
        }
        return existing;
    }


    @Override
    public List<String> upsertInstance(List<CloudInstanceCreateRequest> instances) {
        List<String> objectIds = new ArrayList<>();
        if (CollectionUtils.isEmpty(instances)) return objectIds;
        for (CloudInstanceCreateRequest cir : instances) {
            CloudInstance existing = cloudInstanceRepository.findByInstanceId(cir.getInstanceId());
            if (existing == null) {
                CloudInstance newObject = createCloudInstanceObject(cir);
                CloudInstance in = cloudInstanceRepository.save(newObject);
                objectIds.add(in.getId().toString());
            } else {
                CloudInstance updated = updateCloudInstanceObject(cir, existing);
                cloudInstanceRepository.save(updated);
                objectIds.add(existing.getId().toString());
            }
        }
        return objectIds;
    }
}
