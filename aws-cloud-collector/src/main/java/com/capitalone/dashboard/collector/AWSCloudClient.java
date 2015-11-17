package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.AWSConfig;
import com.capitalone.dashboard.model.CloudComputeData;
import com.capitalone.dashboard.model.CloudStorageData;


public interface AWSCloudClient {

	/**
	 *
	 */
	CloudComputeData getCloudComputeData(AWSConfig config);
	CloudStorageData getCloudStorageData(AWSConfig config);
}
