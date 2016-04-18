package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CloudInstance;
import com.capitalone.dashboard.model.CloudSubNetwork;
import com.capitalone.dashboard.model.CloudVirtualNetwork;
import com.capitalone.dashboard.repository.CloudInstanceRepository;
import com.capitalone.dashboard.repository.CloudSubNetworkRepository;
import com.capitalone.dashboard.repository.CloudVirtualNetworkRepository;

import java.util.List;


public interface AWSCloudClient {

	/**
	 *
	 */
	List<CloudInstance> getCloundInstances(CloudInstanceRepository repository);
    CloudVirtualNetwork getCloudVPC(CloudVirtualNetworkRepository repository);
    CloudSubNetwork getCloudSubnet(CloudSubNetworkRepository repository);


}
