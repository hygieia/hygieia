package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.CloudVolumeStorage;
import com.capitalone.dashboard.model.NameValue;
import com.capitalone.dashboard.request.CloudVolumeCreateRequest;
import com.capitalone.dashboard.request.CloudVolumeListRefreshRequest;
import com.capitalone.dashboard.response.CloudVolumeAggregatedResponse;

import java.util.Collection;
import java.util.List;

public interface CloudVolumeService {
    //Volume Services

    Collection<String> refreshVolumes(CloudVolumeListRefreshRequest request);

    //Volume Upsert
    List<String> upsertVolume(List<CloudVolumeCreateRequest> volume);

    /**
     *     Volume Details by
     *          (a) componentId - for UI mostly
     *          (b) volumeId
     *          (c) List of volume Ids
     *          (d) List of Tags
     *          (e) Account Number
     */
    Collection<CloudVolumeStorage> getVolumeDetailsByComponentId(String componentId);
    Collection<CloudVolumeStorage> getVolumeDetailsByVolumeIds(List<String> volumeId);
    Collection<CloudVolumeStorage> getVolumeDetailsByTags(List<NameValue> tags);
    Collection<CloudVolumeStorage> getVolumeDetailsByAccount(String accountNumber);


    /**
     *     Volume Aggregated Data by
     *          (a) componentId - for UI mostly
     *          (b) Custom request object
     *          (d) List of Tags
     */
    CloudVolumeAggregatedResponse getVolumeAggregatedData(String componentId);

    Collection<CloudVolumeStorage> getVolumeDetailsByInstanceIds(List<String> instanceIds);
}
