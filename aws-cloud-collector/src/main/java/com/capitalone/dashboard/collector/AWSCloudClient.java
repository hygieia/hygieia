package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CloudInstance;
import com.capitalone.dashboard.model.CloudSubNetwork;
import com.capitalone.dashboard.model.CloudVirtualNetwork;
import com.capitalone.dashboard.model.CloudVolumeStorage;
import com.capitalone.dashboard.repository.CloudInstanceRepository;
import com.capitalone.dashboard.repository.CloudSubNetworkRepository;
import com.capitalone.dashboard.repository.CloudVirtualNetworkRepository;

import java.util.List;
import java.util.Map;


public interface AWSCloudClient {

	/**
	 *
	 */
	Map<String, List<CloudInstance>> getCloundInstances(CloudInstanceRepository repository);
    CloudVirtualNetwork getCloudVPC(CloudVirtualNetworkRepository repository);
    CloudSubNetwork getCloudSubnet(CloudSubNetworkRepository repository);
    Map<String, List<CloudVolumeStorage>> getCloudVolumes();
    Double get24HourInstanceEstimatedCharge();

}
