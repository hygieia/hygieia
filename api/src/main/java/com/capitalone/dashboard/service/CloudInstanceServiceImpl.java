package com.capitalone.dashboard.service;

import com.capitalone.dashboard.config.collector.CloudConfig;
import com.capitalone.dashboard.model.CloudInstance;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.NameValue;
import com.capitalone.dashboard.repository.CloudInstanceRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.CloudInstanceListRefreshRequest;
import com.capitalone.dashboard.response.CloudInstanceAggregatedResponse;
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
public class CloudInstanceServiceImpl implements CloudInstanceService {
    private static final Log logger = LogFactory
            .getLog(CloudInstanceServiceImpl.class);

    private final CloudInstanceRepository cloudInstanceRepository;
    private final ComponentRepository componentRepository;

    @Autowired
    public CloudInstanceServiceImpl(CloudInstanceRepository cloudInstanceRepository,
                                    ComponentRepository cloudConfigRepository) {
        this.cloudInstanceRepository = cloudInstanceRepository;
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
    public Collection<CloudInstance> getInstanceDetails(ObjectId componentId) {
        return getInstanceDetails(getCollectorItem(componentId));
    }

    @Override
    public CloudInstance getInstanceDetails(String instanceId) {
        return cloudInstanceRepository.findByInstanceId(instanceId);
    }

    @Override
    public Collection<CloudInstance> getInstanceDetails(List<String> instanceIds) {
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
    public CloudInstanceAggregatedResponse getInstanceAggregatedData(ObjectId componentId) {
        CollectorItem item = getCollectorItem(componentId);
        CloudInstanceAggregatedResponse response = new CloudInstanceAggregatedResponse();
        Collection<CloudInstance> instances = getInstanceDetails(item);
        if ((item != null) && !(item instanceof CloudConfig)) return response;
        CloudConfig config = (CloudConfig) item;
        if (CollectionUtils.isEmpty(instances)) return response;
        int ageAlertCount = 0;
        int ageErrorCount = 0;
        int ageGoodCount = 0;
        int cpuHighCount = 0;
        int cpuAlertCount = 0;
        int cpuLowCount = 0;
        int unEcryptedComputeCount = 0;
        int unTaggedCount = 0;
        int stoppedCount = 0;
        int totalCount = 0;
        /** For future enhancements
         double estimatedCharge = 0.0;
         int memoryHighCount = 0;
         int memoryAlertCount = 0;
         int memoryLowCount = 0;
         int diskHighCount = 0;
         int diskAlertCount = 0;
         int diskLowCount = 0;
         int networkHighCount = 0;
         int networkAlertCount = 0;
         int networkLowCount = 0;
         **/


        for (CloudInstance rd : instances) {
            totalCount = totalCount + 1;

            if (!rd.isEncrypted()) {
                unEcryptedComputeCount = unEcryptedComputeCount + 1;
            }
            if (rd.isStopped()) {
                stoppedCount = stoppedCount + 1;
            }
            if (!rd.isTagged()) {
                unTaggedCount = unTaggedCount + 1;
            }
            if (rd.getAge() >= config.getAgeError()) {
                ageErrorCount = ageErrorCount + 1;
            }
            if ((rd.getAge() < config.getAgeError()) && (rd.getAge() >= config.getAgeAlert())) {
                ageAlertCount = ageAlertCount + 1;
            }
            if (rd.getAge() < config.getAgeAlert()) {
                ageGoodCount = ageGoodCount + 1;
            }
            if (rd.getCpuUtilization() >= config.getCpuError()) {
                cpuHighCount = cpuHighCount + 1;
            }
            if ((rd.getCpuUtilization() < config.getCpuError()) && (rd.getCpuUtilization() >= config.getCpuAlert())) {
                cpuAlertCount = cpuAlertCount + 1;
            }
            if (rd.getCpuUtilization() < config.getCpuAlert()) {
                cpuLowCount = cpuLowCount + 1;
            }
        }
        response.setAgeAlert(ageAlertCount);
        response.setAgeError(ageErrorCount);
        response.setAgeGood(ageGoodCount);
        response.setCpuAlert(cpuAlertCount);
        response.setCpuHigh(cpuHighCount);
        response.setCpuLow(cpuLowCount);
        return response;
    }

    @Override
    public CloudInstanceAggregatedResponse getInstanceAggregatedData(List<String> instanceIds) {
        return null;
    }

    @Override
    public CloudInstanceAggregatedResponse getInstanceAggregatedDataByTags(List<NameValue> tags) {
        return null;
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

    @Override
    public List<ObjectId> upsertInstance(List<CloudInstance> instances) {
        List<ObjectId> objectIds = new ArrayList<>();
        if (CollectionUtils.isEmpty(instances))
        for (CloudInstance ci : instances) {
            CloudInstance existing = cloudInstanceRepository.findByInstanceId(ci.getInstanceId());
            if (existing == null) {
                CloudInstance in = cloudInstanceRepository.save(ci);
                objectIds.add(in.getId());
            } else {
                try {
                    HygieiaUtils.mergeObjects(existing, ci);
                    cloudInstanceRepository.save(existing);
                    objectIds.add(existing.getId());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.error("Error saving cloud instance info for instanceID: " + ci.getInstanceId(), e);
                }
            }
        }
        return objectIds;
    }


}
