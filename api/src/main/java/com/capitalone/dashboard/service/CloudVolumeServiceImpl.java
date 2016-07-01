package com.capitalone.dashboard.service;

import com.capitalone.dashboard.config.collector.CloudConfig;
import com.capitalone.dashboard.model.CloudVolumeStorage;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.NameValue;
import com.capitalone.dashboard.repository.CloudVolumeRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.CloudVolumeCreateRequest;
import com.capitalone.dashboard.request.CloudVolumeListRefreshRequest;
import com.capitalone.dashboard.response.CloudVolumeAggregatedResponse;
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
public class CloudVolumeServiceImpl implements CloudVolumeService {
    private static final Log LOGGER = LogFactory
            .getLog(CloudVolumeServiceImpl.class);

    private final CloudVolumeRepository cloudVolumeRepository;
    private final ComponentRepository componentRepository;

    @Autowired
    public CloudVolumeServiceImpl(CloudVolumeRepository cloudVolumeRepository,
                                  ComponentRepository cloudConfigRepository) {
        this.cloudVolumeRepository = cloudVolumeRepository;
        this.componentRepository = cloudConfigRepository;
    }

    public Collection<CloudVolumeStorage> getVolumeDetails(CollectorItem item) {
        Collection<CloudVolumeStorage> volumes = new HashSet<>();
        if ((item != null) && (item instanceof CloudConfig)) {
            CloudConfig config = (CloudConfig) item;
            volumes.addAll(getVolumeDetailsByTags(config.getTags()));
        }
        return volumes;
    }

    private CollectorItem getCollectorItem(ObjectId componentId) {
        Component component = componentRepository.findOne(componentId);
        if (CollectionUtils.isEmpty(component.getCollectorItems())) return null;
        return component.getCollectorItems().get(CollectorType.Cloud).get(0);
    }

    @Override
    public Collection<CloudVolumeStorage> getVolumeDetailsByComponentId(String componentIdString) {
        return getVolumeDetails(getCollectorItem(new ObjectId(componentIdString)));
    }

    @Override
    public Collection<CloudVolumeStorage> getVolumeDetailsByVolumeIds(List<String> volumeIds) {
        return cloudVolumeRepository.findByVolumeIdIn(volumeIds);
    }

    @Override
    public Collection<CloudVolumeStorage> getVolumeDetailsByTags(List<NameValue> tags) {
        Set<CloudVolumeStorage> volumes = new HashSet<>();
        for (NameValue nv : tags) {
            volumes.addAll(cloudVolumeRepository.findByTagNameAndValue(nv.getName(), nv.getValue()));
        }
        return volumes;
    }

    @Override
    public Collection<CloudVolumeStorage> getVolumeDetailsByAccount(String accountNumber) {
        return cloudVolumeRepository.findByAccountNumber(accountNumber);
    }

    @Override
    public CloudVolumeAggregatedResponse getVolumeAggregatedData(String componentIdString) {
        CollectorItem item = getCollectorItem(new ObjectId(componentIdString));
        CloudVolumeAggregatedResponse response = new CloudVolumeAggregatedResponse();
        Collection<CloudVolumeStorage> volumes = getVolumeDetails(item);
        if ((item != null) && !(item instanceof CloudConfig)) return response;
        CloudConfig config = (CloudConfig) item;
        if (CollectionUtils.isEmpty(volumes)) return response;
        return aggregate(volumes, config);
    }

    @Override
    public Collection<CloudVolumeStorage> getVolumeDetailsByInstanceIds(List<String> attachInstances) {
        return cloudVolumeRepository.findByAttachInstancesIn(attachInstances);
    }


    @Override
    public Collection<String> refreshVolumes(CloudVolumeListRefreshRequest request) {
        Collection<CloudVolumeStorage> existing = cloudVolumeRepository.findByAccountNumber(request.getAccountNumber());
        Set<CloudVolumeStorage> toDelete = new HashSet<>();
        Set<String> deletedIds = new HashSet<>();
        if (CollectionUtils.isEmpty(request.getVolumeIds()) || CollectionUtils.isEmpty(existing))
            return new ArrayList<>();

        for (CloudVolumeStorage ci : existing) {
            if (!request.getVolumeIds().contains(ci.getVolumeId())) {
                toDelete.add(ci);
                deletedIds.add(ci.getVolumeId());
            }
        }
        if (CollectionUtils.isEmpty(toDelete)) {
            cloudVolumeRepository.delete(toDelete);
        }
        return deletedIds;
    }

    private CloudVolumeStorage createCloudVolumeObject(CloudVolumeCreateRequest request) {
        CloudVolumeStorage volume = new CloudVolumeStorage();
        volume.setAccountNumber(request.getAccountNumber());
        volume.setStatus(request.getStatus());
        volume.setEncrypted(request.isEncrypted());
        volume.setVolumeId(request.getVolumeId());
        volume.getTags().addAll(request.getTags());
        return volume;
    }

    @Override
    public List<String> upsertVolume(List<CloudVolumeCreateRequest> volumes) {
        List<String> objectIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(volumes))
            for (CloudVolumeCreateRequest ci : volumes) {
                CloudVolumeStorage newObject = createCloudVolumeObject(ci);
                CloudVolumeStorage existing = cloudVolumeRepository.findByVolumeId(ci.getVolumeId());
                if (existing == null) {
                    CloudVolumeStorage in = cloudVolumeRepository.save(newObject);
                    objectIds.add(in.getId().toString());
                } else {
                    try {
                        HygieiaUtils.mergeObjects(existing, newObject);
                        //Copy ArrayLists manually
                        if (!CollectionUtils.isEmpty(newObject.getTags())) {
                            existing.getTags().clear();
                            existing.getTags().addAll(newObject.getTags());
                        }
                        if (!CollectionUtils.isEmpty(newObject.getAttachInstances())) {
                            existing.getAttachInstances().clear();
                            existing.getAttachInstances().addAll(newObject.getAttachInstances());
                        }
                        cloudVolumeRepository.save(existing);
                        objectIds.add(existing.getId().toString());
                    } catch (IllegalAccessException | InvocationTargetException e) {
                    	LOGGER.error("Error saving cloud volume info for volumeID: " + ci.getVolumeId(), e);
                    }
                }
            }
        return objectIds;
    }

    private CloudVolumeAggregatedResponse aggregate(Collection<CloudVolumeStorage> volumes, CloudConfig config) {
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
        CloudVolumeAggregatedResponse response = new CloudVolumeAggregatedResponse();
        for (CloudVolumeStorage rd : volumes) {
            totalCount = totalCount + 1;
        }
        return response;
    }
}
