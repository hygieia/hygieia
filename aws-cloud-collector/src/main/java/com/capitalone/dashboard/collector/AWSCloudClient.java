package com.capitalone.dashboard.collector;

import java.util.HashMap;
import java.util.List;

import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Volume;
import com.capitalone.dashboard.model.AWSConfig;
import com.capitalone.dashboard.model.CloudComputeData;
import com.capitalone.dashboard.model.CloudComputeInstanceData;
import com.capitalone.dashboard.model.CloudStorageData;


public interface AWSCloudClient {

	/**
	 *
	 */
	CloudComputeData getCloudComputeData(AWSConfig config);
	CloudStorageData getCloudStorageData(AWSConfig config);
}
