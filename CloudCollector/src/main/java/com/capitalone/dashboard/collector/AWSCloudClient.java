package com.capitalone.dashboard.collector; 

import java.util.List;

import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.ec2.model.Instance;
import com.capitalone.dashboard.model.AWSConfig;
import com.capitalone.dashboard.model.CloudComputeRawData;

/**
 * Client for fetching commit history from GitHub
 */
public interface AWSCloudClient {

    /**
     * Fetch all of the commits for the provided SubversionRepo.
     * @param cwClient 
     * @param accessKey 
     *
     * @param client, the client for EC2
     * @param cwClient, the client for CloudWatch
     * @return a collection of objects with metrics for each instance
     */

  CloudComputeRawData getMetrics(Instance currInstance, AmazonCloudWatchClient cwClient, String accessKey);

  List<Instance> getInstances (AWSConfig config);
  
}

