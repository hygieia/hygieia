package com.capitalone.dashboard.service;

import com.capitalone.dashboard.config.collector.CloudConfig;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CloudInstanceHistoryRepository;
import com.capitalone.dashboard.repository.CloudInstanceRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.CloudInstanceCreateRequest;
import com.capitalone.dashboard.request.CloudInstanceListRefreshRequest;
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
        if (CollectionUtils.isEmpty(request.getInstanceIds()) || CollectionUtils.isEmpty(existing)) return new ArrayList<>();

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

    private CloudInstance createCloudInstanceObject (CloudInstanceCreateRequest request) {
        CloudInstance instance = new CloudInstance();
        instance.setAccountNumber(request.getAccountNumber());
        instance.setRootDeviceName(request.getRootDeviceName());
        instance.setCpuUtilization(request.getCpuUtilization());
        instance.setVirtualNetworkId(request.getVirtualNetworkId());
        instance.setSubnetId(request.getSubnetId());
        instance.setStatus(request.getStatus());
        instance.setAge(request.getAge());
        instance.setDiskRead(request.getDiskRead());
        instance.setDiskWrite(request.getDiskWrite());
        instance.setImageApproved(request.isImageApproved());
        instance.setImageId(request.getImageId());
        instance.setImageExpirationDate(request.getImageExpirationDate());
        instance.setInstanceId(request.getInstanceId());
        instance.setInstanceOwner(request.getInstanceOwner());
        instance.setInstanceType(request.getInstanceType());
        instance.setLastAction(request.getLastAction());
        instance.setMonitored(request.isMonitored());
        instance.setNetworkIn(request.getNetworkIn());
        instance.setNetworkOut(request.getNetworkOut());
        instance.setLastUpdatedDate(request.getLastUpdatedDate());
        instance.setPrivateDns(request.getPrivateDns());
        instance.setPublicIp(request.getPublicIp());
        instance.setStopped(request.isStopped());
        instance.setTagged(request.isTagged());
        instance.getTags().addAll(request.getTags());
        instance.getSecurityGroups().addAll(request.getSecurityGroups());
        return instance;
    }

    @Override
    public List<String> upsertInstance(List<CloudInstanceCreateRequest> instances) {
        List<String> objectIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(instances))
        for (CloudInstanceCreateRequest ci : instances) {
            CloudInstance newObject = createCloudInstanceObject(ci);
            CloudInstance existing = cloudInstanceRepository.findByInstanceId(ci.getInstanceId());
            if (existing == null) {
                CloudInstance in = cloudInstanceRepository.save(newObject);
                objectIds.add(in.getId().toString());
            } else {
                try {
                    HygieiaUtils.mergeObjects(existing, newObject);
                    cloudInstanceRepository.save(existing);
                    objectIds.add(existing.getId().toString());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.error("Error saving cloud instance info for instanceID: " + ci.getInstanceId(), e);
                }
            }
        }
        return objectIds;
    }
}
