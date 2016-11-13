package com.capitalone.dashboard.service;

import com.capitalone.dashboard.config.collector.CloudConfig;
import com.capitalone.dashboard.misc.HygieiaException;
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
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CloudInstanceServiceImpl implements CloudInstanceService {

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


    private CloudInstance upsertCloudInstanceObject(CloudInstanceCreateRequest request, CloudInstance existing) throws HygieiaException {

        CloudInstance instance = (existing == null) ? new CloudInstance() : existing;
        if (StringUtils.isEmpty(request.getAccountNumber()) || (StringUtils.isEmpty(request.getInstanceId()))) {
            throw new HygieiaException("Missing required fields (account number, instance id). ", HygieiaException.ERROR_INSERTING_DATA);
        }

        instance.setAccountNumber(request.getAccountNumber());
        instance.setInstanceId(request.getInstanceId());

        //Anything null or resulting in parsing error will be thrown back to caller.
        setRootDeviceName(request, instance);
        setCpuUtilization(request, instance);
        setVirtualNetworkId(request, instance);
        setSubnetId(request, instance);
        setStatus(request, instance);
        setAge(request, instance);
        setDiskRead(request, instance);
        setDiskWrite(request, instance);
        setImageApproved(request, instance);
        setImageId(request, instance);
        setImageExpirationDate(request, instance);

        setInstanceOwner(request, instance);
        setInstanceType(request, instance);
        setLastAction(request, instance);
        setIsMonitored(request, instance);
        setNetworkIn(request, instance);
        setNetworkOut(request, instance);
        setLastUpdatedDate(request, instance);
        setPrivateDns(request, instance);
        setPublicIp(request, instance);
        setIsStopped(request, instance);
        setIsTagged(request, instance);
        setAutoScaleName(request, instance);
        setTags(request, instance);
        setSecurityGroups(request, instance);
        return instance;
    }

    private void setSecurityGroups(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (!CollectionUtils.isEmpty(request.getSecurityGroups())) {
            instance.getSecurityGroups().clear();
            instance.getSecurityGroups().addAll(request.getSecurityGroups());
        }
    }

    private void setTags(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (!CollectionUtils.isEmpty(request.getTags())) {
            instance.getTags().clear();
            instance.getTags().addAll(request.getTags());
        }
    }

    private void setAutoScaleName(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (request.getAutoScaleName() != null) instance.setAutoScaleName(request.getAutoScaleName());
    }

    private void setIsTagged(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (request.getIsTagged() != null) instance.setIsTagged(Boolean.parseBoolean(request.getIsTagged()));
    }

    private void setIsStopped(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (request.getIsStopped() != null) instance.setIsStopped(Boolean.parseBoolean(request.getIsStopped()));
    }

    private void setPublicIp(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (request.getPublicIp() != null) instance.setPublicIp(request.getPublicIp());
    }

    private void setPrivateDns(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (request.getPrivateDns() != null) instance.setPrivateDns(request.getPrivateDns());
    }

    private void setLastUpdatedDate(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (request.getLastUpdatedDate() != null)
            instance.setLastUpdatedDate(Long.parseLong(request.getLastUpdatedDate()));
    }

    private void setNetworkOut(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (request.getNetworkOut() != null) instance.setNetworkOut(Double.parseDouble(request.getNetworkOut()));
    }

    private void setNetworkIn(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (request.getNetworkIn() != null) instance.setNetworkIn(Double.parseDouble(request.getNetworkIn()));
    }

    private void setIsMonitored(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (request.getIsMonitored() != null) instance.setIsMonitored(Boolean.parseBoolean(request.getIsMonitored()));
    }

    private void setLastAction(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (request.getLastAction() != null) instance.setLastAction(request.getLastAction());
    }

    private void setInstanceType(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (request.getInstanceType() != null) instance.setInstanceType(request.getInstanceType());
    }

    private void setInstanceOwner(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (request.getInstanceOwner() != null) instance.setInstanceOwner(request.getInstanceOwner());
    }

    private void setImageExpirationDate(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (request.getImageExpirationDate() != null)
            instance.setImageExpirationDate(Long.parseLong(request.getImageExpirationDate()));
    }

    private void setImageId(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (request.getImageId() != null) instance.setImageId(request.getImageId());
    }

    private void setImageApproved(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (request.getImageApproved() != null)
            instance.setImageApproved(Boolean.parseBoolean(request.getImageApproved()));
    }

    private void setDiskWrite(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (request.getDiskWrite() != null) instance.setDiskWrite(Double.parseDouble(request.getDiskWrite()));
    }

    private void setDiskRead(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (request.getDiskRead() != null) instance.setDiskRead(Double.parseDouble(request.getDiskRead()));
    }

    private void setAge(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (request.getAge() != null) instance.setAge(Integer.parseInt(request.getAge()));
    }

    private void setStatus(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (request.getStatus() != null) instance.setStatus(request.getStatus());
    }

    private void setSubnetId(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (request.getSubnetId() != null) instance.setSubnetId(request.getSubnetId());
    }

    private void setVirtualNetworkId(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (request.getVirtualNetworkId() != null) instance.setVirtualNetworkId(request.getVirtualNetworkId());
    }

    private void setCpuUtilization(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (request.getCpuUtilization() != null)
            instance.setCpuUtilization(Double.parseDouble(request.getCpuUtilization()));
    }

    private void setRootDeviceName(CloudInstanceCreateRequest request, CloudInstance instance) {
        if (request.getRootDeviceName() != null) instance.setRootDeviceName(request.getRootDeviceName());
    }


    @Override
    public List<String> upsertInstance(List<CloudInstanceCreateRequest> instances) throws HygieiaException {
        List<String> objectIds = new ArrayList<>();
        if (CollectionUtils.isEmpty(instances)) return objectIds;
        for (CloudInstanceCreateRequest cir : instances) {
            CloudInstance existing = cloudInstanceRepository.findByInstanceId(cir.getInstanceId());
            CloudInstance upsertObject = cloudInstanceRepository.save(upsertCloudInstanceObject(cir, existing));
            objectIds.add(upsertObject.getId().toString());
        }
        return objectIds;
    }
}
